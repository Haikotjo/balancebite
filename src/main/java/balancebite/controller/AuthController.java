package balancebite.controller;

import balancebite.dto.user.UserLoginInputDTO;
import balancebite.dto.user.UserRegistrationInputDTO;
import balancebite.security.*;
import balancebite.service.user.RegistrationService;
import balancebite.model.user.User;
import balancebite.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * AuthController handles user registration, login, and token management.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RegistrationService registrationService;
    private final LoginService loginService;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    /**
     * Constructs the AuthController with necessary dependencies.
     *
     * @param authenticationManager The Spring Security AuthenticationManager.
     * @param jwtService            The JWT service for token generation and validation.
     * @param registrationService   The service handling user registration logic.
     * @param loginService          The service handling user login logic.
     * @param userRepository        The repository to fetch user details by ID.
     * @param tokenBlacklistService        The repository to fetch user details by ID.
     */
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                          RegistrationService registrationService, LoginService loginService,
                          UserRepository userRepository, TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.registrationService = registrationService;
        this.loginService = loginService;
        this.userRepository = userRepository;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    /**
     * Handles user registration.
     * Accepts registration details, processes them, and returns a success or failure response.
     *
     * @param registrationDTO The DTO containing user registration details.
     * @return A ResponseEntity indicating success or failure.
     */
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody UserRegistrationInputDTO registrationDTO) {
        log.info("Processing user registration for email: {}", registrationDTO.getEmail());

        try {
            registrationService.registerUser(registrationDTO);

            Map<String, Object> response = Map.of(
                    "message", "User registered successfully!",
                    "email", registrationDTO.getEmail(),
                    "userName", registrationDTO.getUserName(),
                    "roles", registrationDTO.getRoles()
            );

            log.info("User registered successfully with email: {}", registrationDTO.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error during registration for email '{}': {}", registrationDTO.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Handles user login requests.
     * Authenticates the provided credentials using the LoginService and returns JWT tokens upon success.
     *
     * @param loginDTO The DTO containing user login credentials (email and password).
     * @return A ResponseEntity containing access and refresh tokens if login is successful,
     *         or an error message with status 401 (Unauthorized) if login fails.
     */
    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@Valid @RequestBody UserLoginInputDTO loginDTO) {
        log.info("Processing login for email: {}", loginDTO.getEmail());

        try {
            Map<String, String> tokens = loginService.login(loginDTO.getEmail(), loginDTO.getPassword());

            log.info("Login successful for email '{}'", loginDTO.getEmail());
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.get("accessToken"))
                    .body(Map.of(
                            "message", "Login successful",
                            "accessToken", tokens.get("accessToken"),
                            "refreshToken", tokens.get("refreshToken")
                    ));

        } catch (RuntimeException e) {
            log.warn("Login failed for email '{}': {}", loginDTO.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshAccessToken(@RequestBody Map<String, String> request) {
        log.info("Refreshing access token using provided refresh token.");

        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || !jwtService.validateRefreshToken(refreshToken)) {
            log.warn("Invalid or expired refresh token: {}",
                    refreshToken != null ? refreshToken.substring(0, 5) + "..." : "null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired refresh token"));
        }

        try {
            Long userId = jwtService.extractUserId(refreshToken);
            log.info("Extracted userId {} from refresh token.", userId);

            User user = userRepository.findById(userId).orElseThrow(() -> {
                log.warn("User with ID {} not found.", userId);
                return new UsernameNotFoundException("User not found");
            });

            MyUserDetails userDetails = new MyUserDetails(user);
            List<String> roles = userDetails.getRoles();
            String newAccessToken = jwtService.generateAccessToken(userDetails.getId(), roles);

            log.info("Access token refreshed successfully for userId {}. New token starts with: {}",
                    userId, newAccessToken.substring(0, 5) + "...");

            return ResponseEntity.ok(Map.of(
                    "message", "Access token refreshed successfully",
                    "accessToken", newAccessToken
            ));
        } catch (Exception e) {
            log.error("Error during token refresh:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Could not refresh access token"));
        }
    }



    /**
     * Handles user logout requests.
     * Invalidates the access token by adding it to the blacklist with an expiry time.
     *
     * @return ResponseEntity indicating successful logout.
     */
    @PostMapping("/logout")
    public ResponseEntity<Object> logoutUser(@RequestHeader("Authorization") String authHeader) {
        log.info("User requested logout.");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("No valid Authorization header provided.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "No token provided."));
        }

        String token = authHeader.substring(7); // Verwijder "Bearer " prefix

        // Voeg een log toe om te controleren of het token wordt ontvangen
        log.info("Token extracted from Authorization header: {}", token);

        try {
            jwtService.blacklistToken(token); // Voeg token toe aan de blacklist
            log.info("Token successfully blacklisted: {}", token);
        } catch (Exception e) {
            log.error("Error while blacklisting token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Could not blacklist token."));
        }

        SecurityContextHolder.clearContext(); // Wis de securitycontext
        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }

}

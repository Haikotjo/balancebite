package balancebite.controller;

import balancebite.dto.user.UserLoginInputDTO;
import balancebite.dto.user.UserRegistrationInputDTO;
import balancebite.service.user.RegistrationService;
import balancebite.security.JwtService;
import balancebite.security.LoginService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    /**
     * Constructs the AuthController with necessary dependencies.
     *
     * @param authenticationManager The Spring Security AuthenticationManager.
     * @param jwtService            The JWT service for token generation and validation.
     * @param registrationService   The service handling user registration logic.
     * @param loginService          The service handling user login logic.
     */
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                          RegistrationService registrationService, LoginService loginService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.registrationService = registrationService;
        this.loginService = loginService;
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
            // Use the registration service to register the user
            registrationService.registerUser(registrationDTO);

            log.info("User registered successfully with email: {}", registrationDTO.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registered successfully!"));
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

    /**
     * Refreshes the access token using a valid refresh token.
     *
     * @param refreshToken The refresh token provided by the client.
     * @return A ResponseEntity containing the new access token or an error message.
     */
    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshAccessToken(@RequestParam String refreshToken) {
        log.info("Refreshing access token using provided refresh token.");

        if (!jwtService.validateRefreshToken(refreshToken)) {
            log.warn("Invalid or expired refresh token.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired refresh token"));
        }

        String email = jwtService.extractEmail(refreshToken);
        UserDetails userDetails = registrationService.loadUserByEmail(email); // Here, use a method that retrieves user data
        String newAccessToken = jwtService.generateAccessToken(userDetails);

        log.info("Access token refreshed successfully for email: {}", email);

        return ResponseEntity.ok(Map.of(
                "message", "Access token refreshed successfully",
                "accessToken", newAccessToken
        ));
    }

    /**
     * Handles user logout requests.
     * Informs the client to clear tokens locally, as no server-side invalidation is used.
     *
     * @return ResponseEntity indicating successful logout.
     */
    @PostMapping("/logout")
    public ResponseEntity<Object> logoutUser() {
        log.info("User requested logout.");
        return ResponseEntity.ok(Map.of("message", "Logged out on this device."));
    }
}

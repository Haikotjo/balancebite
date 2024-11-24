package balancebite.controller;

import balancebite.dto.AuthDTO;
import balancebite.security.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * AuthController is responsible for handling authentication requests.
 * It manages user login and token generation.
 */
@RestController
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    /**
     * Constructs an AuthController with the given AuthenticationManager and JwtService.
     *
     * @param authManager the AuthenticationManager to use for authentication
     * @param jwtService  the JwtService to use for token generation
     */
    public AuthController(AuthenticationManager authManager, JwtService jwtService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    /**
     * Handles the sign-in request.
     * Authenticates the user with the provided credentials and generates a JWT token if successful.
     *
     * @param authDTO the authentication data transfer object containing the user's email and password
     * @return a ResponseEntity containing the JWT token if authentication is successful, or an error message if not
     */
    @PostMapping("/auth")
    public ResponseEntity<Object> signIn(@Valid @RequestBody AuthDTO authDTO) {
        log.info("Attempting to authenticate user with email: {}", authDTO.email);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authDTO.email, authDTO.password);

        try {
            Authentication auth = authManager.authenticate(authenticationToken);

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = jwtService.generateToken(userDetails);

            log.info("Authentication successful for user: {}", authDTO.email);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("message", "Authentication successful");
            responseBody.put("token", token);

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(responseBody);

        } catch (AuthenticationException ex) {
            log.warn("Authentication failed for user: {}. Reason: {}", authDTO.email, ex.getMessage());
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("error", "Authentication failed");
            responseBody.put("message", ex.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        }
    }
}
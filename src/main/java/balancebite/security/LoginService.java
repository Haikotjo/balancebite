package balancebite.security;

import balancebite.model.user.User;
import balancebite.repository.UserRepository;
import balancebite.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service responsible for handling user login and JWT generation.
 * This service checks if the user exists in the database, authenticates them,
 * and generates JWT tokens if authentication is successful.
 */
@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    /**
     * Constructor for LoginService.
     * Injects the necessary dependencies: AuthenticationManager, JwtService, and UserRepository.
     *
     * @param authenticationManager The AuthenticationManager to handle user authentication.
     * @param jwtService The JwtService to generate JWT tokens.
     * @param userRepository The UserRepository to check if a user exists in the database.
     */
    public LoginService(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    /**
     * Attempts to authenticate a user by their email and password and generates JWT tokens upon successful authentication.
     * If the user is not found or authentication fails (wrong password), it returns null.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     * @return A map containing the generated access and refresh tokens, or null if authentication fails.
     */
    public Map<String, String> login(String email, String password) {
        log.info("Attempting login for email: {}", email);

        // First, check if the email exists in the database
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            log.warn("Login failed for email '{}': User not found", email);
            return null;  // Return null if the user does not exist
        }

        // Proceed with authentication if the user exists
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            // Get authenticated user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Generate JWT tokens
            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            log.info("Login successful for email: {}", email);

            // Return the tokens as a map
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            return tokens;

        } catch (AuthenticationException ex) {
            // Log failed login attempt due to incorrect password and return null
            log.warn("Login failed for email '{}': Incorrect password", email);
            return null;  // Return null if authentication fails (wrong password)
        }
    }
}

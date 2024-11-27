package balancebite.utils;

import balancebite.errorHandling.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class for extracting information from JWT tokens.
 * Provides methods to retrieve user-specific data, such as user ID or email, from a valid JWT token.
 */
@Component
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Extracts the user ID from a given JWT token.
     *
     * @param token The JWT token to parse.
     * @return The user ID extracted from the token.
     * @throws InvalidTokenException If the token is invalid or cannot be parsed.
     */
    public Long extractUserId(String token) {
        log.info("Extracting user ID from token.");
        try {
            Claims claims = extractAllClaims(token);
            String userId = claims.getSubject(); // Assuming 'sub' contains the user ID
            log.debug("Extracted user ID: {}", userId);
            return Long.parseLong(userId);
        } catch (Exception e) {
            log.error("Failed to extract user ID from token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid or malformed token provided.");
        }
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token The JWT token to parse.
     * @return Claims object containing all token details.
     * @throws InvalidTokenException If the token is invalid or cannot be parsed.
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Error parsing token claims: {}", e.getMessage());
            throw new InvalidTokenException("Invalid token claims.");
        }
    }
}

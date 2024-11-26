package balancebite.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtService is a service class that provides methods for generating, validating,
 * and extracting information from JWT tokens, including support for access and refresh tokens.
 */
@Service
public class JwtService {

    private final static String SECRET_KEY = "yabbadabbadooyabbadabbadooyabbadabbadooyabbadabbadoo";

    private final static long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60; // 1 uur
    private final static long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 30; // 30 days

    /**
     * Retrieves the signing key used to sign JWT tokens.
     *
     * @return the signing key
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the email (subject) from the JWT token.
     *
     * @param token the JWT token
     * @return the email extracted from the token
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token the JWT token
     * @return the expiration date extracted from the token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the JWT token.
     *
     * @param token the JWT token
     * @param claimsResolver a function to extract a specific claim
     * @param <T> the type of the claim
     * @return the extracted claim
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token the JWT token
     * @return all claims extracted from the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    /**
     * Checks if the JWT token is expired.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generates an access token for the given user details.
     *
     * @param userDetails the user details
     * @return the generated access token
     */
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");
        return createToken(claims, userDetails.getUsername(), ACCESS_TOKEN_VALIDITY);
    }

    /**
     * Generates a refresh token for the given user details.
     *
     * @param userDetails the user details
     * @return the generated refresh token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, userDetails.getUsername(), REFRESH_TOKEN_VALIDITY);
    }

    /**
     * Creates a JWT token with the given claims, subject, and validity period.
     *
     * @param claims the claims to include in the token
     * @param subject the subject (username) of the token
     * @param validity the validity period of the token in milliseconds
     * @return the created JWT token
     */
    private String createToken(Map<String, Object> claims, String subject, long validity) {
        long currentTime = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime + validity))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the JWT token against the given user details.
     *
     * @param token the JWT token
     * @param userDetails the user details
     * @return true if the token is valid, false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractEmail(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Validates whether the given token is a refresh token.
     *
     * @param token the JWT token
     * @return true if the token is valid, of type refresh, and not expired
     */
    public Boolean validateRefreshToken(String token) {
        if (isTokenExpired(token)) {
            return false;
        }
        final Claims claims = extractAllClaims(token);
        String type = claims.get("type", String.class);
        return "refresh".equals(type);
    }

    /**
     * Validates whether the given token is an access token.
     *
     * @param token the JWT token
     * @return true if the token is valid, of type access, and not expired
     */
    public Boolean validateAccessToken(String token) {
        if (isTokenExpired(token)) {
            return false;
        }
        final Claims claims = extractAllClaims(token);
        String type = claims.get("type", String.class);
        return "access".equals(type);
    }
}

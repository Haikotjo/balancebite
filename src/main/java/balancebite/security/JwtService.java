package balancebite.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtService is a service class that provides methods for generating, validating,
 * and extracting information from JWT tokens, using user IDs as the subject.
 */
@Service
public class JwtService {

    private final static String SECRET_KEY = "yabbadabbadooyabbadabbadooyabbadabbadooyabbadabbadoo";

    private final static long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 60; // 1 hour
    private final static long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 30; // 30 days

    private final TokenBlacklistService tokenBlacklistService;
    public JwtService(TokenBlacklistService tokenBlacklistService) {
        this.tokenBlacklistService = tokenBlacklistService;
    }

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
     * Extracts the user ID from the JWT token.
     *
     * @param token the JWT token
     * @return the user ID extracted from the token
     * @throws IllegalArgumentException if the token is invalid or cannot be parsed
     */
    public Long extractUserId(String token) {
        try {
            return Long.valueOf(extractClaim(token, Claims::getSubject));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or malformed token provided.", e);
        }
    }

    /**
     * Extracts roles from the JWT token.
     *
     * @param token the JWT token
     * @return a list of roles extracted from the token
     */
    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token the JWT token
     * @return Claims object containing all token details
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
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
     * Checks if the JWT token is expired.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
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
     * Generates an access token for the given user ID and roles.
     *
     * @param userId the ID of the user
     * @param roles the roles of the user
     * @return the generated access token
     */
    public String generateAccessToken(Long userId, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");
        claims.put("roles", roles); // Add roles to the token
        return createToken(claims, String.valueOf(userId), ACCESS_TOKEN_VALIDITY);
    }

    /**
     * Generates a refresh token for the given user ID.
     *
     * @param userId the ID of the user
     * @return the generated refresh token
     */
    public String generateRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, String.valueOf(userId), REFRESH_TOKEN_VALIDITY);
    }

    /**
     * Creates a JWT token with the given claims, subject, and validity period.
     *
     * @param claims the claims to include in the token
     * @param subject the subject (user ID) of the token
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
        return "access".equals(claims.get("type", String.class));
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
        return "refresh".equals(claims.get("type", String.class));
    }

    /**
     * Blacklists a token by adding it to the database with an expiry time.
     *
     * @param token the token to blacklist.
     */
    public void blacklistToken(String token) {
        LocalDateTime expiry = extractExpiration(token).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        tokenBlacklistService.blacklistToken(token, expiry); // Delegate to TokenBlacklistService
    }

    /**
     * Checks if a token is blacklisted.
     *
     * @param token the token to check.
     * @return true if the token is blacklisted, false otherwise.
     */
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistService.isTokenBlacklisted(token);
    }

}

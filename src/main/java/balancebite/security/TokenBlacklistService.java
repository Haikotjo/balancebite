package balancebite.security;

import balancebite.security.BlacklistedToken;
import balancebite.repository.TokenBlacklistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service for managing the token blacklist.
 * This service provides methods to add tokens to the blacklist and check if a token is blacklisted.
 */
@Service
public class TokenBlacklistService {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);

    private final TokenBlacklistRepository repository;

    /**
     * Constructs a TokenBlacklistService with the given repository.
     *
     * @param repository the TokenBlacklistRepository to use for database operations.
     */
    public TokenBlacklistService(TokenBlacklistRepository repository) {
        this.repository = repository;
    }

    /**
     * Adds a token to the blacklist with a specified expiry date and time.
     *
     * @param token  the token to blacklist.
     * @param expiry the expiry date and time for the blacklisted token.
     */
    public void blacklistToken(String token, LocalDateTime expiry) {
        logger.info("Blacklisting token: {}", token);

        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        blacklistedToken.setExpiry(expiry);

        repository.save(blacklistedToken);
        logger.info("Token blacklisted successfully with expiry: {}", expiry);
    }

    /**
     * Checks if a token is blacklisted.
     *
     * @param token the token to check.
     * @return true if the token is blacklisted, false otherwise.
     */
    public boolean isTokenBlacklisted(String token) {
        boolean isBlacklisted = repository.findByToken(token).isPresent();
        logger.info("Token blacklisted status for '{}': {}", token, isBlacklisted);
        return isBlacklisted;
    }
}

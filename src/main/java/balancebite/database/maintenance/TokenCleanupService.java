package balancebite.database.maintenance;

import balancebite.repository.TokenBlacklistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service for cleaning up expired tokens from the blacklist.
 *
 * This service contains a scheduled task that runs at a fixed interval
 * (midnight every day) to remove tokens that have expired and are no longer
 * needed. This helps maintain database performance and avoid unnecessary data buildup.
 */
@Service
public class TokenCleanupService {

    private static final Logger logger = LoggerFactory.getLogger(TokenCleanupService.class);

    private final TokenBlacklistRepository repository;

    /**
     * Constructs the TokenCleanupService with the required repository.
     *
     * @param repository the TokenBlacklistRepository for interacting with the database.
     */
    public TokenCleanupService(TokenBlacklistRepository repository) {
        this.repository = repository;
    }

    /**
     * Scheduled task to remove expired tokens from the blacklist.
     *
     * This task runs every day at midnight, based on the cron expression.
     * It deletes all tokens from the blacklist that have an expiry date before the current time.
     */
    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight
    public void cleanUpExpiredTokens() {
        logger.info("Starting cleanup of expired tokens in the blacklist.");

        // Perform the cleanup operation
        int deletedCount = repository.deleteAllByExpiryBefore(LocalDateTime.now());

        // Log the result of the operation
        logger.info("Expired tokens removed from the blacklist. Total removed: {}", deletedCount);
    }
}

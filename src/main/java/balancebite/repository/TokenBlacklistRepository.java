package balancebite.repository;

import balancebite.security.BlacklistedToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository interface for managing blacklisted tokens in the database.
 *
 * This interface provides methods to:
 * - Find a blacklisted token by its value.
 * - Delete expired tokens from the database.
 * - Perform standard CRUD operations provided by JpaRepository.
 */
@Repository
public interface TokenBlacklistRepository extends JpaRepository<BlacklistedToken, Long> {

    /**
     * Finds a blacklisted token by its value.
     *
     * @param token the token to search for.
     * @return an Optional containing the blacklisted token if found, or an empty Optional otherwise.
     */
    Optional<BlacklistedToken> findByToken(String token);

    /**
     * Deletes all tokens that have expired before the specified date and time.
     *
     * @param dateTime the cutoff date and time; all tokens with expiry before this will be removed.
     * @return the number of tokens deleted.
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM BlacklistedToken t WHERE t.expiry < :dateTime")
    int deleteAllByExpiryBefore(LocalDateTime dateTime);
}

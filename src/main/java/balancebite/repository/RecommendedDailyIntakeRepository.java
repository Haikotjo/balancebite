

package balancebite.repository;

import balancebite.model.RecommendedDailyIntake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository interface for managing RecommendedDailyIntake entities.
 * This interface extends JpaRepository to provide standard CRUD operations
 * on RecommendedDailyIntake entities in the database.
 */
public interface RecommendedDailyIntakeRepository extends JpaRepository<RecommendedDailyIntake, Long> {

    /**
     * Finds the RecommendedDailyIntake for a specific user on a specific date, excluding BaseRDI.
     *
     * @param userId The ID of the user for whom the RecommendedDailyIntake is being retrieved.
     * @param createdAt The date for which the RecommendedDailyIntake is being retrieved.
     * @return An Optional containing the RecommendedDailyIntake if found, or empty if not found.
     */
    @Query("SELECT r FROM RecommendedDailyIntake r WHERE r.user.id = :userId AND r.createdAt = :createdAt AND r.id != r.user.baseRecommendedDailyIntake.id")
    Optional<RecommendedDailyIntake> findByUser_IdAndCreatedAt(@Param("userId") Long userId, @Param("createdAt") LocalDate createdAt);

    /**
     * Checks if a RecommendedDailyIntake exists for a specific user on a specific date, excluding BaseRDI.
     *
     * @param userId The ID of the user to check.
     * @param createdAt The date to check for an existing RecommendedDailyIntake.
     * @return True if a RecommendedDailyIntake exists for the specified user and date, otherwise false.
     */
    @Query("SELECT COUNT(r) > 0 FROM RecommendedDailyIntake r WHERE r.user.id = :userId AND r.createdAt = :createdAt AND r.id != r.user.baseRecommendedDailyIntake.id")
    boolean existsByUser_IdAndCreatedAt(@Param("userId") Long userId, @Param("createdAt") LocalDate createdAt);
}

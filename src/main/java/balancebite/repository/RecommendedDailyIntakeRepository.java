package balancebite.repository;

import balancebite.model.RecommendedDailyIntake;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository interface for managing RecommendedDailyIntake entities.
 * This interface extends JpaRepository to provide standard CRUD operations
 * on RecommendedDailyIntake entities in the database.
 */
public interface RecommendedDailyIntakeRepository extends JpaRepository<RecommendedDailyIntake, Long> {

    /**
     * Finds the RecommendedDailyIntake for a specific user on a specific date.
     *
     * @param userId The ID of the user for whom the RecommendedDailyIntake is being retrieved.
     * @param createdAt The date for which the RecommendedDailyIntake is being retrieved.
     * @return An Optional containing the RecommendedDailyIntake if found, or empty if not found.
     */
    Optional<RecommendedDailyIntake> findByUser_IdAndCreatedAt(Long userId, LocalDate createdAt);
}

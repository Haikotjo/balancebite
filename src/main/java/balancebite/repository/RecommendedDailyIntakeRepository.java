package balancebite.repository;

import balancebite.model.RecommendedDailyIntake;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing RecommendedDailyIntake entities.
 * This interface extends JpaRepository to provide standard CRUD operations
 * on RecommendedDailyIntake entities in the database.
 */
public interface RecommendedDailyIntakeRepository extends JpaRepository<RecommendedDailyIntake, Long> {

    // You can add custom query methods here if needed, e.g.:
    // Optional<RecommendedDailyIntake> findByUserId(Long userId);
}

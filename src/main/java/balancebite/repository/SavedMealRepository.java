package balancebite.repository;

import balancebite.dto.meal.SavedMealDTO;
import balancebite.model.meal.Meal;
import balancebite.model.meal.SavedMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SavedMealRepository extends JpaRepository<SavedMeal, Long> {

    long countByMeal(Meal meal);

    long countByMealAndTimestampAfter(Meal meal, LocalDateTime since);

    List<SavedMeal> findAllByTimestampAfter(LocalDateTime since);

    Optional<SavedMeal> findTopByMealOrderByTimestampDesc(Meal meal); // ← toegevoegd

    @Modifying
    @Query("""
        DELETE FROM SavedMeal s WHERE s.id = (
            SELECT s2.id FROM SavedMeal s2 WHERE s2.meal.id = :mealId
            ORDER BY s2.timestamp DESC LIMIT 1
        )
    """)
    void deleteLatestByMealId(@Param("mealId") Long mealId);

    @Query("""
        SELECT new balancebite.dto.meal.SavedMealDTO(s.meal, COUNT(s))
        FROM SavedMeal s
        WHERE s.timestamp >= :since
        GROUP BY s.meal
        ORDER BY COUNT(s) DESC
    """)
    List<SavedMealDTO> findSavedMealsSince(@Param("since") LocalDateTime since);
}


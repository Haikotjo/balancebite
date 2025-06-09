package balancebite.repository;

import balancebite.dto.diet.SavedDietPlanDTO;
import balancebite.model.diet.SavedDietPlan;
import balancebite.model.diet.DietPlan;
import balancebite.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SavedDietPlanRepository extends JpaRepository<SavedDietPlan, Long> {

    // Count total saves of a diet plan
    long countByDietPlan(DietPlan dietPlan);

    // Count saves in een bepaalde periode (bijv. deze week/maand)
    long countByDietPlanAndTimestampAfter(DietPlan dietPlan, LocalDateTime since);

    // Voor populaire plannen (optioneel)
    List<SavedDietPlan> findAllByTimestampAfter(LocalDateTime since);

    void deleteTopByDietPlanIdOrderByTimestampDesc(Long dietPlanId);

    @Modifying
    @Query("DELETE FROM SavedDietPlan s WHERE s.id = (" +
            "SELECT s2.id FROM SavedDietPlan s2 WHERE s2.dietPlan.id = :dietPlanId " +
            "ORDER BY s2.timestamp DESC LIMIT 1)")
    void deleteLatestByDietPlanId(@Param("dietPlanId") Long dietPlanId);

    @Query("""
    SELECT new balancebite.dto.diet.SavedDietPlanDTO(s.dietPlan, COUNT(s))
    FROM SavedDietPlan s
    WHERE s.timestamp >= :since
    GROUP BY s.dietPlan
    ORDER BY COUNT(s) DESC
""")
    List<SavedDietPlanDTO> findSavedDietPlanSince(@Param("since") LocalDateTime since);

}

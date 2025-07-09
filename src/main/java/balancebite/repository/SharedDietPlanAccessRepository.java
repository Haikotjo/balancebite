package balancebite.repository;

import balancebite.model.diet.DietPlan;
import balancebite.model.diet.SharedDietPlanAccess;
import balancebite.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SharedDietPlanAccessRepository extends JpaRepository<SharedDietPlanAccess, Long> {

    List<SharedDietPlanAccess> findByUser(User user);

    List<SharedDietPlanAccess> findByEmail(String email);

    Optional<SharedDietPlanAccess> findByDietPlanAndUser(DietPlan dietPlan, User user);

    Optional<SharedDietPlanAccess> findByDietPlanAndEmail(DietPlan dietPlan, String email);

    List<SharedDietPlanAccess> findByDietPlan(DietPlan dietPlan);

    void deleteByDietPlan(DietPlan dietPlan);

    boolean existsByDietPlanIdAndUserId(Long dietPlanId, Long userId);

    boolean existsByDietPlanIdAndEmail(Long dietPlanId, String email);

    boolean existsByDietPlanIdAndEmailIgnoreCase(Long dietPlanId, String email);

}

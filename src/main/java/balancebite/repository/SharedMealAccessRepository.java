package balancebite.repository;

import balancebite.model.meal.Meal;
import balancebite.model.meal.SharedMealAccess;
import balancebite.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SharedMealAccessRepository extends JpaRepository<SharedMealAccess, Long> {

    List<SharedMealAccess> findByUser(User user);

    List<SharedMealAccess> findByEmail(String email);

    Optional<SharedMealAccess> findByMealAndUser(Meal meal, User user);

    Optional<SharedMealAccess> findByMealAndEmail(Meal meal, String email);

    List<SharedMealAccess> findByMeal(Meal meal);

    boolean existsByMealIdAndEmail(Long mealId, String email);

    boolean existsByMealIdAndUserId(Long mealId, Long userId);

    boolean existsByMealIdAndEmailIgnoreCase(Long mealId, String email);

    void deleteByMeal(Meal meal);
}

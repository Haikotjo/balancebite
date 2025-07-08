package balancebite.service.interfaces.user;

import balancebite.dto.diet.DietPlanDTO;
import balancebite.dto.diet.DietPlanInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.model.meal.references.Diet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface IUserDietPlanService {

    DietPlanDTO createDietPlan(DietPlanInputDTO input, Long userId);

    DietPlanDTO updateDietPlan(Long dietPlanId, DietPlanInputDTO input, Long adjustedByUserId);

    DietPlanDTO getDietPlanById(Long dietId, Long userId);

    Page<DietPlanDTO> getFilteredDietPlans(
            List<String> requiredDiets,
            List<String> excludedDiets,
            Long userId,
            String mode,
            Diet dietFilter,
            Double minCalories,
            Double maxCalories,
            Double minProtein,
            Double maxProtein,
            Double minCarbs,
            Double maxCarbs,
            Double minFat,
            Double maxFat,
            String sortBy,
            String sortOrder,
            Pageable pageable
    );


    DietPlanDTO removeDietDay(Long userId, Long dietPlanId, int dayIndex);

    DietPlanDTO addMealToDietDay(Long userId, Long dietPlanId, int dayIndex, Long mealId);

    void updateDietPrivacy(Long userId, Long dietPlanId, boolean isPrivate);

    void updateDietRestriction(Long userId, Long dietPlanId, boolean isRestricted);

    DietPlanDTO removeMealFromDietDay(Long userId, Long dietPlanId, int dayIndex, Long mealId);

    UserDTO removeDietPlanFromUser(Long userId, Long dietPlanId);

    DietPlanDTO addDietPlanToUser(Long userId, Long dietPlanId);

    List<Map<String, Object>> getShoppingListForDietPlan(Long dietPlanId, Long userId);
}

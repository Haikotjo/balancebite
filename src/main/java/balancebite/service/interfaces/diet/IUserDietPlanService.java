package balancebite.service.interfaces.diet;

import balancebite.dto.diet.DietPlanDTO;
import balancebite.dto.diet.DietPlanInputDTO;
import balancebite.dto.user.UserDTO;

import java.util.List;

public interface IUserDietPlanService {

    DietPlanDTO createDietPlan(DietPlanInputDTO input, Long userId);

    DietPlanDTO getDietPlanById(Long dietId, Long userId);

    List<DietPlanDTO> getAllDietPlansForUser(Long userId);

    DietPlanDTO removeDietDay(Long userId, Long dietPlanId, int dayIndex);


    DietPlanDTO updateDietPlan(Long dietPlanId, DietPlanInputDTO input, Long adjustedByUserId);

    DietPlanDTO addMealToDietDay(Long userId, Long dietPlanId, int dayIndex, Long mealId);

    DietPlanDTO removeMealFromDietDay(Long userId, Long dietPlanId, int dayIndex, Long mealId);

    void deleteDietPlan(Long dietPlanId, Long userId);

    UserDTO addDietPlanToUser(Long userId, Long dietPlanId);

}

package balancebite.service.interfaces.diet;

import balancebite.dto.diet.DietPlanDTO;
import balancebite.dto.diet.DietPlanInputDTO;
import balancebite.dto.user.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserDietPlanService {

    DietPlanDTO createDietPlan(DietPlanInputDTO input, Long userId);

    DietPlanDTO getDietPlanById(Long dietId, Long userId);

    Page<DietPlanDTO> getAllDietPlansForUser(Long userId, Pageable pageable);

    Page<DietPlanDTO> getDietPlansCreatedByUser(Long userId, Pageable pageable);


    DietPlanDTO removeDietDay(Long userId, Long dietPlanId, int dayIndex);

    DietPlanDTO updateDietPlan(Long dietPlanId, DietPlanInputDTO input, Long adjustedByUserId);

    DietPlanDTO addMealToDietDay(Long userId, Long dietPlanId, int dayIndex, Long mealId);

    DietPlanDTO removeMealFromDietDay(Long userId, Long dietPlanId, int dayIndex, Long mealId);

    UserDTO removeDietPlanFromUser(Long userId, Long dietPlanId);

    DietPlanDTO addDietPlanToUser(Long userId, Long dietPlanId);
}

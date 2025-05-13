package balancebite.service.interfaces.diet;

import balancebite.dto.diet.DietPlanDTO;

import java.util.List;

public interface IPublicDietPlanService {
    List<DietPlanDTO> getAllPublicDietPlans();
    DietPlanDTO getPublicDietPlanById(Long id);
}

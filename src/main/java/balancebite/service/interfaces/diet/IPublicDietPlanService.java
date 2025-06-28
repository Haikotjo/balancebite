package balancebite.service.interfaces.diet;

import balancebite.dto.diet.DietPlanDTO;
import balancebite.dto.diet.DietPlanNameDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPublicDietPlanService {

    DietPlanDTO getPublicDietPlanById(Long id);

    /**
     * Retrieves paginated and sorted public diet plans with optional filtering on diet types,
     * nutrient ranges, inclusion/exclusion of specific diets, and optionally createdBy user ID.
     *
     * @param requiredDiets Diets that must all be included in the plan (e.g., VEGAN, NUT_FREE).
     * @param excludedDiets Diets that must not be included at all (e.g., DAIRY_FREE).
     * @param diets Legacy: any of these diets must be present (optional, for compatibility).
     * @param sortBy Field to sort by (e.g., name, createdAt, avgProtein).
     * @param sortOrder Sorting order ("asc" or "desc").
     * @param pageable Pageable object for pagination.
     * @param minProtein Minimum average protein.
     * @param maxProtein Maximum average protein.
     * @param minCarbs Minimum average carbohydrates.
     * @param maxCarbs Maximum average carbohydrates.
     * @param minFat Minimum average fat.
     * @param maxFat Maximum average fat.
     * @param minCalories Minimum average calories.
     * @param maxCalories Maximum average calories.
     * @param createdByUserId Optional: only return templates created by this user.
     * @return A paginated list of DietPlanDTOs matching the filters.
     */
    Page<DietPlanDTO> getAllPublicDietPlans(
            List<String> requiredDiets,
            List<String> excludedDiets,
            List<String> diets,
            String sortBy,
            String sortOrder,
            Pageable pageable,
            Double minProtein,
            Double maxProtein,
            Double minCarbs,
            Double maxCarbs,
            Double minFat,
            Double maxFat,
            Double minCalories,
            Double maxCalories,
            Long createdByUserId,
            String name
    );
    List<DietPlanNameDTO> getAllPublicDietPlanNames();

}

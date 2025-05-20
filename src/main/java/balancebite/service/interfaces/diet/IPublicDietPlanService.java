package balancebite.service.interfaces.diet;

import balancebite.dto.diet.DietPlanDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPublicDietPlanService {
    DietPlanDTO getPublicDietPlanById(Long id);

    /**
     * Retrieves paginated and sorted public diet plans with optional filtering.
     *
     * @param diets Optional filter for diet types (e.g., KETO, VEGAN).
     * @param sortBy Field to sort by (e.g., name, createdAt).
     * @param sortOrder Sorting order ("asc" or "desc").
     * @param pageable Pageable object for pagination.
     * @return A paginated list of DietPlanDTOs matching the filters.
     */
    Page<DietPlanDTO> getAllPublicDietPlans(
            List<String> diets,
            String sortBy,
            String sortOrder,
            Pageable pageable
    );
}

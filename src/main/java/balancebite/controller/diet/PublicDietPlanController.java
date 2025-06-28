package balancebite.controller.diet;

import balancebite.dto.diet.DietPlanDTO;
import balancebite.dto.diet.DietPlanNameDTO;
import balancebite.errorHandling.DietPlanNotFoundException;
import balancebite.service.interfaces.diet.IPublicDietPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public/diet-plans")
public class PublicDietPlanController {

    private static final Logger log = LoggerFactory.getLogger(PublicDietPlanController.class);
    private final IPublicDietPlanService publicDietPlanService;

    public PublicDietPlanController(IPublicDietPlanService publicDietPlanService) {
        this.publicDietPlanService = publicDietPlanService;
    }

    @GetMapping
    public ResponseEntity<Page<DietPlanDTO>> getAllPublicDiets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) List<String> requiredDiets,
            @RequestParam(required = false) List<String> excludedDiets,
            @RequestParam(required = false) List<String> diets,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) Double minProtein,
            @RequestParam(required = false) Double maxProtein,
            @RequestParam(required = false) Double minCarbs,
            @RequestParam(required = false) Double maxCarbs,
            @RequestParam(required = false) Double minFat,
            @RequestParam(required = false) Double maxFat,
            @RequestParam(required = false) Double minCalories,
            @RequestParam(required = false) Double maxCalories,
            @RequestParam(required = false) Long createdByUserId,
            @RequestParam(required = false) String name

            ) {
        Map<String, String> sortFieldMap = Map.ofEntries(
                Map.entry("avgProtein", "avgProtein"),
                Map.entry("avgCarbs", "avgCarbs"),
                Map.entry("avgFat", "avgFat"),
                Map.entry("avgCalories", "avgCalories"),
                Map.entry("totalProtein", "totalProtein"),
                Map.entry("totalCarbs", "totalCarbs"),
                Map.entry("totalFat", "totalFat"),
                Map.entry("totalCalories", "totalCalories"),
                Map.entry("saveCount", "saveCount"),
                Map.entry("weeklySaveCount", "weeklySaveCount"),
                Map.entry("monthlySaveCount", "monthlySaveCount"),
                Map.entry("createdAt", "createdAt"),
                Map.entry("name", "name")
        );

        String mappedSortBy = sortFieldMap.get(sortBy);
        if (mappedSortBy == null) {
            log.warn("Invalid sortBy value '{}', falling back to default 'createdAt'", sortBy);
            mappedSortBy = "createdAt";
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<DietPlanDTO> plans = publicDietPlanService.getAllPublicDietPlans(
                requiredDiets,
                excludedDiets,
                diets,
                sortBy,
                sortOrder,
                pageable,
                minProtein, maxProtein,
                minCarbs, maxCarbs,
                minFat, maxFat,
                minCalories, maxCalories,
                createdByUserId,
                name
        );

        if (plans.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(plans);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPublicDietById(@PathVariable Long id) {
        try {
            DietPlanDTO diet = publicDietPlanService.getPublicDietPlanById(id);
            return ResponseEntity.ok(diet);
        } catch (DietPlanNotFoundException e) {
            log.warn("Public diet not found: {}", e.getMessage());
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint to retrieve only the IDs and names of all public diet plans.
     * Optimized for search/autocomplete use.
     *
     * @return A list of DietPlanNameDTOs with only ID and name fields.
     */
    @GetMapping("/names")
    public ResponseEntity<?> getAllPublicDietPlanNames() {
        log.info("Fetching all public diet plan names and IDs.");
        List<DietPlanNameDTO> names = publicDietPlanService.getAllPublicDietPlanNames();

        if (names.isEmpty()) {
            log.info("No public diet plan names found.");
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(names);
    }

}

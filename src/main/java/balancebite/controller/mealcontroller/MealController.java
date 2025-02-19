package balancebite.controller.mealcontroller;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.errorHandling.DuplicateMealException;
import balancebite.errorHandling.InvalidFoodItemException;
import balancebite.service.meal.MealService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing Meal-related operations.
 * Provides endpoints to create, update, retrieve, and calculate nutrients for meals.
 */
@RestController
@RequestMapping("/meals")
public class MealController {

    private static final Logger log = LoggerFactory.getLogger(MealController.class);

    private final MealService mealService;

    /**
     * Constructor for dependency injection.
     *
     * @param mealService Service for managing Meal operations.
     */
    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    /**
     * Retrieves all template Meal entities from the repository.
     *
     * @return ResponseEntity containing a list of MealDTO objects representing all template meals, or a 204 NO CONTENT if no templates are found.
     */
    @GetMapping
    public ResponseEntity<?> getAllMeals() {
        try {
            log.info("Retrieving all template meals.");
            List<MealDTO> mealDTOs = mealService.getAllMeals(); // Now returns only template meals
            if (mealDTOs.isEmpty()) {
                log.info("No template meals found.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(mealDTOs);
        } catch (Exception e) {
            log.error("Unexpected error during retrieval of all template meals: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Retrieves a Meal entity by its ID, only if it is marked as a template.
     *
     * @param id The ID of the Meal to retrieve.
     * @return ResponseEntity containing the MealDTO with 200 status code, or an error response with an appropriate status.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTemplateMealById(@PathVariable Long id) {
        try {
            log.info("Received request to retrieve template meal with ID: {}", id);

            // Call the service method to fetch the template meal
            MealDTO mealDTO = mealService.getMealById(id);

            log.info("Successfully retrieved template meal with ID: {}", id);
            return ResponseEntity.ok(mealDTO);

        } catch (EntityNotFoundException e) {
            log.warn("Meal with ID {} not found or not a template: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error occurred while retrieving meal with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Retrieves nutrient information per food item for a specific meal by its ID.
     *
     * @param id The ID of the meal for which to calculate nutrient information.
     * @return ResponseEntity containing a map of food item IDs to nutrient maps, or an error response with an appropriate status.
     */
    @GetMapping("/nutrients-per-food-item/{id}")
    public ResponseEntity<?> calculateNutrientsPerFoodItem(@PathVariable Long id) {
        log.info("Received request to calculate nutrients per food item for meal ID: {}", id);
        try {
            Map<Long, Map<String, NutrientInfoDTO>> nutrientsPerFoodItem = mealService.calculateNutrientsPerFoodItem(id);
            log.info("Successfully calculated nutrients per food item for meal ID: {}", id);
            return ResponseEntity.ok(nutrientsPerFoodItem);
        } catch (EntityNotFoundException e) {
            log.warn("Meal not found for nutrient calculation with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during nutrient calculation for meal ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Retrieves total nutrient information for a specific meal by its ID.
     *
     * @param id The ID of the meal for which to calculate total nutrients.
     * @return ResponseEntity containing a map of nutrient names and their corresponding total values, or an error response with an appropriate status.
     */
    @GetMapping("/nutrients/{id}")
    public ResponseEntity<?> calculateNutrients(@PathVariable Long id) {
        log.info("Received request to calculate total nutrients for meal ID: {}", id);
        try {
            Map<String, NutrientInfoDTO> nutrients = mealService.calculateNutrients(id);
            log.info("Successfully calculated total nutrients for meal ID: {}", id);
            return ResponseEntity.ok(nutrients);
        } catch (EntityNotFoundException e) {
            log.warn("Meal not found for total nutrient calculation with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during total nutrient calculation for meal ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Retrieves and returns all template meals sorted by a specific nutrient.
     *
     * @param sortField The nutrient name to sort by (e.g., "Energy", "Protein", "Total lipid (fat)").
     * @param sortOrder The sorting order ("asc" for ascending, "desc" for descending").
     * @return ResponseEntity containing a sorted list of MealDTOs, or an error response with an appropriate status.
     */
    @GetMapping("/sorted")
    public ResponseEntity<?> getSortedMeals(
            @RequestParam String sortField,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        log.info("Received request to retrieve and sort template meals by {} in {} order.", sortField, sortOrder);

        try {
            List<MealDTO> sortedMeals = mealService.getSortedMeals(sortField, sortOrder);

            if (sortedMeals.isEmpty()) {
                log.info("No meals found for sorting by {}.", sortField);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            log.info("Successfully retrieved {} meals sorted by {}.", sortedMeals.size(), sortField);
            return ResponseEntity.ok(sortedMeals);

        } catch (Exception e) {
            log.error("Unexpected error during sorted meal retrieval: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred."));
        }
    }
}

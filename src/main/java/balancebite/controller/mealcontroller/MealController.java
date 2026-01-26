package balancebite.controller.mealcontroller;

import balancebite.dto.fooditem.FoodItemNameDTO;
import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.dto.meal.MealNameDTO;
import balancebite.errorHandling.DuplicateMealException;
import balancebite.errorHandling.InvalidFoodItemException;
import balancebite.model.meal.references.Cuisine;
import balancebite.model.meal.references.Diet;
import balancebite.model.meal.references.MealType;
import balancebite.service.meal.MealService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
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
     * Retrieves paginated and sorted template meals with optional filtering.
     *
     * Users can filter meals by cuisine, diet, meal type, food items, and macro-nutrients.
     * Meals can be sorted by name, total calories, protein, fat, or carbs.
     * Results are paginated.
     *
     * @param cuisines     (Optional) Filter for meal cuisines.
     * @param diets        (Optional) Filter for meal diets.
     * @param mealTypes    (Optional) Filter for meal types.
     * @param foodItems    (Optional) Filter by food items (comma-separated).
     * @param sortBy       (Optional) Sort field: calories, protein, fat, carbs, name, etc.
     * @param sortOrder    (Optional) Sort direction: "asc" or "desc".
     * @param creatorId    (Optional) Filter by creator user ID.
     * @param minCalories  (Optional) Minimum total calories.
     * @param maxCalories  (Optional) Maximum total calories.
     * @param minProtein   (Optional) Minimum total protein.
     * @param maxProtein   (Optional) Maximum total protein.
     * @param minCarbs     (Optional) Minimum total carbs.
     * @param maxCarbs     (Optional) Maximum total carbs.
     * @param minFat       (Optional) Minimum total fat.
     * @param maxFat       (Optional) Maximum total fat.
     * @param pageable     Pageable object for pagination and sorting.
     * @return A paginated list of MealDTOs matching the filters.
     */
    @GetMapping
    public ResponseEntity<Page<MealDTO>> getAllMeals(
            @RequestParam(required = false) List<String> cuisines,
            @RequestParam(required = false) List<String> diets,
            @RequestParam(required = false) List<String> mealTypes,
            @RequestParam(required = false) List<String> foodItems,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(required = false) Double minCalories,
            @RequestParam(required = false) Double maxCalories,
            @RequestParam(required = false) Double minProtein,
            @RequestParam(required = false) Double maxProtein,
            @RequestParam(required = false) Double minCarbs,
            @RequestParam(required = false) Double maxCarbs,
            @RequestParam(required = false) Double minFat,
            @RequestParam(required = false) Double maxFat,
            @RequestParam(required = false) String foodSource,
            Pageable pageable
    ) {
        try {
            log.info("Retrieving meals. Filter foodSource: {}", foodSource);

            Page<MealDTO> mealDTOs = mealService.getAllMeals(
                    cuisines, diets, mealTypes, foodItems, sortBy, sortOrder, pageable, creatorId,
                    minCalories, maxCalories, minProtein, maxProtein, minCarbs, maxCarbs, minFat, maxFat,
                    foodSource
            );

            return ResponseEntity.ok(mealDTOs);
        } catch (Exception e) {
            log.error("Error retrieving meals: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Page.empty());
        }
    }

    /**
     * Retrieves any Meal entity by its ID.
     *
     * @param id The ID of the Meal to retrieve.
     * @return ResponseEntity containing the MealDTO with 200 status code, or an error response with an appropriate status.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMealById(@PathVariable Long id) {
        try {
            log.info("Received request to retrieve meal with ID: {}", id);

            // Haal de meal op, ongeacht of het een template is of niet
            MealDTO mealDTO = mealService.getMealById(id);

            log.info("Successfully retrieved meal with ID: {}", id);
            return ResponseEntity.ok(mealDTO);

        } catch (EntityNotFoundException e) {
            log.warn("Meal with ID {} not found: {}", id, e.getMessage());
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
     * Retrieves all available enum values for diet, cuisine, and meal type.
     * This allows the frontend to dynamically fetch filtering options without hardcoding them.
     *
     * @return ResponseEntity containing a map with lists of enum values for diets, cuisines, and meal types.
     */
    @GetMapping("/enums")
    public ResponseEntity<Map<String, List<String>>> getMealEnums() {
        Map<String, List<String>> enums = new HashMap<>();
        enums.put("diets", Arrays.stream(Diet.values()).map(Enum::name).toList());
        enums.put("cuisines", Arrays.stream(Cuisine.values()).map(Enum::name).toList());
        enums.put("mealTypes", Arrays.stream(MealType.values()).map(Enum::name).toList());

        return ResponseEntity.ok(enums);
    }

    /**
     * Endpoint to retrieve only the IDs and names of all FoodItems.
     * This is optimized for search functionality where full food item details are not needed.
     *
     * @return A ResponseEntity containing a list of FoodItemDTOs with only ID and name fields.
     */
    @GetMapping("/names")
    public ResponseEntity<?> getAllMealNames() {
        log.info("Fetching all meal names and IDs.");
        List<MealNameDTO> mealNames = mealService.getAllMealNames();

        if (mealNames.isEmpty()) {
            log.info("No meal names found.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(mealNames);
    }
}

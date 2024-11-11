package balancebite.controller;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.errorHandling.DuplicateMealException;
import balancebite.errorHandling.InvalidFoodItemException;
import balancebite.service.MealService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
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
     * Creates a new Meal entity based on the provided MealInputDTO.
     *
     * @param mealInputDTO The input data for creating the meal.
     * @return ResponseEntity containing the created MealDTO with 201 status code, or an error response with an appropriate status.
     */
    @PostMapping
    public ResponseEntity<?> createMealNoUser(@RequestBody MealInputDTO mealInputDTO) {
        try {
            log.info("Creating new meal with name: {}", mealInputDTO.getName());
            MealDTO createdMeal = mealService.createMealNoUser(mealInputDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMeal);
        } catch (DuplicateMealException e) {
            log.warn("Duplicate meal detected for meal name {}: {}", mealInputDTO.getName(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (InvalidFoodItemException e) {
            log.warn("Invalid food item for meal creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during meal creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Creates a new Meal entity for a specific user based on the provided MealInputDTO.
     *
     * @param mealInputDTO The input data for creating the meal.
     * @param userId       The ID of the user to associate the meal with.
     * @return ResponseEntity containing the created MealDTO with 201 status code, or an error response with an appropriate status.
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createMealForUser(@RequestBody MealInputDTO mealInputDTO, @PathVariable Long userId) {
        try {
            log.info("Creating new meal for user ID: {}", userId);
            MealDTO createdMeal = mealService.createMealForUser(mealInputDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMeal);
        } catch (DuplicateMealException e) {
            log.warn("Duplicate meal detected for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            log.warn("User not found with ID {} during meal creation: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (InvalidFoodItemException e) {
            log.warn("Invalid food item for user meal creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during meal creation for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Updates an existing Meal entity by its ID with the provided MealInputDTO.
     *
     * @param id           The ID of the meal to update.
     * @param mealInputDTO The new details of the meal.
     * @return ResponseEntity containing the updated MealDTO with 200 status code, or an error response with an appropriate status.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateMeal(@PathVariable Long id, @RequestBody MealInputDTO mealInputDTO) {
        try {
            log.info("Updating meal with ID: {}", id);
            MealDTO updatedMeal = mealService.updateMeal(id, mealInputDTO);
            return ResponseEntity.ok(updatedMeal);
        } catch (DuplicateMealException e) {
            log.warn("Duplicate meal detected during update for meal ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            log.warn("Meal not found with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (InvalidFoodItemException e) {
            log.warn("Invalid food item in meal update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during meal update for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
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
     * Retrieves a Meal entity by its ID.
     *
     * @param id The ID of the meal to retrieve.
     * @return ResponseEntity containing the corresponding MealDTO with 200 status code, or an error response with an appropriate status.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMealById(@PathVariable Long id) {
        try {
            log.info("Retrieving meal with ID: {}", id);
            MealDTO mealDTO = mealService.getMealById(id);
            return ResponseEntity.ok(mealDTO);
        } catch (EntityNotFoundException e) {
            log.warn("Meal not found with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during retrieval for meal ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Deletes a specific Meal entity by its ID.
     *
     * @param mealId The ID of the meal to delete.
     * @return ResponseEntity with 204 NO CONTENT status if deletion is successful, or an error response with an appropriate status.
     */
    @DeleteMapping("/meal/{mealId}")
    public ResponseEntity<?> deleteMeal(@PathVariable Long mealId) {
        try {
            log.info("Deleting meal with ID: {}", mealId);
            mealService.deleteMeal(mealId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.warn("Meal not found for deletion with ID: {}", mealId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during meal deletion for ID {}: {}", mealId, e.getMessage(), e);
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
        try {
            log.info("Calculating nutrients per food item for meal ID: {}", id);
            Map<Long, Map<String, NutrientInfoDTO>> nutrientsPerFoodItem = mealService.calculateNutrientsPerFoodItem(id);
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
        try {
            log.info("Calculating total nutrients for meal ID: {}", id);
            Map<String, NutrientInfoDTO> nutrients = mealService.calculateNutrients(id);
            return ResponseEntity.ok(nutrients);
        } catch (EntityNotFoundException e) {
            log.warn("Meal not found for total nutrient calculation with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during total nutrient calculation for meal ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }
}

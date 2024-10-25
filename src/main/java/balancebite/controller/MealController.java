package balancebite.controller;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.errorHandling.InvalidFoodItemException;
import balancebite.service.MealService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> createMeal(@Valid @RequestBody MealInputDTO mealInputDTO) {
        try {
            MealDTO createdMeal = mealService.createMeal(mealInputDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMeal);
        } catch (InvalidFoodItemException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
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
            MealDTO createdMeal = mealService.createMealForUser(mealInputDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMeal);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (InvalidFoodItemException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
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
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMeal(@PathVariable Long id, @RequestBody MealInputDTO mealInputDTO) {
        try {
            MealDTO updatedMeal = mealService.updateMeal(id, mealInputDTO);
            return ResponseEntity.ok(updatedMeal);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (InvalidFoodItemException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Retrieves all Meal entities from the repository.
     *
     * @return ResponseEntity containing a list of MealDTO objects representing all meals, or a 204 NO CONTENT if no meals are found.
     */
    @GetMapping
    public ResponseEntity<?> getAllMeals() {
        try {
            List<MealDTO> mealDTOs = mealService.getAllMeals();
            if (mealDTOs.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(mealDTOs);
        } catch (Exception e) {
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
            MealDTO mealDTO = mealService.getMealById(id);
            return ResponseEntity.ok(mealDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
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
            mealService.deleteMeal(mealId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
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
            Map<Long, Map<String, NutrientInfoDTO>> nutrientsPerFoodItem = mealService.calculateNutrientsPerFoodItem(id);
            return ResponseEntity.ok(nutrientsPerFoodItem);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
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
            Map<String, NutrientInfoDTO> nutrients = mealService.calculateNutrients(id);
            return ResponseEntity.ok(nutrients);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }
}

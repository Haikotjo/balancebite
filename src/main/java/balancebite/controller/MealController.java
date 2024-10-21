package balancebite.controller;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.exceptions.InvalidFoodItemException;
import balancebite.service.MealService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing Meal-related operations.
 * Provides endpoints to create, retrieve, and calculate nutrients for meals.
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
     * @param mealInputDTO the input data for creating the meal.
     * @return ResponseEntity containing the created MealDTO object and status.
     */
    @PostMapping
    public ResponseEntity<MealDTO> createMeal(@Valid @RequestBody MealInputDTO mealInputDTO) {
        try {
            MealDTO createdMeal = mealService.createMeal(mealInputDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMeal);
        } catch (InvalidFoodItemException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Creates a new Meal entity for a specific user based on the provided MealInputDTO.
     *
     * @param mealInputDTO the input data for creating the meal.
     * @param userId the ID of the user to associate the meal with.
     * @return ResponseEntity containing the created MealDTO object and status.
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<MealDTO> createMealForUser(@RequestBody MealInputDTO mealInputDTO, @PathVariable Long userId) {
        try {
            MealDTO createdMeal = mealService.createMealForUser(mealInputDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMeal);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (InvalidFoodItemException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Updates an existing meal by ID.
     * This method allows updating meal details but not its associated users.
     *
     * @param id the ID of the meal to be updated
     * @param mealInputDTO the new details of the meal
     * @return ResponseEntity containing the updated MealDTO or a 404 Not Found status if the meal is not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<MealDTO> updateMeal(@PathVariable Long id, @RequestBody MealInputDTO mealInputDTO) {
        try {
            MealDTO updatedMeal = mealService.updateMeal(id, mealInputDTO);
            return ResponseEntity.ok(updatedMeal);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (InvalidFoodItemException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Retrieves all meals from the repository.
     *
     * @return ResponseEntity containing a list of MealDTO objects representing all meals, or an appropriate error message.
     */
    @GetMapping
    public ResponseEntity<List<MealDTO>> getAllMeals() {
        List<MealDTO> mealDTOs = mealService.getAllMeals();
        return ResponseEntity.ok(mealDTOs);
    }


    /**
     * Retrieves a Meal entity by its ID.
     *
     * @param id the ID of the meal to retrieve.
     * @return ResponseEntity containing the corresponding MealDTO object, or an error if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMealById(@PathVariable Long id) {
        try {
            MealDTO mealDTO = mealService.getMealById(id);
            return ResponseEntity.ok(mealDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving the meal.");
        }
    }

    /**
     * Deletes a specific meal from a user's list.
     * The user can only delete meals that they have added to their own list.
     *
     * @param userId The ID of the user requesting the deletion.
     * @param mealId The ID of the meal to be deleted from the user's list.
     * @return ResponseEntity containing a status message indicating the result of the operation.
     */
    @DeleteMapping("/user/{userId}/meal/{mealId}")
    public ResponseEntity<String> deleteUserMeal(@PathVariable Long userId, @PathVariable Long mealId) {
        try {
            mealService.deleteUserMeal(userId, mealId);
            return ResponseEntity.ok("Meal successfully deleted from user's list.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
        // Commenting out AccessDeniedException handling until proper authorization mechanism is implemented.
        // Uncomment this once access validation has been properly set up, e.g. using Spring Security.
        /*
        catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
        */
    }


    /**
     * Deletes a specific meal.
     * This operation is intended for administrators who can delete any meal from the repository.
     *
     * @param mealId The ID of the meal to be deleted.
     * @return ResponseEntity containing a status message indicating the result of the operation.
     */
    @DeleteMapping("/meal/{mealId}")
    public ResponseEntity<String> deleteMeal(@PathVariable Long mealId) {
        try {
            mealService.deleteMeal(mealId);
            return ResponseEntity.ok("Meal successfully deleted.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    /**
     * Retrieves the nutrients per food item for a given Meal by its ID.
     *
     * @param id the ID of the meal.
     * @return ResponseEntity containing a map of food item IDs to nutrient maps, or an error if not found.
     */
    @GetMapping("/nutrients-per-food-item/{id}")
    public ResponseEntity<?> calculateNutrientsPerFoodItem(@PathVariable Long id) {
        try {
            Map<Long, Map<String, NutrientInfoDTO>> nutrientsPerFoodItem = mealService.calculateNutrientsPerFoodItem(id);
            return ResponseEntity.ok(nutrientsPerFoodItem);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while calculating nutrients per food item.");
        }
    }

    /**
     * Retrieves the total nutrients for a given Meal by its ID.
     *
     * @param id the ID of the meal.
     * @return ResponseEntity containing a map of nutrient names and their corresponding total values, or an error if not found.
     */
    @GetMapping("/nutrients/{id}")
    public ResponseEntity<?> calculateNutrients(@PathVariable Long id) {
        try {
            Map<String, NutrientInfoDTO> nutrients = mealService.calculateNutrients(id);
            return ResponseEntity.ok(nutrients);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while calculating nutrients.");
        }
    }
}

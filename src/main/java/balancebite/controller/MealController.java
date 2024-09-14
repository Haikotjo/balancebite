package balancebite.controller;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.service.MealService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<MealDTO> createMeal(@RequestBody MealInputDTO mealInputDTO) {
        MealDTO createdMeal = mealService.createMeal(mealInputDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMeal);
    }

    /**
     * Retrieves the total nutrients for a given Meal by its ID.
     *
     * @param id the ID of the meal.
     * @return ResponseEntity containing a map of nutrient names and their corresponding total values, or an error if not found.
     */
    @GetMapping("/nutrients/{id}")
    public ResponseEntity<Map<String, NutrientInfoDTO>> calculateNutrients(@PathVariable Long id) {
        try {
            Map<String, NutrientInfoDTO> nutrients = mealService.calculateNutrients(id);
            return ResponseEntity.ok(nutrients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Retrieves the nutrients per food item for a given Meal by its ID.
     *
     * @param id the ID of the meal.
     * @return ResponseEntity containing a map of food item IDs to nutrient maps, or an error if not found.
     */
    @GetMapping("/nutrients-per-food-item/{id}")
    public ResponseEntity<Map<Long, Map<String, NutrientInfoDTO>>> calculateNutrientsPerFoodItem(@PathVariable Long id) {
        try {
            Map<Long, Map<String, NutrientInfoDTO>> nutrientsPerFoodItem = mealService.calculateNutrientsPerFoodItem(id);
            return ResponseEntity.ok(nutrientsPerFoodItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Retrieves all meals from the repository.
     *
     * @return ResponseEntity containing a list of MealDTO objects representing all meals.
     */
    @GetMapping("/all")
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
    public ResponseEntity<MealDTO> getMealById(@PathVariable Long id) {
        try {
            MealDTO mealDTO = mealService.getMealById(id);
            return ResponseEntity.ok(mealDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Retrieves only the macronutrients for a given Meal by its ID.
     * This includes values such as Energy (kcal), Protein, Carbohydrates, and Fats,
     * where fats are grouped into a separate section.
     *
     * @param id the ID of the meal.
     * @return ResponseEntity containing a map of macronutrient names and their corresponding values, or an error if not found.
     */
    @GetMapping("/macronutrients/{id}")
    public ResponseEntity<Map<String, Object>> getMacronutrients(@PathVariable Long id) {
        try {
            Map<String, Object> macronutrients = mealService.getMacronutrients(id);
            return ResponseEntity.ok(macronutrients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Retrieves only the macronutrients for each food item in a given meal by its ID.
     * This includes values such as Energy (kcal), Protein, Carbohydrates, and Fats,
     * where fats are grouped into a separate section.
     *
     * @param mealId the ID of the meal.
     * @return ResponseEntity containing a map where the key is the food item ID, and the value is a map of macronutrient names and their corresponding values, or an error if not found.
     */
    @GetMapping("/macronutrients-per-food-item/{mealId}")
    public ResponseEntity<Map<Long, Map<String, Object>>> getMacronutrientsPerFoodItem(@PathVariable Long mealId) {
        try {
            Map<Long, Map<String, Object>> macronutrientsPerFoodItem = mealService.getMacronutrientsPerFoodItem(mealId);
            return ResponseEntity.ok(macronutrientsPerFoodItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /**
     * Updates an existing meal by ID.
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
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}

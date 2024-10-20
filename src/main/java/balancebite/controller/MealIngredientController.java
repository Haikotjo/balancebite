package balancebite.controller;

import balancebite.dto.mealingredient.MealIngredientInputDTO;
import balancebite.service.MealIngredientService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling HTTP requests related to Meal Ingredients.
 */
@RestController
@RequestMapping("/mealIngredients")
public class MealIngredientController {

    private final MealIngredientService mealIngredientService;

    /**
     * Constructor for MealIngredientController, using constructor injection
     * for better testability and clear dependency management.
     *
     * @param mealIngredientService the service responsible for managing meal ingredients.
     */
    public MealIngredientController(MealIngredientService mealIngredientService) {
        this.mealIngredientService = mealIngredientService;
    }

    /**
     * Adds a new meal ingredient to a specific meal.
     *
     * @param mealId the ID of the meal to which the ingredient should be added.
     * @param inputDTO the DTO containing the data of the meal ingredient to be added.
     */
    @PostMapping("/add/{mealId}")
    public void addMealIngredient(@PathVariable Long mealId, @RequestBody @Valid MealIngredientInputDTO inputDTO) {
        mealIngredientService.addMealIngredient(mealId, inputDTO);
    }
}

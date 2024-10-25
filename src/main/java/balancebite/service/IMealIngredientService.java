package balancebite.service;

import balancebite.dto.mealingredient.MealIngredientInputDTO;

/**
 * Interface defining methods for managing meal ingredients.
 * Includes operations for adding meal ingredients to specific meals.
 */
public interface IMealIngredientService {

    /**
     * Adds a meal ingredient to a specified meal.
     *
     * @param mealId  The ID of the meal to which the ingredient should be added.
     * @param inputDTO The DTO containing the data of the meal ingredient to be added.
     * @throws IllegalArgumentException if the meal with the specified ID is not found.
     */
    void addMealIngredient(Long mealId, MealIngredientInputDTO inputDTO);
}

package balancebite.dto;

import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.NutrientInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object (DTO) for capturing input data related to a Meal.
 * This class is used for receiving data from the client-side when creating or updating
 * a Meal in the application.
 */
public class MealInputDTO {

    private String name;
    private List<MealIngredientInputDTO> mealIngredients;

    /**
     * Default constructor for MealInputDTO.
     */
    public MealInputDTO() {}

    /**
     * Constructor for creating a MealInputDTO with basic meal information.
     *
     * @param name the name of the meal.
     * @param mealIngredients the list of ingredients that make up the meal.
     */
    public MealInputDTO(String name, List<MealIngredientInputDTO> mealIngredients) {
        this.name = name;
        this.mealIngredients = mealIngredients;
    }

    /**
     * Gets the name of the meal.
     *
     * @return the name of the meal.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the meal.
     *
     * @param name the name of the meal to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of ingredients that make up the meal.
     *
     * @return the list of meal ingredients.
     */
    public List<MealIngredientInputDTO> getMealIngredients() {
        return mealIngredients;
    }

    /**
     * Sets the list of ingredients that make up the meal.
     *
     * @param mealIngredients the list of meal ingredients to set.
     */
    public void setMealIngredients(List<MealIngredientInputDTO> mealIngredients) {
        this.mealIngredients = mealIngredients;
    }
}







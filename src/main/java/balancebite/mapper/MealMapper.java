package balancebite.mapper;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.mealingredient.MealIngredientDTO;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.NutrientInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Meal entities and DTOs.
 */
@Component
public class MealMapper {

    /**
     * Converts a Meal entity to a MealDTO.
     *
     * @param meal the Meal entity to be converted.
     * @return the created MealDTO with the success message.
     */
    public MealDTO toDTO(Meal meal) {
        if (meal == null) {
            return null;
        }

        // Convert Meal entity to MealDTO including the success message
        String successMessage = "Meal successfully created with the following ingredients: " +
                meal.getMealIngredients().stream()
                        .map(ingredient -> ingredient.getFoodItem().getName() + " (" + ingredient.getQuantity() + "g)")
                        .collect(Collectors.joining(", "));

        // Calculate nutrients dynamically from the meal ingredients
        return new MealDTO(
                meal.getId(),
                meal.getName(),
                meal.getMealIngredients().stream()
                        .map(ingredient -> new MealIngredientDTO(
                                ingredient.getId(),
                                meal.getId(),
                                ingredient.getFoodItem() != null ? ingredient.getFoodItem().getId() : null,
                                ingredient.getQuantity()
                        ))
                        .collect(Collectors.toList()),
                successMessage
        );
    }

    /**
     * Calculates nutrients dynamically based on the food items in the meal ingredients.
     *
     * @param mealIngredients the list of ingredients in the meal.
     * @return a map of nutrient names and their aggregated values.
     */
    private void calculateNutrients(List<MealIngredient> mealIngredients) {
        // Loop through each ingredient and process nutrients dynamically
        for (MealIngredient ingredient : mealIngredients) {
            for (NutrientInfo nutrient : ingredient.getFoodItem().getNutrients()) {
                // Dynamic nutrient calculation logic can be implemented here if needed
            }
        }
    }
}

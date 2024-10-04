package balancebite.mapper;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.mealingredient.MealIngredientDTO;
import balancebite.dto.user.UserDTO;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.NutrientInfo;
import balancebite.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Meal entities and DTOs.
 * Handles the transformation of Meal entities, including their ingredients,
 * into corresponding Data Transfer Objects (DTOs).
 */
@Component
public class MealMapper {

    /**
     * Converts a Meal entity to a MealDTO.
     * This includes converting the meal's ingredients, associated users,
     * and generating a success message for meal creation.
     *
     * @param meal the Meal entity to be converted.
     * @return the created MealDTO with the success message.
     */
    public MealDTO toDTO(Meal meal) {
        if (meal == null) {
            return null;
        }

        // Create a success message based on the meal ingredients (for creation)
        String successMessage = "Meal successfully created with the following ingredients: " +
                meal.getMealIngredients().stream()
                        .map(ingredient -> ingredient.getFoodItem().getName() + " (" + ingredient.getQuantity() + "g)")
                        .collect(Collectors.joining(", "));

        // Convert the Meal entity to a MealDTO, including the associated users
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
                meal.getUsers().stream()
                        .map(user -> new UserDTO(user.getId(), user.getUserName(), user.getEmail()))
                        // Convert users to UserDTO
                        .collect(Collectors.toList()),
                successMessage
        );
    }

    /**
     * Converts a Meal entity to a MealDTO for the update operation.
     * This includes converting the meal's ingredients, associated users,
     * and generating a success message for meal update.
     *
     * @param meal the Meal entity to be converted.
     * @return the updated MealDTO with the success message.
     */
    public MealDTO toUpdatedDTO(Meal meal) {
        if (meal == null) {
            return null;
        }

        // Create a success message based on the meal ingredients (for update)
        String successMessage = "Meal successfully updated to include the following ingredients: " +
                meal.getMealIngredients().stream()
                        .map(ingredient -> ingredient.getFoodItem().getName() + " (" + ingredient.getQuantity() + "g)")
                        .collect(Collectors.joining(", "));

        // Convert the Meal entity to a MealDTO, including the associated users
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
                meal.getUsers().stream()
                        .map(user -> new UserDTO(user.getId(), user.getUserName(), user.getEmail()))
                        // Convert users to UserDTO
                        .collect(Collectors.toList()),
                successMessage
        );
    }

    /**
     * (Optional) Method for calculating nutrients dynamically based on the meal ingredients.
     * This method is currently not implemented and can be expanded as needed.
     *
     * @param mealIngredients the list of ingredients in the meal.
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

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
     * This includes converting the meal's ingredients, associated users, and the creator.
     *
     * @param meal the Meal entity to be converted.
     * @return the created MealDTO.
     */
    public MealDTO toDTO(Meal meal) {
        if (meal == null) {
            return null;
        }

        // Convert the Meal entity to a MealDTO, including the associated users and createdBy
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
                        .collect(Collectors.toList()),
                meal.getCreatedBy() != null ? new UserDTO(meal.getCreatedBy().getId(), meal.getCreatedBy().getUserName(), meal.getCreatedBy().getEmail()) : null
        );
    }

    /**
     * Converts a Meal entity to a MealDTO for the update operation.
     * This includes converting the meal's ingredients, associated users, and the creator.
     *
     * @param meal the Meal entity to be converted.
     * @return the updated MealDTO.
     */
    public MealDTO toUpdatedDTO(Meal meal) {
        if (meal == null) {
            return null;
        }

        // Convert the Meal entity to a MealDTO, including the associated users and createdBy
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
                        .collect(Collectors.toList()),
                meal.getCreatedBy() != null ? new UserDTO(meal.getCreatedBy().getId(), meal.getCreatedBy().getUserName(), meal.getCreatedBy().getEmail()) : null
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

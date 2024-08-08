package balancebite.mapper;

import balancebite.dto.MealDTO;
import balancebite.dto.MealInputDTO;
import balancebite.dto.MealIngredientDTO;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MealMapper {

    public Meal toEntity(MealInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        Meal meal = new Meal(inputDTO.getName());
        inputDTO.getMealIngredients().forEach(ingredientInputDTO -> {
            MealIngredient ingredient = new MealIngredient(
                    meal, null, ingredientInputDTO.getQuantity()
            );
            meal.addMealIngredient(ingredient);
        });
        return meal;
    }

    public MealDTO toDTO(Meal meal) {
        if (meal == null) {
            return null;
        }
        return new MealDTO(
                meal.getId(),
                meal.getName(),
                meal.getMealIngredients().stream()
                        .map(ingredient -> new MealIngredientDTO(
                                ingredient.getId(),
                                meal.getId(),
                                ingredient.getFoodItem() != null ? ingredient.getFoodItem().getId() : null,
                                ingredient.getQuantity()
                        )).collect(Collectors.toList())
        );
    }
}

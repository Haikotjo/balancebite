package balancebite.mapper;

import balancebite.dto.MealIngredientDTO;
import balancebite.dto.MealIngredientInputDTO;
import balancebite.model.FoodItem;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.repository.FoodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MealIngredientMapper {

    @Autowired
    private FoodItemRepository foodItemRepository;

    public MealIngredient toEntity(MealIngredientInputDTO inputDTO, Meal meal) {
        FoodItem foodItem = foodItemRepository.findById(inputDTO.getFoodItemId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid food item ID: " + inputDTO.getFoodItemId()));
        return new MealIngredient(meal, foodItem, inputDTO.getQuantity());
    }

    public MealIngredientDTO toDTO(MealIngredient mealIngredient) {
        return new MealIngredientDTO(
                mealIngredient.getId(),
                mealIngredient.getMeal().getId(),
                mealIngredient.getFoodItem().getId(),
                mealIngredient.getQuantity()
        );
    }
}

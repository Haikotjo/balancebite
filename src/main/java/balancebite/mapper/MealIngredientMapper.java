package balancebite.mapper;

import balancebite.dto.mealingredient.MealIngredientDTO;
import balancebite.dto.mealingredient.MealIngredientInputDTO;
import balancebite.model.FoodItem;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.repository.FoodItemRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between MealIngredient entities and DTOs.
 */
@Component
public class MealIngredientMapper {

    private final FoodItemRepository foodItemRepository;

    /**
     * Constructor for MealIngredientMapper, using constructor injection
     * for better testability and clear dependency management.
     *
     * @param foodItemRepository the repository for managing FoodItem entities.
     */
    public MealIngredientMapper(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    /**
     * Converts a MealIngredientInputDTO to a MealIngredient entity.
     *
     * @param inputDTO the DTO containing the input data for creating a MealIngredient.
     * @param meal the Meal entity to which the ingredient should be associated.
     * @return the created MealIngredient entity.
     */
    public MealIngredient toEntity(MealIngredientInputDTO inputDTO, Meal meal) {
        FoodItem foodItem = foodItemRepository.findById(inputDTO.getFoodItemId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid food item ID: " + inputDTO.getFoodItemId()));

        // If quantity is null or 0, use the gramWeight from FoodItem, otherwise use the provided quantity.
        double quantity = (inputDTO.getQuantity() == null || inputDTO.getQuantity() == 0.0)
                ? foodItem.getGramWeight()
                : inputDTO.getQuantity();

        return new MealIngredient(meal, foodItem, quantity);
    }

    /**
     * Converts a MealIngredient entity to a MealIngredientDTO.
     *
     * @param mealIngredient the MealIngredient entity to be converted.
     * @return the created MealIngredientDTO.
     */
    public MealIngredientDTO toDTO(MealIngredient mealIngredient) {
        return new MealIngredientDTO(
                mealIngredient.getId(),
                mealIngredient.getMeal().getId(),
                mealIngredient.getFoodItem().getId(),
                mealIngredient.getQuantity()
        );
    }
}

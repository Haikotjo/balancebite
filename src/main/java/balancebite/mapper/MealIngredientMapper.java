package balancebite.mapper;

import balancebite.dto.mealingredient.MealIngredientDTO;
import balancebite.dto.mealingredient.MealIngredientInputDTO;
import balancebite.errorHandling.InvalidFoodItemException;
import balancebite.model.foodItem.FoodItem;
import balancebite.model.meal.Meal;
import balancebite.model.MealIngredient;
import balancebite.repository.FoodItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Mapper class for converting between MealIngredient entities and DTOs.
 */
@Component
public class MealIngredientMapper {

    private static final Logger log = LoggerFactory.getLogger(MealIngredientMapper.class);

    private final FoodItemRepository foodItemRepository;
    private final FoodItemMapper foodItemMapper;

    /**
     * Constructor for MealIngredientMapper, using constructor injection
     * for better testability and clear dependency management.
     *
     * @param foodItemRepository the repository for managing FoodItem entities.
     */
    public MealIngredientMapper(FoodItemRepository foodItemRepository, FoodItemMapper foodItemMapper) {
        this.foodItemRepository = foodItemRepository;
        this.foodItemMapper = foodItemMapper;
    }

    /**
     * Converts a MealIngredientInputDTO to a MealIngredient entity.
     *
     * @param inputDTO the DTO containing the input data for creating a MealIngredient.
     * @param meal the Meal entity to which the ingredient should be associated.
     * @return the created MealIngredient entity.
     */
    public MealIngredient toEntity(MealIngredientInputDTO inputDTO, Meal meal) {
        log.info("Converting MealIngredientInputDTO to MealIngredient entity for meal ID {}.", meal.getId());

        FoodItem foodItem = foodItemRepository.findById(inputDTO.getFoodItemId())
                .orElseThrow(() -> {
                    log.error("Invalid food item ID: {}", inputDTO.getFoodItemId());
                    return new InvalidFoodItemException("Invalid food item ID: " + inputDTO.getFoodItemId());
                });

        double quantity;
        if (inputDTO.getQuantity() == null || inputDTO.getQuantity() <= 0) {
            quantity = foodItem.getGramWeight();
            log.debug("Quantity is null or <= 0, using gramWeight from FoodItem: {}", quantity);
        } else {
            quantity = inputDTO.getQuantity();
            log.debug("Using quantity from inputDTO: {}", quantity);
        }

        MealIngredient mealIngredient = new MealIngredient(meal, foodItem, quantity);
        log.debug("Finished mapping MealIngredientInputDTO to MealIngredient entity: {}", mealIngredient);
        return mealIngredient;
    }

    /**
     * Converts a MealIngredient entity to a MealIngredientDTO.
     *
     * @param mealIngredient the MealIngredient entity to be converted.
     * @return the created MealIngredientDTO.
     */
    public MealIngredientDTO toDTO(MealIngredient mealIngredient) {
        log.info("Converting MealIngredient entity to MealIngredientDTO for meal ID {}.", mealIngredient.getMeal().getId());

        // Map FoodItem -> DTO once (promo-aware pricePer100g lives here)
        var foodItemDto = foodItemMapper.toDTO(mealIngredient.getFoodItem());

        // Calculate itemCost from pricePer100g and quantity
        BigDecimal itemCost = null; // null means “unknown”
        BigDecimal pricePer100g = (foodItemDto != null) ? foodItemDto.getPricePer100g() : null;
        double qty = mealIngredient.getQuantity(); // grams used in the meal

        if (pricePer100g != null && qty > 0d) {
            // itemCost = pricePer100g * quantity / 100
            itemCost = pricePer100g
                    .multiply(BigDecimal.valueOf(qty))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        MealIngredientDTO dto = new MealIngredientDTO(
                mealIngredient.getId(),
                mealIngredient.getMeal().getId(),
                mealIngredient.getFoodItem().getId(),
                mealIngredient.getFoodItem().getName(),
                mealIngredient.getQuantity(),
                foodItemDto,
                itemCost
        );

        log.debug("Finished mapping MealIngredient entity to MealIngredientDTO: {}", dto);
        return dto;
    }

}

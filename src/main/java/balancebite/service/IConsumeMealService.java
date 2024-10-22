package balancebite.service;

import balancebite.model.Meal;
import balancebite.dto.NutrientInfoDTO;
import java.util.Map;

/**
 * Interface defining the contract for services that handle meal consumption operations.
 */
/**
 * Interface defining the contract for services that handle meal consumption operations.
 */
public interface IConsumeMealService {
    /**
     * Processes the consumption of a meal by a user, updating the user's intake of nutrients for the current day.
     *
     * @param userId The ID of the user consuming the meal.
     * @param mealId The ID of the meal being consumed.
     * @return A map containing the remaining daily intake for each nutrient after the meal consumption for today.
     */
    Map<String, Double> consumeMeal(Long userId, Long mealId);
}

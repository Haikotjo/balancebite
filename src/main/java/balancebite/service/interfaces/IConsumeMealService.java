package balancebite.service.interfaces;

import balancebite.errorHandling.DailyIntakeNotFoundException;
import balancebite.errorHandling.DailyIntakeUpdateException;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;

import java.util.Map;

/**
 * Interface for managing the consumption of meals by users.
 * Defines methods to update nutrient intake based on meal consumption.
 */
public interface IConsumeMealService {

    /**
     * Processes the consumption of a meal by a user, updating the user's intake of nutrients for the current day.
     * This method retrieves the nutrients of the meal, deducts them from the recommended daily intake for today,
     * and updates the remaining intake for each nutrient.
     *
     * @param userId The ID of the user consuming the meal.
     * @param mealId The ID of the meal being consumed.
     * @return A map containing the remaining daily intake for each nutrient after the meal consumption for today.
     * @throws UserNotFoundException if the user is not found in the system.
     * @throws MealNotFoundException if the meal is not found in the system.
     * @throws DailyIntakeNotFoundException if the recommended daily intake for the user is not found.
     * @throws DailyIntakeUpdateException if there is an error updating the daily intake.
     */
    Map<String, Double> consumeMeal(Long userId, Long mealId);
}

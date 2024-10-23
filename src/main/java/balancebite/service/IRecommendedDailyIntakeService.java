package balancebite.service;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import java.util.Map;

/**
 * Service interface for managing Recommended Daily Intake logic.
 * This interface defines the methods to handle operations related to the recommended daily intake of nutrients for users.
 */
public interface IRecommendedDailyIntakeService {
    /**
     * Gets or creates a recommended daily intake for a specific user for today.
     *
     * This method uses the user's information to fetch or create the daily intake.
     *
     * @param userId The ID of the user to assign the recommended daily intake to.
     * @return The RecommendedDailyIntakeDTO with the calculated values for energy, protein, fat, saturated fat, and unsaturated fat intake.
     * @throws IllegalArgumentException If the user does not have all the necessary information (weight, height, age,
     *                                  gender, activity level, goal) or if the user ID is not found.
     */
    RecommendedDailyIntakeDTO getOrCreateDailyIntakeForUser(Long userId);

    /**
     * Retrieves the cumulative recommended nutrient intake for the current week for a specific user.
     *
     * This method calculates the total recommended nutrient intake for the current week by multiplying
     * the recommended daily intake values by the number of remaining days until the upcoming Sunday,
     * and adjusting for any surplus or deficit from previous days in the week.
     *
     * @param userId The ID of the user.
     * @return A map of nutrient names to cumulative values for the current week.
     * @throws IllegalArgumentException If the user is not found.
     */
    Map<String, Double> getAdjustedWeeklyIntakeForUser(Long userId);

    /**
     * Retrieves the cumulative recommended nutrient intake for the current month for a specific user.
     *
     * This method calculates the total recommended nutrient intake for the current month by adjusting
     * for any surplus or deficit from previous days in the month.
     *
     * @param userId The ID of the user.
     * @return A map of nutrient names to cumulative values for the current month.
     * @throws IllegalArgumentException If the user is not found.
     */
    Map<String, Double> getAdjustedMonthlyIntakeForUser(Long userId);

    /**
     * Deletes the Recommended Daily Intake for a specific user.
     *
     * @param userId The ID of the user whose intake will be deleted.
     * @throws IllegalArgumentException If the user is not found.
     */
    void deleteRecommendedDailyIntakeForUser(Long userId);
}

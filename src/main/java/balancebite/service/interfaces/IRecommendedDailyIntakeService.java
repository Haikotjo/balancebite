package balancebite.service.interfaces;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.errorHandling.MissingUserInformationException;

import java.time.LocalDate;
import java.util.Map;

/**
 * Interface defining the methods for managing Recommended Daily Intake calculations and adjustments for users.
 * This interface outlines methods for retrieving, creating, updating, and deleting recommended daily intake
 * data for users, as well as adjusting intake based on weekly and monthly requirements.
 */
public interface IRecommendedDailyIntakeService {

    /**
     * Retrieves or creates the recommended daily intake for a specific user for the current date.
     * Ensures that the user has provided all necessary information for calculation, including
     * weight, height, age, gender, activity level, and goal.
     *
     * @param userId The ID of the user for whom the recommended daily intake is retrieved or created.
     * @return The {@link RecommendedDailyIntakeDTO} containing the calculated intake values for energy,
     *         protein, fat, saturated fat, and unsaturated fat.
     * @throws MissingUserInformationException If the user has not provided all the required information
     *         (weight, height, age, gender, activity level, goal).
     * @throws IllegalArgumentException If the user with the given ID does not exist in the system.
     */
    RecommendedDailyIntakeDTO getOrCreateDailyIntakeForUser(Long userId);

    /**
     * Calculates and retrieves the cumulative recommended nutrient intake for the current week for a specific user.
     * The method adjusts intake based on the user's profile and any intake surplus or deficit from earlier in the week.
     *
     * @param userId The ID of the user whose cumulative weekly intake is being retrieved.
     * @return A map of nutrient names to cumulative values representing the user's weekly recommended intake.
     * @throws IllegalArgumentException If the user with the given ID is not found in the system.
     */
    Map<String, Double> getAdjustedWeeklyIntakeForUser(Long userId);

    /**
     * Calculates and retrieves the cumulative recommended nutrient intake for the current month for a specific user.
     * Adjusts intake for the number of days remaining in the month, factoring in any intake surplus or deficit.
     *
     * @param userId The ID of the user whose cumulative monthly intake is being retrieved.
     * @return A map of nutrient names to cumulative values representing the user's monthly recommended intake.
     * @throws IllegalArgumentException If the user with the given ID is not found in the system.
     */
    Map<String, Double> getAdjustedMonthlyIntakeForUser(Long userId);

    /**
     * Retrieves the recommended daily intake for a specific user on a specific date.
     * Ensures that the user has provided all necessary information for calculation.
     *
     * @param userId    The ID of the user for whom the recommended daily intake is retrieved.
     * @param createdAt The date for which the recommended daily intake is being retrieved.
     * @return The {@link RecommendedDailyIntakeDTO} containing the calculated intake values for energy,
     *         protein, fat, saturated fat, and unsaturated fat.
     * @throws MissingUserInformationException If the user has not provided all the required information
     *         (weight, height, age, gender, activity level, goal).
     * @throws IllegalArgumentException If the user with the given ID does not exist in the system or if
     *         no recommended daily intake is found for the specified date.
     */
    RecommendedDailyIntakeDTO getDailyIntakeForDate(Long userId, LocalDate createdAt);


    /**
     * Deletes the recommended daily intake record for a specific user.
     * Removes all daily intake records associated with the user. If the user has no intake record,
     * an exception is thrown.
     *
     * @param userId The ID of the user whose recommended daily intake will be deleted.
     * @throws IllegalArgumentException If the user with the specified ID does not exist or does not have any
     *                                  recommended daily intake records.
     */
    void deleteRecommendedDailyIntakeForUser(Long userId);
}

package balancebite.service;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.errorHandling.MissingUserInformationException;
import balancebite.mapper.RecommendedDailyIntakeMapper;
import balancebite.model.RecommendedDailyIntake;
import balancebite.model.User;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import balancebite.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

/**
 * Service class responsible for managing Recommended Daily Intake logic.
 */
@Service
public class RecommendedDailyIntakeService implements IRecommendedDailyIntakeService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final RecommendedDailyIntakeMapper RecommendedDailyIntakeMapper;
    private final RecommendedDailyIntakeRepository recommendedDailyIntakeRepository;
    private final UserRepository userRepository;
    private final RecommendedDailyIntakeMapper recommendedDailyIntakeMapper;

    public RecommendedDailyIntakeService(RecommendedDailyIntakeMapper RecommendedDailyIntakeMapper,
                                         RecommendedDailyIntakeRepository recommendedDailyIntakeRepository,
                                         UserRepository userRepository, RecommendedDailyIntakeMapper recommendedDailyIntakeMapper) {
        this.RecommendedDailyIntakeMapper = RecommendedDailyIntakeMapper;
        this.recommendedDailyIntakeRepository = recommendedDailyIntakeRepository;
        this.userRepository = userRepository;
        this.recommendedDailyIntakeMapper = recommendedDailyIntakeMapper;
    }

    /**
     * Retrieves or creates the recommended daily intake for a specific user for the current date.
     *
     * This method checks if the user has provided all the required information:
     * weight, height, age, gender, activity level, and goal. If any of this information is missing,
     * a {@link MissingUserInformationException} is thrown to prompt the user to update their profile.
     *
     * If the user has all the necessary details, the method either retrieves an existing recommended
     * daily intake for the current date or creates a new one based on the user's profile.
     *
     * @param userId The ID of the user for whom the recommended daily intake is retrieved or created.
     * @return The {@code RecommendedDailyIntakeDTO} containing the calculated intake values for energy,
     *         protein, fat, saturated fat, and unsaturated fat.
     * @throws MissingUserInformationException If the user has not provided all the required information
     *         (weight, height, age, gender, activity level, goal).
     * @throws IllegalArgumentException If the user with the given ID does not exist in the system.
     */
    public RecommendedDailyIntakeDTO getOrCreateDailyIntakeForUser(Long userId) {
        User user = findUserById(userId);

        // Check if all necessary fields for calculation are provided
        if (user.getWeight() == null || user.getHeight() == null || user.getAge() == null ||
                user.getGender() == null || user.getActivityLevel() == null || user.getGoal() == null) {

            log.warn("User with ID {} is missing necessary information", userId);
            throw new MissingUserInformationException("User must provide all required information: weight, height, age, gender, activity level, and goal. Please update profile.");
        }

        // Directly use the static method from the utility class
        log.info("Calculating recommended daily intake for user with ID: {}", userId);
        RecommendedDailyIntake recommendedDailyIntake = DailyIntakeCalculatorUtil.getOrCreateDailyIntakeForUser(user);
        log.info("Attempting to save RecommendedDailyIntake for user ID {} on date {}", userId, recommendedDailyIntake.getCreatedAt());

        // Save the recommended daily intake to the database
        recommendedDailyIntakeRepository.save(recommendedDailyIntake);

        log.info("RecommendedDailyIntake for user ID {} successfully created or retrieved for date {}", userId, LocalDate.now());

        // Convert to DTO and return
        return recommendedDailyIntakeMapper.toDTO(recommendedDailyIntake);
    }

    /**
     * Calculates and retrieves the cumulative recommended nutrient intake for the current week for a specific user.
     *
     * This method multiplies the recommended daily intake values by the number of remaining days until the end of the week
     * (Sunday) and adjusts for any surplus or deficit from the previous days of the current week.
     *
     * The method returns a map where the keys are nutrient names (e.g., protein, fat, carbohydrates) and the values are
     * the cumulative recommended intake for the remainder of the week.
     *
     * @param userId The ID of the user whose cumulative weekly intake is being retrieved.
     * @return A map of nutrient names to cumulative values representing the user's weekly recommended intake.
     * @throws IllegalArgumentException If the user with the given ID is not found in the system.
     */
    public Map<String, Double> getAdjustedWeeklyIntakeForUser(Long userId) {
        User user = findUserById(userId);
        log.info("Calculating weekly intake for user with ID: {}", userId);

        // Use the new utility class to calculate the intake
        return WeeklyIntakeCalculatorUtil.calculateAdjustedWeeklyIntake(user);
    }

    /**
     * Calculates and retrieves the cumulative recommended nutrient intake for the current month for a specific user.
     *
     * This method calculates the user's total recommended nutrient intake for the month by multiplying the recommended
     * daily intake values by the number of remaining days in the month and adjusting for any surplus or deficit from
     * previous days.
     *
     * The method returns a map where the keys are nutrient names (e.g., protein, fat, carbohydrates) and the values are
     * the cumulative recommended intake for the remainder of the month.
     *
     * @param userId The ID of the user whose cumulative monthly intake is being retrieved.
     * @return A map of nutrient names to cumulative values representing the user's monthly recommended intake.
     * @throws IllegalArgumentException If the user with the given ID is not found in the system.
     */
    public Map<String, Double> getAdjustedMonthlyIntakeForUser(Long userId) {
        User user = findUserById(userId);
        log.info("Calculating monthly intake for user with ID: {}", userId);

        // Use the new utility class to calculate the intake
        return MonthlyIntakeCalculatorUtil.calculateAdjustedMonthlyIntake(user);
    }

    /**
     * Deletes the recommended daily intake record for a specific user.
     *
     * This method removes the recommended daily intake associated with the user for all dates. If the user does not
     * have a recommended daily intake record, an exception is thrown.
     *
     * @param userId The ID of the user whose recommended daily intake will be deleted.
     * @throws IllegalArgumentException If the user with the specified ID does not exist or does not have any recommended daily intake records.
     */
    public void deleteRecommendedDailyIntakeForUser(Long userId) {
        User user = findUserById(userId);

        if (user.getRecommendedDailyIntakes() == null) {
            log.error("User with ID {} not found", userId);
            throw new IllegalArgumentException("User with ID " + userId + " does not have a recommended daily intake");
        }

        log.info("Deleting recommended daily intake for user with ID: {}", userId);

        // Remove the recommended daily intake
        user.setRecommendedDailyIntakes(null);
        userRepository.save(user);

        log.info("Successfully deleted recommended daily intake for user with ID: {}", userId);
    }

    private User findUserById(Long userId) {
        log.info("Fetching user with ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", userId);
                    return new IllegalArgumentException("User with ID " + userId + " not found");
                });
    }

}

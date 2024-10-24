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
     * Gets or creates a recommended daily intake for a specific user for today.
     *
     * This method fetches the user details and checks if all necessary information
     * (weight, height, age, gender, activity level, and goal) is provided.
     * If any required information is missing, a {@link MissingUserInformationException}
     * is thrown, prompting the user to update their profile.
     *
     * If all data is present, the method calculates or retrieves the recommended daily intake
     * for the user and converts it to a DTO for the response.
     *
     * @param userId The ID of the user to assign the recommended daily intake to.
     * @return The {@code RecommendedDailyIntakeDTO} with the calculated energy, protein,
     *         fat, saturated fat, and unsaturated fat intake.
     * @throws MissingUserInformationException If the user does not have all the necessary
     *                                         information (weight, height, age, gender, activity level, goal).
     * @throws IllegalArgumentException If the user with the specified ID is not found.
     */
    public RecommendedDailyIntakeDTO getOrCreateDailyIntakeForUser(Long userId) {
        // Fetch the user from the repository
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));

        // Check if all necessary fields for calculation are provided
        if (user.getWeight() == null || user.getHeight() == null || user.getAge() == null ||
                user.getGender() == null || user.getActivityLevel() == null || user.getGoal() == null) {

            throw new MissingUserInformationException("User must provide all required information: weight, height, age, gender, activity level, and goal. Please update profile.");
        }

        // Directly use the static method from the utility class
        RecommendedDailyIntake recommendedDailyIntake = DailyIntakeCalculatorUtil.getOrCreateDailyIntakeForUser(user);
        log.info("Attempting to save RecommendedDailyIntake for user ID {} on date {}", userId, recommendedDailyIntake.getCreatedAt());

        // Save the recommended daily intake to the database
        recommendedDailyIntakeRepository.save(recommendedDailyIntake);

        log.info("RecommendedDailyIntake for user ID {} successfully created or retrieved for date {}", userId, LocalDate.now());

        // Convert to DTO and return
        return recommendedDailyIntakeMapper.toDTO(recommendedDailyIntake);
    }


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
    public Map<String, Double> getAdjustedWeeklyIntakeForUser(Long userId) {
        // Fetch the user from the repository
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }

        User user = userOptional.get();

        // Use the new utility class to calculate the intake
        return WeeklyIntakeCalculatorUtil.calculateAdjustedWeeklyIntake(user);
    }

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
    public Map<String, Double> getAdjustedMonthlyIntakeForUser(Long userId) {
        // Fetch the user from the repository
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }

        User user = userOptional.get();

        // Use the new utility class to calculate the intake
        return MonthlyIntakeCalculatorUtil.calculateAdjustedMonthlyIntake(user);
    }

    /**
     * Deletes the RecommendedDailyIntake for a specific user.
     *
     * @param userId The ID of the user whose intake will be deleted.
     */
    public void deleteRecommendedDailyIntakeForUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }

        User user = userOptional.get();

        if (user.getRecommendedDailyIntakes() == null) {
            throw new IllegalArgumentException("User with ID " + userId + " does not have a recommended daily intake");
        }

        // Remove the recommended daily intake
        user.setRecommendedDailyIntakes(null);
        userRepository.save(user);
    }
}

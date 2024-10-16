package balancebite.service;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.mapper.RecommendedDailyIntakeMapper;
import balancebite.model.RecommendedDailyIntake;
import balancebite.model.User;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import balancebite.utils.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing Recommended Daily Intake logic.
 */
@Service
public class RecommendedDailyIntakeService {

    private final RecommendedDailyIntakeMapper intakeMapper;
    private final RecommendedDailyIntakeRepository intakeRepository;
    private final UserRepository userRepository;

    public RecommendedDailyIntakeService(RecommendedDailyIntakeMapper intakeMapper,
                                         RecommendedDailyIntakeRepository intakeRepository,
                                         UserRepository userRepository) {
        this.intakeMapper = intakeMapper;
        this.intakeRepository = intakeRepository;
        this.userRepository = userRepository;
    }

    /**
     * Gets or creates a recommended daily intake for a specific user for today.
     *
     * This method uses the utility class to fetch or create the daily intake for the given user.
     *
     * @param userId The ID of the user to assign the recommended daily intake to.
     * @return The RecommendedDailyIntakeDTO with the calculated energy, protein, fat, saturated fat, and unsaturated fat intake.
     * @throws IllegalArgumentException If the user does not have all the necessary information (weight, height, age,
     *                                  gender, activity level, goal) or if the user ID is not found.
     */
    public RecommendedDailyIntakeDTO getOrCreateDailyIntakeForUser(Long userId) {
        // Fetch the user from the repository
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }

        User user = userOptional.get();

        // Use the utility class to get or create the daily intake
        RecommendedDailyIntake recommendedIntake = DailyIntakeCalculatorUtil.getOrCreateDailyIntakeForUser(user);

        // Convert to DTO and return
        return intakeMapper.toDTO(recommendedIntake);
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
        return WeeklyIntakeCalculator.calculateAdjustedWeeklyIntake(user);
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

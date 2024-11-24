package balancebite.tests.service;

import balancebite.model.user.User;
import balancebite.model.RecommendedDailyIntake;
import balancebite.repository.UserRepository;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.service.user.UserService;
import balancebite.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.stream.Collectors;

/**
 * Test service for creating recommended daily intakes for specific dates.
 * This service is meant solely for testing purposes and should not be used in production.
 */
@Service
public class TestRecommendedDailyIntakeService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecommendedDailyIntakeRepository intakeRepository;

    /**
     * Creates or retrieves a recommended daily intake for a given user and date with calculated values.
     *
     * This method fetches the user details and checks if all necessary information
     * (weight, height, age, gender, activity level, and goal) is provided.
     * If any required information is missing, an {@link IllegalArgumentException} is thrown,
     * prompting the user to update their profile.
     *
     * If all data is present, the method calculates or retrieves the recommended daily intake
     * for the user and adds it for the given date.
     *
     * @param userId The ID of the user to assign the recommended daily intake to.
     * @param date   The date for which the recommended daily intake should be created.
     * @throws IllegalArgumentException If the user with the specified ID is not found or if necessary
     *                                  information is missing to calculate the intake.
     */
    public void createOrRetrieveRecommendedDailyIntakeForDate(Long userId, LocalDate date) {
        // Fetch the user from the repository
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));

        // Check if all necessary fields for calculation are provided
        if (user.getWeight() == null || user.getHeight() == null || user.getAge() == null ||
                user.getGender() == null || user.getActivityLevel() == null || user.getGoal() == null) {
            throw new IllegalArgumentException("User must provide all required information: weight, height, age, gender, activity level, and goal. Please update profile.");
        }

        // Check if an intake for the given date already exists
        boolean intakeExists = user.getRecommendedDailyIntakes().stream()
                .anyMatch(intake -> intake.getCreatedAt().equals(date));

        log.info("Checking if intake exists for user ID {} on date {}. Current intakes: {}", userId, date,
                user.getRecommendedDailyIntakes().stream()
                        .map(intake -> intake.getCreatedAt())
                        .collect(Collectors.toList()));

        if (!intakeExists) {
            // Directly use the static method from the utility class to calculate or retrieve intake
            RecommendedDailyIntake newIntake = DailyIntakeCalculatorUtil.getOrCreateDailyIntakeForUser(user);
            newIntake.setCreatedAt(date);

            // Add the new intake to the user's set of intakes
            user.getRecommendedDailyIntakes().forEach(intake -> {
                log.info("Checking intake for date: {}, createdAt: {}", intake.getCreatedAt(), intake.getCreatedAt());
            });

            log.info("Checking if intake exists for user ID {} on date {}: {}", userId, date, intakeExists);



            // Save the updated user (cascading saves the intake as well)
            userRepository.save(user);
        }
    }
}

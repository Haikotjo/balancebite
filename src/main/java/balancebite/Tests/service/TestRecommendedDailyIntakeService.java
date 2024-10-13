package balancebite.tests.service;

import balancebite.model.User;
import balancebite.model.RecommendedDailyIntake;
import balancebite.repository.UserRepository;
import balancebite.repository.RecommendedDailyIntakeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Test service for creating recommended daily intakes for specific dates.
 * This service is meant solely for testing purposes and should not be used in production.
 */
@Service
public class TestRecommendedDailyIntakeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecommendedDailyIntakeRepository intakeRepository;

    /**
     * Creates a recommended daily intake for a given user and date.
     *
     * @param userId The ID of the user.
     * @param date   The date for which the recommended daily intake should be created.
     */
    public void createRecommendedDailyIntakeForDate(Long userId, LocalDate date) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Check if an intake for the given date already exists
            boolean intakeExists = user.getRecommendedDailyIntakes().stream()
                    .anyMatch(intake -> intake.getCreatedAt().toLocalDate().equals(date));

            if (!intakeExists) {
                // Create a new RecommendedDailyIntake for the specified date
                RecommendedDailyIntake newIntake = new RecommendedDailyIntake();
                newIntake.setCreatedAt(date.atStartOfDay());
                newIntake.setUser(user);

                // Add the new intake to the user's set of intakes
                user.getRecommendedDailyIntakes().add(newIntake);

                // Save the updated user (cascading saves the intake as well)
                userRepository.save(user);
            }
        } else {
            System.err.println("User with ID " + userId + " not found.");
        }
    }
}
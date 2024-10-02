package balancebite.service;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.mapper.RecommendedDailyIntakeMapper;
import balancebite.model.RecommendedDailyIntake;
import balancebite.model.User;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing Recommended Daily Intake logic.
 */
@Service
public class RecommendedDailyIntakeService {

    private final RecommendedDailyIntakeMapper intakeMapper;
    private final RecommendedDailyIntakeRepository intakeRepository;
    private final UserRepository userRepository;  // Toegevoegd om met User te werken

    public RecommendedDailyIntakeService(RecommendedDailyIntakeMapper intakeMapper,
                                         RecommendedDailyIntakeRepository intakeRepository,
                                         UserRepository userRepository) {
        this.intakeMapper = intakeMapper;
        this.intakeRepository = intakeRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new recommended daily intake for a specific user.
     *
     * @param userId The ID of the user to assign the recommended daily intake to.
     * @return The created RecommendedDailyIntakeDTO.
     */
    public RecommendedDailyIntakeDTO createRecommendedDailyIntakeForUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }

        User user = userOptional.get();

        // Create a new RecommendedDailyIntake which initializes the nutrients with default values
        RecommendedDailyIntake newIntake = new RecommendedDailyIntake();

        // Ensure the nutrients set is not empty (optional logging can be added here)
        if (newIntake.getNutrients().isEmpty()) {
            throw new RuntimeException("RecommendedDailyIntake was not properly initialized with nutrients.");
        }

        // Assign the new RecommendedDailyIntake to the user
        user.setRecommendedDailyIntake(newIntake);

        // Save the user, which will cascade and save the RecommendedDailyIntake and its nutrients
        userRepository.save(user);

        // Convert the RecommendedDailyIntake to a DTO and return it
        return intakeMapper.toDTO(newIntake);
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

        if (user.getRecommendedDailyIntake() == null) {
            throw new IllegalArgumentException("User with ID " + userId + " does not have a recommended daily intake");
        }

        // Remove the recommended daily intake
        user.setRecommendedDailyIntake(null);
        userRepository.save(user);
    }

}

package balancebite.service;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.mapper.RecommendedDailyIntakeMapper;
import balancebite.model.RecommendedDailyIntake;
import balancebite.model.User;
import balancebite.model.userenums.Gender;
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

        // Check if user has all necessary data (weight, height, age, gender, activity level)
        if (user.getWeight() == null || user.getHeight() == null || user.getAge() == null ||
                user.getGender() == null || user.getActivityLevel() == null) {
            throw new IllegalArgumentException("User must have weight, height, age, gender, and activity level filled in to calculate recommended intake.");
        }

        // Calculate BMR based on gender
        double bmr;
        if (user.getGender() == Gender.MALE) {
            bmr = 88.362 + (13.397 * user.getWeight()) + (4.799 * user.getHeight()) - (5.677 * user.getAge());
        } else if (user.getGender() == Gender.FEMALE) {
            bmr = 447.593 + (9.247 * user.getWeight()) + (3.098 * user.getHeight()) - (4.330 * user.getAge());
        } else {
            throw new IllegalArgumentException("Unsupported gender");
        }

        // Adjust BMR based on activity level
        double activityFactor;
        switch (user.getActivityLevel()) {
            case SEDENTARY:
                activityFactor = 1.2;
                break;
            case LIGHT:
                activityFactor = 1.375;
                break;
            case MODERATE:
                activityFactor = 1.55;
                break;
            case ACTIVE:
                activityFactor = 1.725;
                break;
            case VERY_ACTIVE:
                activityFactor = 1.9;
                break;
            default:
                throw new IllegalArgumentException("Unsupported activity level");
        }

        // Calculate the total daily energy expenditure
        double totalEnergyKcal = bmr * activityFactor;

        // Create a new RecommendedDailyIntake and set the calculated energy kcal
        RecommendedDailyIntake newIntake = new RecommendedDailyIntake();
        newIntake.getNutrients().forEach(nutrient -> {
            if (nutrient.getName().equals("Energy kcal")) {
                nutrient.setValue(totalEnergyKcal);
            }
        });

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

    /**
     * Retrieves the recommended daily intake for a specific user by their user ID.
     *
     * @param userId The ID of the user.
     * @return The RecommendedDailyIntakeDTO of the user.
     */
    public RecommendedDailyIntakeDTO getRecommendedDailyIntakeForUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }

        User user = userOptional.get();

        RecommendedDailyIntake intake = user.getRecommendedDailyIntake();
        if (intake == null) {
            throw new IllegalArgumentException("User with ID " + userId + " does not have a recommended daily intake");
        }

        return intakeMapper.toDTO(intake);
    }

}

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
    private final UserRepository userRepository;

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
     * This method calculates the recommended daily intake for a user based on their personal details such as weight, height,
     * age, gender, activity level, and their goal (weight loss, maintenance, or weight gain). The calculation for total daily
     * energy expenditure (TDEE) uses the Harris-Benedict formula to estimate the user's basal metabolic rate (BMR), adjusted
     * by their activity level. Based on the user's goal, the final calorie recommendation is adjusted accordingly.
     *
     * @param userId The ID of the user to assign the recommended daily intake to.
     * @return The created RecommendedDailyIntakeDTO with the calculated energy intake.
     * @throws IllegalArgumentException If the user does not have all the necessary information (weight, height, age, gender, activity level)
     *                                  or if the user ID is not found.
     */
    public RecommendedDailyIntakeDTO createRecommendedDailyIntakeForUser(Long userId) {
        // Fetch the user from the repository
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
            // Harris-Benedict formula for men
            bmr = 88.362 + (13.397 * user.getWeight()) + (4.799 * user.getHeight()) - (5.677 * user.getAge());
        } else if (user.getGender() == Gender.FEMALE) {
            // Harris-Benedict formula for women
            bmr = 447.593 + (9.247 * user.getWeight()) + (3.098 * user.getHeight()) - (4.330 * user.getAge());
        } else {
            throw new IllegalArgumentException("Unsupported gender");
        }

        // Adjust BMR based on activity level
        double activityFactor;
        switch (user.getActivityLevel()) {
            case SEDENTARY:
                activityFactor = 1.2;  // Little or no exercise
                break;
            case LIGHT:
                activityFactor = 1.375;  // Light exercise/sports 1-3 days per week
                break;
            case MODERATE:
                activityFactor = 1.55;  // Moderate exercise/sports 3-5 days per week
                break;
            case ACTIVE:
                activityFactor = 1.725;  // Hard exercise/sports 6-7 days per week
                break;
            case VERY_ACTIVE:
                activityFactor = 1.9;  // Very hard exercise or physical job
                break;
            default:
                throw new IllegalArgumentException("Unsupported activity level");
        }

        // Calculate total daily energy expenditure (TDEE)
        double totalEnergyKcal = bmr * activityFactor;

// Adjust total energy based on the user's goal
        double goalAdjustmentFactor;
        switch (user.getGoal()) {
            case WEIGHT_LOSS:
                // Decrease calories by 15% for weight loss
                goalAdjustmentFactor = 0.85;
                totalEnergyKcal *= goalAdjustmentFactor;
                break;
            case WEIGHT_LOSS_WITH_MUSCLE_MAINTENANCE:
                // Moderate calorie deficit for weight loss with muscle preservation (10% reduction)
                goalAdjustmentFactor = 0.90;
                totalEnergyKcal *= goalAdjustmentFactor;
                break;
            case MAINTENANCE:
                // No adjustment for maintenance
                goalAdjustmentFactor = 1.0;
                totalEnergyKcal *= goalAdjustmentFactor;
                break;
            case MAINTENANCE_WITH_MUSCLE_MAINTENANCE:
                // Maintenance with muscle focus may slightly increase caloric needs (5% increase)
                goalAdjustmentFactor = 1.05;
                totalEnergyKcal *= goalAdjustmentFactor;
                break;
            case WEIGHT_GAIN:
                // Increase calories by 15% for weight gain
                goalAdjustmentFactor = 1.15;
                totalEnergyKcal *= goalAdjustmentFactor;
                break;
            case WEIGHT_GAIN_WITH_MUSCLE_FOCUS:
                // Higher calorie surplus for muscle gain (20% increase)
                goalAdjustmentFactor = 1.20;
                totalEnergyKcal *= goalAdjustmentFactor;
                break;
            default:
                throw new IllegalArgumentException("Unsupported goal");
        }


        // Create a new RecommendedDailyIntake and set the calculated energy kcal
        RecommendedDailyIntake newIntake = new RecommendedDailyIntake();

        // Assign the calculated kcal value to the "Energy kcal" nutrient
        final double finalTotalEnergyKcal = totalEnergyKcal;  // Ensure it's effectively final for lambda
        newIntake.getNutrients().forEach(nutrient -> {
            if (nutrient.getName().equals("Energy kcal")) {
                nutrient.setValue(finalTotalEnergyKcal);
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

package balancebite.service;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.mapper.RecommendedDailyIntakeMapper;
import balancebite.model.RecommendedDailyIntake;
import balancebite.model.User;
import balancebite.model.userenums.Gender;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import balancebite.utils.DailyIntakeCalculator;
import balancebite.utils.ProteinIntakeCalculator;
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
     * This method calculates the recommended daily intake based on the user's personal details such as weight,
     * height, age, gender, activity level, and goal (e.g., weight loss, maintenance, or weight gain). It uses the
     * Harris-Benedict formula to estimate the user's basal metabolic rate (BMR), adjusts by their activity level,
     * and further modifies based on the user's specific goal. The method also calculates the recommended daily
     * protein intake based on the user's total energy intake, weight, activity level, age, and goal.
     *
     * @param userId The ID of the user to assign the recommended daily intake to.
     * @return The created RecommendedDailyIntakeDTO with the calculated energy and protein intake.
     * @throws IllegalArgumentException If the user does not have all the necessary information (weight, height, age,
     *                                  gender, activity level, goal) or if the user ID is not found.
     */
    public RecommendedDailyIntakeDTO createRecommendedDailyIntakeForUser(Long userId) {
        // Fetch the user from the repository
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }

        User user = userOptional.get();

        // Check if the user has all necessary data (weight, height, age, gender, activity level, goal)
        if (user.getWeight() == null || user.getHeight() == null || user.getAge() == null ||
                user.getGender() == null || user.getActivityLevel() == null || user.getGoal() == null) {
            throw new IllegalArgumentException("User must have weight, height, age, gender, activity level, and goal filled in to calculate recommended intake.");
        }

        // Calculate Total Daily Energy Expenditure (TDEE) and adjust it based on the user's goal
        double tdee = DailyIntakeCalculator.calculateTDEE(user);
        double totalEnergyKcal = DailyIntakeCalculator.adjustCaloriesForGoal(tdee, user.getGoal());

        // Calculate protein intake based on the user's total energy intake, weight, activity level, age, and goal
        double proteinIntake = ProteinIntakeCalculator.calculateProteinIntake(user);

        // Create a new RecommendedDailyIntake object
        RecommendedDailyIntake newIntake = new RecommendedDailyIntake();

        // Assign the calculated kcal and protein values to their respective nutrients
        final double finalTotalEnergyKcal = totalEnergyKcal;  // Ensure it is effectively final for lambda use
        final double finalProteinIntake = proteinIntake; // Ensure it is effectively final for lambda use
        newIntake.getNutrients().forEach(nutrient -> {
            if (nutrient.getName().equals("Energy kcal")) {
                nutrient.setValue(finalTotalEnergyKcal);
            } else if (nutrient.getName().equals("Protein")) {
                nutrient.setValue(finalProteinIntake);
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

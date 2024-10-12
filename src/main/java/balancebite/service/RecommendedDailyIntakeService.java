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
import java.util.Optional;

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
     * This method checks if a recommended daily intake already exists for today. If it exists, it returns that intake.
     * If it doesn't exist, a new recommended daily intake is created for the user.
     *
     * This method calculates the recommended daily intake based on the user's personal details such as weight,
     * height, age, gender, activity level, and goal (e.g., weight loss, maintenance, or weight gain). It uses the
     * Harris-Benedict formula to estimate the user's basal metabolic rate (BMR), adjusts by their activity level,
     * and further modifies based on the user's specific goal. The method also calculates the recommended daily
     * protein intake based on the user's total energy intake, weight, activity level, age, and goal. Additionally,
     * it calculates the recommended daily fat intake based on a percentage of the adjusted energy intake, which
     * depends on the user's goal, and divides the fat into saturated and unsaturated fats.
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

        // Check if a RecommendedDailyIntake for today already exists
        Optional<RecommendedDailyIntake> existingIntake = user.getRecommendedDailyIntakes().stream()
                .filter(intake -> intake.getCreatedAt().toLocalDate().equals(LocalDate.now()))
                .findFirst();

        if (existingIntake.isPresent()) {
            // Return the existing intake for today
            return intakeMapper.toDTO(existingIntake.get());
        }

        // Calculate Total Daily Energy Expenditure (TDEE) and adjust it based on the user's goal
        double tdee = KcalIntakeCalculator.calculateTDEE(user);
        double totalEnergyKcal = KcalIntakeCalculator.adjustCaloriesForGoal(tdee, user.getGoal());

        // Calculate protein intake based on the user's total energy intake, weight, activity level, age, and goal
        double proteinIntake = ProteinIntakeCalculator.calculateProteinIntake(user);

        // Calculate fat intake based on the user's goal and total energy intake
        double fatIntake = FatIntakeCalculator.calculateFatIntake(user, totalEnergyKcal);

        // Calculate the distribution of saturated and unsaturated fats
        FatTypeDistributionCalculator.FatTypeDistribution fatDistribution = FatTypeDistributionCalculator.calculateFatDistribution(fatIntake);

        // Calculate carbohydrate intake based on the remaining energy after protein and fat
        double carbohydrateIntake = CarbohydrateIntakeCalculator.calculateCarbohydrateIntake(totalEnergyKcal, proteinIntake, fatIntake);

        // Create a new RecommendedDailyIntake object
        RecommendedDailyIntake newIntake = new RecommendedDailyIntake();

        // Assign the calculated kcal, protein, fat, saturated fat, and unsaturated fat values to their respective nutrients
        final double finalTotalEnergyKcal = totalEnergyKcal;  // Ensure it is effectively final for lambda use
        final double finalProteinIntake = proteinIntake; // Ensure it is effectively final for lambda use
        final double finalFatIntake = fatIntake; // Ensure it is effectively final for lambda use
        final double finalSaturatedFat = fatDistribution.getSaturatedFat(); // Ensure it is effectively final for lambda use
        final double finalUnsaturatedFat = fatDistribution.getUnsaturatedFat(); // Ensure it is effectively final for lambda use
        final double finalCarbohydrateIntake = carbohydrateIntake; // Ensure it is effectively final for lambda use

        newIntake.getNutrients().forEach(nutrient -> {
            if (nutrient.getName().equals("Energy kcal")) {
                nutrient.setValue(finalTotalEnergyKcal);
            } else if (nutrient.getName().equals("Protein")) {
                nutrient.setValue(finalProteinIntake);
            } else if (nutrient.getName().equals("Total lipid (fat)")) {
                nutrient.setValue(finalFatIntake);
            } else if (nutrient.getName().equals("Fatty acids, total saturated")) {
                nutrient.setValue(finalSaturatedFat);
            } else if (nutrient.getName().equals("Fatty acids, total polyunsaturated")) {
                nutrient.setValue(finalUnsaturatedFat);
            } else if (nutrient.getName().equals("Carbohydrate, by difference")) {
                nutrient.setValue(finalCarbohydrateIntake);
            }
        });

        // Add the new RecommendedDailyIntake to the user's set of intakes
        newIntake.setUser(user); // Zorg ervoor dat de relatie naar de gebruiker wordt gelegd
        user.getRecommendedDailyIntakes().add(newIntake);

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

        if (user.getRecommendedDailyIntakes() == null) {
            throw new IllegalArgumentException("User with ID " + userId + " does not have a recommended daily intake");
        }

        // Remove the recommended daily intake
        user.setRecommendedDailyIntakes(null);
        userRepository.save(user);
    }
}

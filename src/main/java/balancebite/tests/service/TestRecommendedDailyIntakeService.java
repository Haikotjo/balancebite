package balancebite.tests.service;

import balancebite.model.User;
import balancebite.model.RecommendedDailyIntake;
import balancebite.repository.UserRepository;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.utils.*;
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
     * Creates a recommended daily intake for a given user and date with calculated values.
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

                // Create a new RecommendedDailyIntake for the specified date
                RecommendedDailyIntake newIntake = new RecommendedDailyIntake();
                newIntake.setCreatedAt(date.atStartOfDay());
                newIntake.setUser(user);

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

package balancebite.utils;

import balancebite.model.RecommendedDailyIntake;
import balancebite.model.User;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Utility class responsible for calculating or creating the recommended daily intake for a specific user.
 *
 * This class provides methods to get or recalculate the original recommended daily intake, taking into
 * account user's personal details, activity level, and nutritional goals.
 */
public class DailyIntakeCalculatorUtil {

    /**
     * Gets or creates a recommended daily intake for a specific user for today.
     *
     * This method checks if a recommended daily intake already exists for today. If it exists, it returns that intake.
     * If it doesn't exist, a new recommended daily intake is created for the user and added to the user's intakes.
     *
     * This method calculates the recommended daily intake based on the user's personal details such as weight,
     * height, age, gender, activity level, and goal (e.g., weight loss, maintenance, or weight gain). It uses the
     * Harris-Benedict formula to estimate the user's basal metabolic rate (BMR), adjusts by their activity level,
     * and further modifies based on the user's specific goal. The method also calculates the recommended daily
     * protein intake based on the user's total energy intake, weight, activity level, age, and goal. Additionally,
     * it calculates the recommended daily fat intake based on a percentage of the adjusted energy intake, which
     * depends on the user's goal, and divides the fat into saturated and unsaturated fats.
     *
     * @param user The user to assign the recommended daily intake to.
     * @return The RecommendedDailyIntake object with the calculated energy, protein, fat, saturated fat, and unsaturated fat intake.
     */
    public static RecommendedDailyIntake getOrCreateDailyIntakeForUser(User user) {
        // Check if a RecommendedDailyIntake for today already exists
        Optional<RecommendedDailyIntake> existingIntake = user.getRecommendedDailyIntakes().stream()
                .filter(intake -> intake.getCreatedAt().toLocalDate().equals(LocalDate.now()))
                .findFirst();

        if (existingIntake.isPresent()) {
            // Return the existing intake for today
            return existingIntake.get();
        }

        // Calculate the recommended daily intake
        RecommendedDailyIntake newIntake = calculateDailyIntake(user);

        // Add the new RecommendedDailyIntake to the user's set of intakes
        newIntake.setUser(user); // Ensure the relationship to the user is set
        user.getRecommendedDailyIntakes().add(newIntake);

        return newIntake;
    }

    /**
     * Calculates the recommended daily intake for a specific user without modifying the database.
     *
     * This method calculates the recommended daily intake based on the user's personal details such as weight,
     * height, age, gender, activity level, and goal (e.g., weight loss, maintenance, or weight gain). It uses the
     * Harris-Benedict formula to estimate the user's basal metabolic rate (BMR), adjusts by their activity level,
     * and further modifies based on the user's specific goal. The method also calculates the recommended daily
     * protein intake based on the user's total energy intake, weight, activity level, age, and goal. Additionally,
     * it calculates the recommended daily fat intake based on a percentage of the adjusted energy intake, which
     * depends on the user's goal, and divides the fat into saturated and unsaturated fats.
     *
     * @param user The user for whom to calculate the recommended daily intake.
     * @return A RecommendedDailyIntake object with the calculated energy, protein, fat, saturated fat, and unsaturated fat intake.
     */
    public static RecommendedDailyIntake calculateDailyIntake(User user) {
        // Validate that all necessary user information is available
        if (user.getWeight() == null || user.getHeight() == null || user.getAge() == null ||
                user.getGender() == null || user.getActivityLevel() == null || user.getGoal() == null) {
            throw new IllegalArgumentException("User is missing necessary information for calculations.");
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

        // Create a new RecommendedDailyIntake object without adding it to the user
        RecommendedDailyIntake newIntake = new RecommendedDailyIntake();

        // Assign the calculated kcal, protein, fat, saturated fat, and unsaturated fat values to their respective nutrients
        newIntake.getNutrients().forEach(nutrient -> {
            switch (nutrient.getName()) {
                case "Energy kcal":
                    nutrient.setValue(totalEnergyKcal);
                    break;
                case "Protein":
                    nutrient.setValue(proteinIntake);
                    break;
                case "Total lipid (fat)":
                    nutrient.setValue(fatIntake);
                    break;
                case "Fatty acids, total saturated":
                    nutrient.setValue(fatDistribution.getSaturatedFat());
                    break;
                case "Fatty acids, total polyunsaturated":
                    nutrient.setValue(fatDistribution.getUnsaturatedFat());
                    break;
                case "Carbohydrate, by difference":
                    nutrient.setValue(carbohydrateIntake);
                    break;
                default:
                    // For other nutrients, set to zero or leave as is
                    break;
            }
        });

        return newIntake;
    }
}

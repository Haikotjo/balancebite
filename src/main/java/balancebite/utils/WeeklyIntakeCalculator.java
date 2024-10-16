package balancebite.utils;

import balancebite.model.RecommendedDailyIntake;
import balancebite.model.User;
import balancebite.utils.KcalIntakeCalculator;
import balancebite.utils.ProteinIntakeCalculator;
import balancebite.utils.FatIntakeCalculator;
import balancebite.utils.FatTypeDistributionCalculator;
import balancebite.utils.CarbohydrateIntakeCalculator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class responsible for calculating the cumulative recommended nutrient intake for the current week.
 *
 * This class provides a method to adjust and calculate the total recommended nutrient intake based on the user's
 * daily intake history and remaining intake days in the current week.
 */
public class WeeklyIntakeCalculator {

    /**
     * Calculates the cumulative recommended nutrient intake for the current week for a specific user.
     *
     * This method calculates the total recommended nutrient intake for the current week by adjusting the intake
     * for each nutrient based on the user's actual intake from past days. It also considers the recommended daily
     * intake for the remaining days in the current week.
     *
     * @param user The user for whom the intake is calculated.
     * @return A map of nutrient names to cumulative values for the current week.
     */
    public static Map<String, Double> calculateAdjustedWeeklyIntake(User user) {
        // Determine today's date and the upcoming Sunday's date
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(java.time.DayOfWeek.SUNDAY);

        // Map to store cumulative nutrient values for the week
        Map<String, Double> cumulativeIntake = new HashMap<>();

        // Iterate over the days from the beginning of the week until yesterday to account for actual intake
        user.getRecommendedDailyIntakes().stream()
                .filter(intake -> !intake.getCreatedAt().toLocalDate().isBefore(startOfWeek)
                        && intake.getCreatedAt().toLocalDate().isBefore(today))  // Consider only the current week until yesterday
                .forEach(intake -> {
                    intake.getNutrients().forEach(nutrient -> {
                        String nutrientName = nutrient.getName();
                        double value = nutrient.getValue() != null ? nutrient.getValue() : 0.0;

                        // Add the actual intake for previous days to the cumulative intake
                        cumulativeIntake.merge(nutrientName, value, Double::sum);
                    });
                });

        // Get the original recommended daily intake (at creation time, not adjusted for meals) and multiply for remaining days
        RecommendedDailyIntake originalIntake = getOriginalDailyIntake(user);
        if (originalIntake == null) {
            // Recalculate the original intake if not found
            originalIntake = recalculateOriginalDailyIntake(user);
        }
        if (originalIntake != null) {
            originalIntake.getNutrients().forEach(nutrient -> {
                String nutrientName = nutrient.getName();
                double dailyValue = nutrient.getValue() != null ? nutrient.getValue() : 0.0;
                // Multiply the original daily intake by the remaining days of the week (excluding today)
                int remainingDays = endOfWeek.getDayOfYear() - today.getDayOfYear();
                if (remainingDays > 0) {
                    cumulativeIntake.merge(nutrientName, dailyValue * remainingDays, Double::sum);
                }
            });
        }

        // Finally, add today's original intake (unadjusted) to the cumulative intake
        Optional<RecommendedDailyIntake> todayIntakeOptional = user.getRecommendedDailyIntakes().stream()
                .filter(intake -> intake.getCreatedAt().toLocalDate().equals(today))
                .findFirst();

        if (todayIntakeOptional.isPresent()) {
            RecommendedDailyIntake todayIntake = todayIntakeOptional.get();
            todayIntake.getNutrients().forEach(nutrient -> {
                String nutrientName = nutrient.getName();
                double dailyValue = nutrient.getValue() != null ? nutrient.getValue() : 0.0;
                if (dailyValue > 0) {
                    cumulativeIntake.merge(nutrientName, dailyValue, Double::sum);
                }
            });
        }

        return cumulativeIntake;
    }

    /**
     * Retrieves the original recommended daily intake for the start of the week.
     *
     * This method returns the recommended daily intake that was originally calculated, without any adjustments
     * made for meals consumed.
     *
     * @param user The user for whom the original intake is retrieved.
     * @return The original RecommendedDailyIntake object or null if not found.
     */
    private static RecommendedDailyIntake getOriginalDailyIntake(User user) {
        LocalDate startOfWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        return user.getRecommendedDailyIntakes().stream()
                .filter(intake -> intake.getCreatedAt().toLocalDate().equals(startOfWeek))
                .findFirst()
                .orElse(null);
    }

    /**
     * Recalculates the original recommended daily intake for the user.
     *
     * This method recalculates the recommended daily intake using the user's personal details.
     *
     * @param user The user for whom the intake is recalculated.
     * @return A new RecommendedDailyIntake object with the calculated values.
     */
    private static RecommendedDailyIntake recalculateOriginalDailyIntake(User user) {
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
        newIntake.setUser(user);

        // Assign the calculated kcal, protein, fat, saturated fat, and unsaturated fat values to their respective nutrients
        newIntake.getNutrients().forEach(nutrient -> {
            if (nutrient.getName().equals("Energy kcal")) {
                nutrient.setValue(totalEnergyKcal);
            } else if (nutrient.getName().equals("Protein")) {
                nutrient.setValue(proteinIntake);
            } else if (nutrient.getName().equals("Total lipid (fat)")) {
                nutrient.setValue(fatIntake);
            } else if (nutrient.getName().equals("Fatty acids, total saturated")) {
                nutrient.setValue(fatDistribution.getSaturatedFat());
            } else if (nutrient.getName().equals("Fatty acids, total polyunsaturated")) {
                nutrient.setValue(fatDistribution.getUnsaturatedFat());
            } else if (nutrient.getName().equals("Carbohydrate, by difference")) {
                nutrient.setValue(carbohydrateIntake);
            }
        });

        return newIntake;
    }
}

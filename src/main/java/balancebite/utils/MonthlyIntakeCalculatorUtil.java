package balancebite.utils;

import balancebite.model.RecommendedDailyIntake;
import balancebite.model.User;
import balancebite.utils.DailyIntakeCalculatorUtil;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class responsible for calculating the cumulative recommended nutrient intake for the current month.
 *
 * This class provides a method to adjust and calculate the total recommended nutrient intake based on the user's
 * daily intake history and remaining intake days in the current month.
 */
public class MonthlyIntakeCalculatorUtil {

    /**
     * Calculates the cumulative recommended nutrient intake for the current month for a specific user.
     * <p>
     * This method calculates the total recommended nutrient intake for the current month by adjusting the intake
     * for each nutrient based on the user's actual intake from past days. It also considers the recommended daily
     * intake for the remaining days in the current month.
     *
     * @param user The user for whom the intake is calculated.
     * @return A map of nutrient names to cumulative values for the current month.
     */
    public static Map<String, Double> calculateAdjustedMonthlyIntake(User user) {
        // Determine today's date and the first and last day of the month
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        // Map to store cumulative nutrient values for the month
        Map<String, Double> cumulativeIntake = new HashMap<>();

        // Iterate over the days from the beginning of the month until yesterday to account for actual intake
        user.getRecommendedDailyIntakes().stream()
                .filter(intake -> !intake.getCreatedAt().toLocalDate().isBefore(startOfMonth)
                        && intake.getCreatedAt().toLocalDate().isBefore(today))  // Consider only the current month until yesterday
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
            // Recalculate the original intake if not found using the utility method
            originalIntake = DailyIntakeCalculatorUtil.calculateDailyIntake(user);
        }

        if (originalIntake != null) {
            originalIntake.getNutrients().forEach(nutrient -> {
                String nutrientName = nutrient.getName();
                double dailyValue = nutrient.getValue() != null ? nutrient.getValue() : 0.0;
                // Multiply the original daily intake by the remaining days of the month (excluding today)
                int remainingDays = endOfMonth.getDayOfMonth() - today.getDayOfMonth();
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
     * Retrieves the original recommended daily intake for the start of the month.
     * <p>
     * This method returns the recommended daily intake that was originally calculated, without any adjustments
     * made for meals consumed.
     *
     * @param user The user for whom the original intake is retrieved.
     * @return The original RecommendedDailyIntake object or null if not found.
     */
    private static RecommendedDailyIntake getOriginalDailyIntake(User user) {
        LocalDate startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        return user.getRecommendedDailyIntakes().stream()
                .filter(intake -> intake.getCreatedAt().toLocalDate().equals(startOfMonth))
                .findFirst()
                .orElse(null);
    }
}
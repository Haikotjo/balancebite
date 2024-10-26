package balancebite.utils;

import balancebite.model.RecommendedDailyIntake;
import balancebite.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class responsible for calculating the cumulative recommended nutrient intake for the current month.
 *
 * This class provides a method to adjust and calculate the total recommended nutrient intake based on the user's
 * daily intake history and remaining intake days in the current month.
 */
public class MonthlyIntakeCalculatorUtil {

    private static final Logger log = LoggerFactory.getLogger(MonthlyIntakeCalculatorUtil.class);

    /**
     * Calculates the cumulative recommended nutrient intake for the current month for a specific user.
     *
     * This method calculates the total recommended nutrient intake for the current month by adjusting the intake
     * for each nutrient based on the user's actual intake from past days. It also considers the recommended daily
     * intake for the remaining days in the current month.
     *
     * @param user The user for whom the intake is calculated.
     * @return A map of nutrient names to cumulative values for the current month.
     */
    public static Map<String, Double> calculateAdjustedMonthlyIntake(User user) {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());

        Map<String, Double> cumulativeIntake = new HashMap<>();
        Map<String, Double> previousDaysAdjustment = new HashMap<>();

        // Process all days from the start of the month up to yesterday (excluding today)
        user.getRecommendedDailyIntakes().stream()
                .filter(intake -> !intake.getCreatedAt().isBefore(startOfMonth)
                        && intake.getCreatedAt().isBefore(today))
                .forEach(intake -> intake.getNutrients().forEach(nutrient -> {
                    String nutrientName = nutrient.getName();
                    double value = nutrient.getValue() != null ? nutrient.getValue() : 0.0;
                    previousDaysAdjustment.merge(nutrientName, value, Double::sum);
                    log.info("Previous day ({}) {} value: {}", intake.getCreatedAt(), nutrientName, value);
                }));

        // Process today's intake separately
        user.getRecommendedDailyIntakes().stream()
                .filter(intake -> intake.getCreatedAt().equals(today))
                .findFirst()
                .ifPresent(todayIntake -> todayIntake.getNutrients().forEach(nutrient -> {
                    String nutrientName = nutrient.getName();
                    double dailyValue = nutrient.getValue() != null ? nutrient.getValue() : 0.0;
                    previousDaysAdjustment.merge(nutrientName, dailyValue, Double::sum);
                    log.info("Today's {} intake value: {}", nutrientName, dailyValue);
                }));

        // Always recalculate the daily intake to ensure it's up-to-date
        RecommendedDailyIntake originalIntake = recalculateOriginalDailyIntake(user);

        if (originalIntake != null) {
            int remainingDays = endOfMonth.getDayOfMonth() - today.getDayOfMonth();
            originalIntake.getNutrients().forEach(nutrient -> {
                String nutrientName = nutrient.getName();
                double dailyValue = nutrient.getValue() != null ? nutrient.getValue() : 0.0;

                cumulativeIntake.put(nutrientName, previousDaysAdjustment.getOrDefault(nutrientName, 0.0));
                cumulativeIntake.merge(nutrientName, dailyValue * remainingDays, Double::sum);
                log.info("Remaining {} intake added for {} days: {}", nutrientName, remainingDays, dailyValue * remainingDays);
            });
        }

        log.info("Final cumulative values for this month: {}", cumulativeIntake);
        return cumulativeIntake;
    }

    /**
     * Recalculates the original recommended daily intake for the user to ensure it's accurate and up-to-date.
     *
     * @param user The user for whom the intake is calculated.
     * @return The recalculated RecommendedDailyIntake object.
     */
    private static RecommendedDailyIntake recalculateOriginalDailyIntake(User user) {
        return DailyIntakeCalculatorUtil.calculateDailyIntake(user);
    }
}

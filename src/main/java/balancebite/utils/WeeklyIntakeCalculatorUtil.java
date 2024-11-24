package balancebite.utils;

import balancebite.model.RecommendedDailyIntake;
import balancebite.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class responsible for calculating the cumulative recommended nutrient intake for the current week.
 *
 * This class provides a method to adjust and calculate the total recommended nutrient intake based on the user's
 * daily intake history and remaining intake days in the current week.
 */
public class WeeklyIntakeCalculatorUtil {

    private static final Logger log = LoggerFactory.getLogger(WeeklyIntakeCalculatorUtil.class);

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
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(java.time.DayOfWeek.SUNDAY);

        Map<String, Double> cumulativeIntake = new HashMap<>();
        Map<String, Double> previousDaysAdjustment = new HashMap<>();

        // Process all days from start of the week to yesterday
        user.getRecommendedDailyIntakes().stream()
                .filter(intake -> !intake.getCreatedAt().isBefore(startOfWeek)
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

        // Recalculate daily intake to ensure it's up-to-date
        RecommendedDailyIntake originalIntake = recalculateOriginalDailyIntake(user);
        if (originalIntake != null) {
            int remainingDays = (int) ChronoUnit.DAYS.between(today, endOfWeek);
            originalIntake.getNutrients().forEach(nutrient -> {
                String nutrientName = nutrient.getName();
                double dailyValue = nutrient.getValue() != null ? nutrient.getValue() : 0.0;
                cumulativeIntake.put(nutrientName, previousDaysAdjustment.getOrDefault(nutrientName, 0.0));
                cumulativeIntake.merge(nutrientName, dailyValue * remainingDays, Double::sum);
                log.info("Remaining {} intake added for {} days: {}", nutrientName, remainingDays, dailyValue * remainingDays);
            });
        }

        log.info("Final cumulative values for this week: {}", cumulativeIntake);
        return cumulativeIntake;
    }

    private static RecommendedDailyIntake recalculateOriginalDailyIntake(User user) {
        return DailyIntakeCalculatorUtil.calculateDailyIntake(user);
    }
}

package balancebite.utils;

import balancebite.model.RecommendedDailyIntake;
import balancebite.model.User;

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
        // Determine today's date and the start and end of the week (Monday to Sunday)
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(java.time.DayOfWeek.SUNDAY);

        // Map to store the cumulative nutrient values for the week
        Map<String, Double> cumulativeIntake = new HashMap<>();
        Map<String, Double> previousDaysAdjustment = new HashMap<>(); // Store adjustments for previous days

        // Process all days from Monday (start of the week) up to yesterday (excluding today)
        user.getRecommendedDailyIntakes().stream()
                .filter(intake -> !intake.getCreatedAt().toLocalDate().isBefore(startOfWeek)
                        && intake.getCreatedAt().toLocalDate().isBefore(today))  // All days from the start of the week to yesterday
                .forEach(intake -> {
                    intake.getNutrients().forEach(nutrient -> {
                        String nutrientName = nutrient.getName();
                        double value = nutrient.getValue() != null ? nutrient.getValue() : 0.0;

                        // Add adjustments for previous days
                        previousDaysAdjustment.merge(nutrientName, value, Double::sum);

                        // Logging kcal, protein, carbohydrates, and fat for previous days
                        if (nutrientName.equals("Energy kcal")) {
                            System.out.println("Kcal from previous day (" + intake.getCreatedAt() + "): " + value);
                        }
                        if (nutrientName.equals("Protein")) {
                            System.out.println("Protein from previous day (" + intake.getCreatedAt() + "): " + value);
                        }
                        if (nutrientName.equals("Carbohydrates")) {
                            System.out.println("Carbohydrates from previous day (" + intake.getCreatedAt() + "): " + value);
                        }
                        if (nutrientName.equals("Total lipid (fat)")) {
                            System.out.println("Fat from previous day (" + intake.getCreatedAt() + "): " + value);
                        }
                    });
                });

        // Process today's intake separately (because it can change throughout the day)
        user.getRecommendedDailyIntakes().stream()
                .filter(intake -> intake.getCreatedAt().toLocalDate().equals(today))  // Add today's intake
                .findFirst().ifPresent(todayIntake -> {
                    todayIntake.getNutrients().forEach(nutrient -> {
                        String nutrientName = nutrient.getName();
                        double dailyValue = nutrient.getValue() != null ? nutrient.getValue() : 0.0;
                        previousDaysAdjustment.merge(nutrientName, dailyValue, Double::sum);  // Add today's intake to adjustments

                        // Logging kcal, protein, carbohydrates, and fat for today
                        if (nutrientName.equals("Energy kcal")) {
                            System.out.println("Kcal from today: " + dailyValue);
                        }
                        if (nutrientName.equals("Protein")) {
                            System.out.println("Protein from today: " + dailyValue);
                        }
                        if (nutrientName.equals("Carbohydrates")) {
                            System.out.println("Carbohydrates from today: " + dailyValue);
                        }
                        if (nutrientName.equals("Total lipid (fat)")) {
                            System.out.println("Fat from today: " + dailyValue);
                        }
                    });
                });

        // Always recalculate the daily intake to ensure it's up-to-date
        RecommendedDailyIntake originalIntake = recalculateOriginalDailyIntake(user);

        if (originalIntake != null) {
            int remainingDays = (int) ChronoUnit.DAYS.between(today, endOfWeek);  // Number of remaining days including today
            originalIntake.getNutrients().forEach(nutrient -> {
                String nutrientName = nutrient.getName();
                double dailyValue = nutrient.getValue() != null ? nutrient.getValue() : 0.0;

                // Apply the adjustment for the previous days (positive or negative)
                cumulativeIntake.put(nutrientName, previousDaysAdjustment.getOrDefault(nutrientName, 0.0));

                // Add the remaining days based on the original daily intake
                cumulativeIntake.merge(nutrientName, dailyValue * remainingDays, Double::sum);

                // Logging kcal, protein, carbohydrates, and fat for remaining days
                if (nutrientName.equals("Energy kcal")) {
                    System.out.println("Remaining days (" + remainingDays + ") kcal added: " + (dailyValue * remainingDays));
                }
                if (nutrientName.equals("Protein")) {
                    System.out.println("Remaining days (" + remainingDays + ") protein added: " + (dailyValue * remainingDays));
                }
                if (nutrientName.equals("Carbohydrates")) {
                    System.out.println("Remaining days (" + remainingDays + ") carbohydrates added: " + (dailyValue * remainingDays));
                }
                if (nutrientName.equals("Total lipid (fat)")) {
                    System.out.println("Remaining days (" + remainingDays + ") fat added: " + (dailyValue * remainingDays));
                }
            });
        }

        // Final cumulative values logging
        System.out.println("Final cumulative kcal value: " + cumulativeIntake.getOrDefault("Energy kcal", 0.0));
        System.out.println("Final cumulative protein value: " + cumulativeIntake.getOrDefault("Protein", 0.0));
        System.out.println("Final cumulative carbohydrates value: " + cumulativeIntake.getOrDefault("Carbohydrates", 0.0));
        System.out.println("Final cumulative fat value: " + cumulativeIntake.getOrDefault("Total lipid (fat)", 0.0));

        return cumulativeIntake;
    }

    private static RecommendedDailyIntake recalculateOriginalDailyIntake(User user) {
        // Calculate the intake based on the user's data
        return DailyIntakeCalculatorUtil.calculateDailyIntake(user);
    }
}

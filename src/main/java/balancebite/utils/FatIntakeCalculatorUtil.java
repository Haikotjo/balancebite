package balancebite.utils;

import balancebite.model.user.User;
import balancebite.model.user.userenums.Goal;

/**
 * Utility class for calculating the recommended daily fat intake.
 */
public class FatIntakeCalculatorUtil {

    /**
     * Calculates the recommended daily fat intake based on the user's total calorie intake and goal.
     *
     * @param user The user for whom the fat intake is being calculated.
     * @param totalEnergyKcal The adjusted daily energy intake in kcal.
     * @return The recommended daily fat intake in grams.
     * @throws IllegalArgumentException If the user's goal is unsupported.
     */
    public static double calculateFatIntake(User user, double totalEnergyKcal) {
        double fatPercentage = determineFatPercentage(user.getGoal());
        double fatKcal = totalEnergyKcal * fatPercentage;
        return fatKcal / 9; // Convert kcal to grams (1 gram of fat = 9 kcal)
    }

    /**
     * Determines the fat percentage based on the user's goal.
     *
     * @param goal The user's goal.
     * @return The fat percentage to be used for calculation.
     * @throws IllegalArgumentException If the goal is unsupported.
     */
    private static double determineFatPercentage(Goal goal) {
        switch (goal) {
            case WEIGHT_LOSS:
                return 0.20; // 20% of total energy for weight loss
            case WEIGHT_LOSS_WITH_MUSCLE_MAINTENANCE:
                return 0.25; // 25% of total energy for weight loss with muscle maintenance
            case MAINTENANCE:
                return 0.25; // 25% of total energy for maintenance
            case MAINTENANCE_WITH_MUSCLE_FOCUS:
                return 0.30; // 30% of total energy for muscle focus
            case WEIGHT_GAIN:
                return 0.30; // 30% of total energy for weight gain
            case WEIGHT_GAIN_WITH_MUSCLE_FOCUS:
                return 0.35; // 35% of total energy for muscle gain with focus
            default:
                throw new IllegalArgumentException("Unsupported goal");
        }
    }
}
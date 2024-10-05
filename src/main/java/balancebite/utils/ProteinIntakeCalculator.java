package balancebite.utils;

import balancebite.model.User;
import balancebite.model.userenums.Goal;
import balancebite.model.userenums.ActivityLevel;

/**
 * Utility class for calculating the recommended daily protein intake.
 */
public class ProteinIntakeCalculator {

    /**
     * Calculates the recommended daily protein intake based on the user's weight, activity level, age, and goal.
     *
     * @param user The user for whom the protein intake is being calculated.
     * @return The recommended daily protein intake in grams.
     * @throws IllegalArgumentException If the user's weight, activity level, or goal is unsupported.
     */
    public static double calculateProteinIntake(User user) {
        double proteinPerKg;

        // Determine base protein requirements based on user's goal
        switch (user.getGoal()) {
            case WEIGHT_LOSS:
            case WEIGHT_LOSS_WITH_MUSCLE_MAINTENANCE:
                proteinPerKg = 2.0; // High protein to maintain muscle during weight loss
                break;
            case MAINTENANCE:
                proteinPerKg = 1.2; // Maintenance with moderate protein needs
                break;
            case MAINTENANCE_WITH_MUSCLE_FOCUS:
                proteinPerKg = 1.5; // Slightly higher for muscle maintenance
                break;
            case WEIGHT_GAIN:
            case WEIGHT_GAIN_WITH_MUSCLE_FOCUS:
                proteinPerKg = 1.8; // Higher protein for muscle gain
                break;
            default:
                throw new IllegalArgumentException("Unsupported goal");
        }

        // Adjust protein requirements based on age and activity level
        if (user.getAge() > 50) {
            proteinPerKg += 0.2; // Increase protein for older users to counteract muscle loss
        }

        switch (user.getActivityLevel()) {
            case LIGHT:
                proteinPerKg += 0.1; // Slight increase for light activity
                break;
            case MODERATE:
                proteinPerKg += 0.2; // Moderate increase for moderate activity
                break;
            case ACTIVE:
            case VERY_ACTIVE:
                proteinPerKg += 0.4; // Significant increase for high activity
                break;
            case SEDENTARY:
            default:
                // No additional protein requirement for sedentary users
                break;
        }

        // Calculate total protein intake in grams
        return proteinPerKg * user.getWeight();
    }
}

package balancebite.utils;

import balancebite.model.User;

/**
 * Utility class for calculating the recommended daily protein intake.
 */
public class ProteinIntakeCalculatorUtil {

    /**
     * Calculates the recommended daily protein intake based on the user's weight,
     * activity level, age, and goal.
     *
     * @param user The user for whom the protein intake is being calculated.
     * @return The recommended daily protein intake in grams.
     * @throws IllegalArgumentException If the user's weight, activity level, or goal is unsupported.
     */
    public static double calculateProteinIntake(User user) {
        double proteinPerKg = 0.0;

        // Determine protein requirements based on activity level
        switch (user.getActivityLevel()) {
            case SEDENTARY:
                proteinPerKg = Math.max(proteinPerKg, 0.8); // 0.8 - 1.0 g/kg for sedentary users
                proteinPerKg = Math.max(proteinPerKg, 1.0);
                break;
            case LIGHT:
                proteinPerKg = Math.max(proteinPerKg, 1.0); // 1.0 - 1.2 g/kg for light activity
                proteinPerKg = Math.max(proteinPerKg, 1.2);
                break;
            case MODERATE:
                proteinPerKg = Math.max(proteinPerKg, 1.2); // 1.2 - 1.6 g/kg for moderate activity
                proteinPerKg = Math.max(proteinPerKg, 1.6);
                break;
            case ACTIVE:
                proteinPerKg = Math.max(proteinPerKg, 1.4); // 1.4 - 2.0 g/kg for active users
                proteinPerKg = Math.max(proteinPerKg, 2.0);
                break;
            case VERY_ACTIVE:
                proteinPerKg = Math.max(proteinPerKg, 1.8); // 1.8 - 2.2 g/kg for very active users
                proteinPerKg = Math.max(proteinPerKg, 2.2);
                break;
            default:
                throw new IllegalArgumentException("Unsupported activity level");
        }

        // Determine protein requirements based on age
        if (user.getAge() > 65) {
            proteinPerKg = Math.max(proteinPerKg, 1.2); // 1.2 - 1.6 g/kg for users older than 65
            proteinPerKg = Math.max(proteinPerKg, 1.6);
        } else if (user.getAge() > 50) {
            proteinPerKg = Math.max(proteinPerKg, 1.2); // 1.2 - 1.5 g/kg for users between 50 and 65
            proteinPerKg = Math.max(proteinPerKg, 1.5);
        } else if (user.getAge() > 30) {
            proteinPerKg = Math.max(proteinPerKg, 1.0); // 1.0 - 1.2 g/kg for users between 30 and 50
            proteinPerKg = Math.max(proteinPerKg, 1.2);
        } else {
            proteinPerKg = Math.max(proteinPerKg, 0.8); // 0.8 - 1.0 g/kg for young adults
            proteinPerKg = Math.max(proteinPerKg, 1.0);
        }

        // Determine protein requirements based on goal
        switch (user.getGoal()) {
            case WEIGHT_LOSS:
                proteinPerKg = Math.max(proteinPerKg, 1.0); // 1.0 - 1.2 g/kg for weight loss
                proteinPerKg = Math.max(proteinPerKg, 1.2);
                break;
            case WEIGHT_LOSS_WITH_MUSCLE_MAINTENANCE:
                proteinPerKg = Math.max(proteinPerKg, 1.2); // 1.2 - 1.6 g/kg for weight loss with muscle maintenance
                proteinPerKg = Math.max(proteinPerKg, 1.6);
                break;
            case MAINTENANCE:
                proteinPerKg = Math.max(proteinPerKg, 1.0); // 1.0 - 1.2 g/kg for maintenance
                proteinPerKg = Math.max(proteinPerKg, 1.2);
                break;
            case MAINTENANCE_WITH_MUSCLE_FOCUS:
                proteinPerKg = Math.max(proteinPerKg, 1.2); // 1.2 - 1.5 g/kg for maintenance with muscle focus
                proteinPerKg = Math.max(proteinPerKg, 1.5);
                break;
            case WEIGHT_GAIN:
                proteinPerKg = Math.max(proteinPerKg, 1.5); // 1.5 - 2.0 g/kg for weight gain
                proteinPerKg = Math.max(proteinPerKg, 2.0);
                break;
            case WEIGHT_GAIN_WITH_MUSCLE_FOCUS:
                proteinPerKg = Math.max(proteinPerKg, 1.8); // 1.8 - 2.2 g/kg for weight gain with muscle focus
                proteinPerKg = Math.max(proteinPerKg, 2.2);
                break;
            default:
                throw new IllegalArgumentException("Unsupported goal");
        }

        // Calculate total protein intake in grams
        return proteinPerKg * user.getWeight();
    }
}

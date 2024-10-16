package balancebite.utils;

import balancebite.model.User;
import balancebite.model.userenums.ActivityLevel;
import balancebite.model.userenums.Goal;
import balancebite.model.userenums.Gender;

/**
 * Utility class for calculating daily intake values such as BMR and TDEE.
 */
public class KcalIntakeCalculatorUtil {

    /**
     * Calculates the Basal Metabolic Rate (BMR) for the user.
     *
     * The BMR is calculated using the Harris-Benedict formula, which differs based on gender.
     *
     * @param user The user for whom the BMR is calculated.
     * @return The BMR value.
     * @throws IllegalArgumentException If the user's gender is unsupported.
     */
    public static double calculateBMR(User user) {
        if (user.getGender() == Gender.MALE) {
            return 88.362 + (13.397 * user.getWeight()) + (4.799 * user.getHeight()) - (5.677 * user.getAge());
        } else if (user.getGender() == Gender.FEMALE) {
            return 447.593 + (9.247 * user.getWeight()) + (3.098 * user.getHeight()) - (4.330 * user.getAge());
        } else {
            throw new IllegalArgumentException("Unsupported gender");
        }
    }

    /**
     * Calculates the Total Daily Energy Expenditure (TDEE) for the user.
     *
     * The TDEE is determined by adjusting the BMR using an activity factor based on the user's activity level.
     *
     * @param user The user for whom the TDEE is calculated.
     * @return The TDEE value.
     */
    public static double calculateTDEE(User user) {
        double bmr = calculateBMR(user);
        double activityFactor = getActivityFactor(user.getActivityLevel());
        return bmr * activityFactor;
    }

    /**
     * Retrieves the activity factor based on the user's activity level.
     *
     * The activity factor is used to adjust the BMR to determine the TDEE.
     *
     * @param activityLevel The activity level of the user.
     * @return The corresponding activity factor.
     * @throws IllegalArgumentException If the activity level is unsupported.
     */
    private static double getActivityFactor(ActivityLevel activityLevel) {
        switch (activityLevel) {
            case SEDENTARY:
                return 1.2;
            case LIGHT:
                return 1.375;
            case MODERATE:
                return 1.55;
            case ACTIVE:
                return 1.725;
            case VERY_ACTIVE:
                return 1.9;
            default:
                throw new IllegalArgumentException("Unsupported activity level");
        }
    }

    /**
     * Adjusts the total daily energy expenditure (TDEE) based on the user's goal.
     *
     * The adjustment varies based on whether the user wants to lose weight, maintain weight, or gain weight.
     *
     * @param tdee The TDEE value.
     * @param goal The user's goal.
     * @return The adjusted calorie value based on the goal.
     * @throws IllegalArgumentException If the goal is unsupported.
     */
    public static double adjustCaloriesForGoal(double tdee, Goal goal) {
        switch (goal) {
            case WEIGHT_LOSS:
                return tdee * 0.85; // 15% calorie deficit for weight loss
            case WEIGHT_LOSS_WITH_MUSCLE_MAINTENANCE:
                return tdee * 0.90; // Moderate deficit for weight loss with muscle maintenance
            case MAINTENANCE:
                return tdee; // No adjustment for maintenance
            case MAINTENANCE_WITH_MUSCLE_FOCUS:
                return tdee * 1.05; // Slight increase for muscle focus
            case WEIGHT_GAIN:
                return tdee * 1.15; // 15% increase for weight gain
            case WEIGHT_GAIN_WITH_MUSCLE_FOCUS:
                return tdee * 1.20; // 20% increase for muscle growth
            default:
                throw new IllegalArgumentException("Unsupported goal");
        }
    }
}

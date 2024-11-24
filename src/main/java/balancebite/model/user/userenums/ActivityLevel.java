package balancebite.model.user.userenums;

/**
 * Enum representing the activity level of a user.
 * This is used to calculate calorie and nutritional requirements based on the user's physical activity.
 *
 * Activity levels include:
 * - SEDENTARY: Little to no exercise
 * - LIGHT: Light exercise or physical activity 1-3 days per week
 * - MODERATE: Moderate exercise or physical activity 3-5 days per week
 * - ACTIVE: Intense exercise 6-7 days per week
 * - VERY_ACTIVE: Intense daily exercise or physical labor
 */
public enum ActivityLevel {

    /**
     * Represents a sedentary lifestyle with little to no exercise.
     * Typically for people with desk jobs or limited movement throughout the day.
     */
    SEDENTARY,

    /**
     * Represents light exercise or physical activity 1-3 days per week.
     * Suitable for people who have some moderate movement, such as walking or light workouts.
     */
    LIGHT,

    /**
     * Represents moderate exercise or physical activity 3-5 days per week.
     * Suitable for people who work out regularly but not intensely.
     */
    MODERATE,

    /**
     * Represents intense exercise 6-7 days per week.
     * Typically for people who follow a rigorous training program or have an active job.
     */
    ACTIVE,

    /**
     * Represents very intense daily exercise or physical labor.
     * Suitable for athletes or individuals with highly demanding physical jobs.
     */
    VERY_ACTIVE
}

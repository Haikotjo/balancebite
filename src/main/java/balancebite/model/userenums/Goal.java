package balancebite.model.userenums;

/**
 * Enum representing the fitness or nutrition goal of a user.
 * This is used to adjust the user's calorie intake based on their specific goals.
 *
 * Goals include:
 * - WEIGHT_LOSS: Reduce body weight by consuming fewer calories
 * - WEIGHT_GAIN: Increase body weight by consuming more calories
 * - MAINTENANCE: Maintain current body weight and composition
 */
public enum Goal {

    /**
     * Represents a goal to lose weight.
     * Users with this goal aim to consume fewer calories than they burn in a day.
     */
    WEIGHT_LOSS,

    /**
     * Represents a goal to gain weight.
     * Users with this goal aim to consume more calories than they burn in a day.
     */
    WEIGHT_GAIN,

    /**
     * Represents a goal to maintain current weight.
     * Users with this goal aim to consume approximately the same amount of calories as they burn in a day.
     */
    MAINTENANCE
}

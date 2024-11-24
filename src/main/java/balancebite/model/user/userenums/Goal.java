package balancebite.model.user.userenums;

/**
 * Enum representing the fitness or nutrition goal of a user.
 * This is used to adjust the user's calorie and protein intake based on their specific goals.
 *
 * Goals include:
 * - WEIGHT_LOSS: Reduce body weight by consuming fewer calories
 * - WEIGHT_GAIN: Increase body weight by consuming more calories
 * - MAINTENANCE: Maintain current body weight and composition
 * - MUSCLE_GAIN: Focus on gaining muscle mass through increased calorie and protein intake
 * - MUSCLE_MAINTENANCE: Focus on maintaining muscle mass while managing calorie intake
 */
public enum Goal {

    /**
     * Represents a goal to lose weight.
     * Users with this goal aim to consume fewer calories than they burn in a day.
     * The focus is primarily on reducing body fat, without necessarily focusing on muscle mass.
     */
    WEIGHT_LOSS,

    /**
     * Represents a goal to lose weight while preserving muscle mass.
     * Users with this goal aim to consume fewer calories but with a higher protein intake
     * to ensure muscle preservation during fat loss.
     */
    WEIGHT_LOSS_WITH_MUSCLE_MAINTENANCE,

    /**
     * Represents a goal to maintain current weight.
     * Users with this goal aim to consume approximately the same amount of calories as they burn in a day.
     * This goal focuses on maintaining both current body weight and composition.
     */
    MAINTENANCE,

    /**
     * Represents a goal to maintain current muscle mass.
     * Users with this goal aim to maintain their muscle mass through adequate protein intake
     * while keeping calorie consumption balanced for weight maintenance.
     */
    MAINTENANCE_WITH_MUSCLE_FOCUS,

    /**
     * Represents a goal to gain weight.
     * Users with this goal aim to consume more calories than they burn in a day,
     * generally leading to an increase in both muscle and fat mass.
     */
    WEIGHT_GAIN,

    /**
     * Represents a goal to gain weight with a focus on increasing muscle mass.
     * Users with this goal aim to consume more calories and protein to ensure that the
     * weight gain is primarily muscle mass rather than fat.
     */
    WEIGHT_GAIN_WITH_MUSCLE_FOCUS
}

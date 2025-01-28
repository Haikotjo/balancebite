package balancebite.model.meal.references;

/**
 * Enum representing different meal types.
 * This helps categorize meals based on the time of day or purpose.
 *
 * Meal types include:
 * - BREAKFAST: Morning meal
 * - LUNCH: Midday meal
 * - DINNER: Evening meal
 * - SNACK: Small meal between main meals
 */
public enum MealType {

    /**
     * Represents a morning meal, typically eaten after waking up.
     */
    BREAKFAST,

    /**
     * Represents a midday meal, usually eaten in the afternoon.
     */
    LUNCH,

    /**
     * Represents an evening meal, typically the last major meal of the day.
     */
    DINNER,

    /**
     * Represents a smaller meal or food item eaten between main meals.
     */
    SNACK
}

package balancebite.errorHandling;

/**
 * Exception thrown when a Meal entity cannot be found in the system.
 *
 * This exception should be used to indicate that a requested meal does not exist
 * in the database, typically when trying to retrieve or associate a meal by ID.
 */
public class MealNotFoundException extends RuntimeException {

    /**
     * Constructs a new MealNotFoundException with the specified detail message.
     *
     * @param message The detail message providing more information about the error.
     */
    public MealNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new MealNotFoundException with a default message for missing meal ID.
     *
     * @param mealId The ID of the meal that was not found.
     */
    public MealNotFoundException(Long mealId) {
        super("Meal not found with ID: " + mealId);
    }
}
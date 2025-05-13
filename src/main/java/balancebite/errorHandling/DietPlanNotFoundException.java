package balancebite.errorHandling;

/**
 * Exception thrown when a DietPlan entity cannot be found in the system.
 *
 * This exception should be used to indicate that a requested diet does not exist
 * in the database, typically when trying to retrieve or modify a diet by ID.
 */
public class DietPlanNotFoundException extends RuntimeException {

    /**
     * Constructs a new DietPlanNotFoundException with the specified detail message.
     *
     * @param message The detail message providing more information about the error.
     */
    public DietPlanNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new DietPlanNotFoundException with a default message for missing dietPlan ID.
     *
     * @param dietPlanId The ID of the dietPlan that was not found.
     */
    public DietPlanNotFoundException(Long dietPlanId) {
        super("DietPlan not found with ID: " + dietPlanId);
    }
}

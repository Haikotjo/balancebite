package balancebite.errorHandling;

/**
 * Exception thrown when a Diet entity cannot be found in the system.
 *
 * This exception should be used to indicate that a requested diet does not exist
 * in the database, typically when trying to retrieve or modify a diet by ID.
 */
public class DietNotFoundException extends RuntimeException {

    /**
     * Constructs a new DietNotFoundException with the specified detail message.
     *
     * @param message The detail message providing more information about the error.
     */
    public DietNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new DietNotFoundException with a default message for missing diet ID.
     *
     * @param dietId The ID of the diet that was not found.
     */
    public DietNotFoundException(Long dietId) {
        super("Diet not found with ID: " + dietId);
    }
}

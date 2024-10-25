package balancebite.errorHandling;

/**
 * Custom exception thrown when an entity is not found in the database.
 */
public class EntityNotFoundException extends RuntimeException {

    /**
     * Constructs a new EntityNotFoundException with the specified detail message.
     *
     * @param message The detail message.
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}

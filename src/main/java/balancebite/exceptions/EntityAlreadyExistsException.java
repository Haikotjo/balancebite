package balancebite.exceptions;

/**
 * Custom exception to be thrown when an entity already exists in the database.
 */
public class EntityAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new EntityAlreadyExistsException with the specified detail message.
     *
     * @param message The detail message.
     */
    public EntityAlreadyExistsException(String message) {
        super(message);
    }
}

package balancebite.exceptions;

/**
 * Exception thrown when a User entity cannot be found in the system.
 *
 * This exception should be used to indicate that a requested user does not exist
 * in the database, typically when trying to retrieve or update a user by ID.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     *
     * @param message The detail message providing more information about the error.
     */
    public UserNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new UserNotFoundException with a default message for missing user ID.
     *
     * @param userId The ID of the user that was not found.
     */
    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId);
    }
}

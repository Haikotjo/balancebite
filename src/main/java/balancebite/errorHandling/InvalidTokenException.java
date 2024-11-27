package balancebite.errorHandling;

/**
 * Custom exception to handle invalid JWT tokens.
 */
public class InvalidTokenException extends RuntimeException {

    /**
     * Constructs a new InvalidTokenException with the specified detail message.
     *
     * @param message The detail message.
     */
    public InvalidTokenException(String message) {
        super(message);
    }
}

package balancebite.exceptions;

/**
 * Custom exception to be thrown when an invalid food item is encountered.
 */
public class InvalidFoodItemException extends RuntimeException {

    /**
     * Constructs a new InvalidFoodItemException with the specified detail message.
     *
     * @param message the detail message.
     */
    public InvalidFoodItemException(String message) {
        super(message);
    }
}

package balancebite.errorHandling;

/**
 * Exception thrown when the daily recommended intake for a user cannot be found.
 */
public class DailyIntakeNotFoundException extends RuntimeException {
    public DailyIntakeNotFoundException(String message) {
        super(message);
    }
}

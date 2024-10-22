package balancebite.errorHandling;

/**
 * Exception thrown when updating the daily recommended intake fails.
 */
public class DailyIntakeUpdateException extends RuntimeException {
    public DailyIntakeUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}

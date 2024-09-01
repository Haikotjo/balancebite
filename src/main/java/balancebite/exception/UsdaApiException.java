package balancebite.exception;

/**
 * Custom exception class to handle errors related to the USDA API.
 */
public class UsdaApiException extends RuntimeException {

    /**
     * Constructs a new UsdaApiException with the specified detail message.
     *
     * @param message The detail message.
     */
    public UsdaApiException(String message) {
        super(message);
    }

    /**
     * Constructs a new UsdaApiException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause The cause of the exception.
     */
    public UsdaApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

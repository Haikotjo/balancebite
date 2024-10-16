package balancebite.errorHandling;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Utility class for handling error responses.
 * This class provides methods to create consistent error ResponseEntity objects across the application.
 */
public class ErrorResponseUtil {

    /**
     * Creates an error ResponseEntity with a given message and HTTP status.
     * Logs the error message for debugging purposes.
     *
     * @param logger  The logger to log the error message.
     * @param message The error message to be logged and included in the response.
     * @param status  The HTTP status for the response.
     * @param <T>     The type of the ResponseEntity body.
     * @return A ResponseEntity containing the error message and HTTP status.
     */
    public static <T> ResponseEntity<T> createErrorResponse(Logger logger, String message, HttpStatus status) {
        if (logger != null) {
            if (status.is5xxServerError()) {
                logger.error(message);
            } else if (status.is4xxClientError()) {
                logger.warn(message);
            }
        }
        return ResponseEntity.status(status).body(null); // Return null for the generic type
    }
}

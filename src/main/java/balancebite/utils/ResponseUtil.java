package balancebite.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class ResponseUtil {

    /**
     * Generates a standardized error response.
     *
     * @param status The HTTP status for the error.
     * @param message The error message to include in the response.
     * @return A ResponseEntity with the error message and status.
     */
    public static ResponseEntity<?> createErrorResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }

    /**
     * Generates a standardized success response.
     *
     * @param body The response body to include in the success response.
     * @param <T> The type of the response body.
     * @return A ResponseEntity with the success response.
     */
    public static <T> ResponseEntity<T> createSuccessResponse(T body) {
        return ResponseEntity.ok(body);
    }

    /**
     * Generates a no-content response.
     *
     * @return A ResponseEntity with a 204 No Content status.
     */
    public static ResponseEntity<?> createNoContentResponse() {
        return ResponseEntity.noContent().build();
    }
}

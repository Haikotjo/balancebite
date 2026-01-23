package balancebite.errorHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user (specifically a Supermarket) tries to add
 * ingredients that do not belong to their specific FoodSource.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalFoodSourceException extends RuntimeException {
    public IllegalFoodSourceException(String message) {
        super(message);
    }
}
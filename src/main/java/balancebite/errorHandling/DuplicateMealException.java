package balancebite.errorHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a template meal with identical ingredients already exists in the system.
 * This exception is used to prevent the creation of duplicate template meals.
 */
@ResponseStatus(HttpStatus.CONFLICT) // Returns a 409 Conflict status
public class DuplicateMealException extends RuntimeException {

    /**
     * Constructs a new DuplicateMealException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception.
     */
    public DuplicateMealException(String message) {
        super(message);
    }
}

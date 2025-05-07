package balancebite.errorHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user attempts to add a duplicate diet copy.
 * This prevents users from duplicating the same diet more than once.
 */
@ResponseStatus(HttpStatus.CONFLICT) // Returns a 409 Conflict status
public class DuplicateDietException extends RuntimeException {

    /**
     * Constructs a new DuplicateDietException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception.
     */
    public DuplicateDietException(String message) {
        super(message);
    }
}

package balancebite.errorHandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user attempts to add a duplicate dietPlan copy.
 * This prevents users from duplicating the same dietPlan more than once.
 */
@ResponseStatus(HttpStatus.CONFLICT) // Returns a 409 Conflict status
public class DuplicateDietPlanException extends RuntimeException {

    /**
     * Constructs a new DuplicateDietPlanException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception.
     */
    public DuplicateDietPlanException(String message) {
        super(message);
    }
}

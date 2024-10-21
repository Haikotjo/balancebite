package balancebite.errorHandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 *
 * This class provides a centralized exception handling mechanism using the {@link ControllerAdvice} annotation.
 * It handles specific exceptions and returns appropriate HTTP responses, which helps to keep controller classes clean.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors when using @Valid in controllers.
     *
     * @param ex The thrown {@link MethodArgumentNotValidException}.
     * @return A ResponseEntity with the details of the validation errors, with a BAD_REQUEST status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handles all other general exceptions.
     *
     * @param e The thrown {@link Exception}.
     * @return A ResponseEntity indicating that a general error occurred, with an INTERNAL_SERVER_ERROR status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception e) {
        // Returns a ResponseEntity with INTERNAL_SERVER_ERROR status and a generic error message
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
    }

    /**
     * Handles cases where an entity already exists in the database.
     *
     * @param e The thrown {@link EntityAlreadyExistsException}.
     * @return A ResponseEntity indicating that the entity already exists, with a BAD_REQUEST status.
     */
    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<String> handleEntityAlreadyExistsException(EntityAlreadyExistsException e) {
        // Returns a ResponseEntity with BAD_REQUEST status and the message from EntityAlreadyExistsException
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    /**
     * Handles cases where a meal is not found in the database.
     *
     * @param e The thrown {@link MealNotFoundException}.
     * @return A ResponseEntity indicating that the meal was not found, with a NOT_FOUND status.
     */
    @ExceptionHandler(MealNotFoundException.class)
    public ResponseEntity<String> handleMealNotFoundException(MealNotFoundException e) {
        // Returns a ResponseEntity with NOT_FOUND status and the message from MealNotFoundException
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    /**
     * Handles cases where a user is not found in the database.
     *
     * @param e The thrown {@link UserNotFoundException}.
     * @return A ResponseEntity indicating that the user was not found, with a NOT_FOUND status.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        // Returns a ResponseEntity with NOT_FOUND status and the message from UserNotFoundException
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}

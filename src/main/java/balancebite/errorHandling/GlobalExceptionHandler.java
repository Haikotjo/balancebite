package balancebite.errorHandling;

import balancebite.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
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
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

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

    /**
     * Handles cases where a daily intake record for the user cannot be found.
     *
     * @param ex The thrown {@link DailyIntakeNotFoundException}.
     * @param request The web request that resulted in the exception.
     * @return A ResponseEntity indicating that the daily intake was not found, with a NOT_FOUND status.
     */
    @ExceptionHandler(DailyIntakeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleDailyIntakeNotFoundException(DailyIntakeNotFoundException ex, WebRequest request) {
        // Returns a ResponseEntity with NOT_FOUND status and the message from DailyIntakeNotFoundException
        String errorMessage = ex.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", errorMessage));
    }

    /**
     * Handles cases where updating the daily intake record fails.
     *
     * @param ex The thrown {@link DailyIntakeUpdateException}.
     * @param request The web request that resulted in the exception.
     * @return A ResponseEntity indicating that updating the daily intake failed, with an INTERNAL_SERVER_ERROR status.
     */
    @ExceptionHandler(DailyIntakeUpdateException.class)
    public ResponseEntity<Map<String, String>> handleDailyIntakeUpdateException(DailyIntakeUpdateException ex, WebRequest request) {
        // Returns a ResponseEntity with INTERNAL_SERVER_ERROR status and the message from DailyIntakeUpdateException
        String errorMessage = ex.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", errorMessage));
    }

    /**
     * Handles cases where the user information required for the daily intake calculation is incomplete.
     *
     * @param ex The thrown {@link MissingUserInformationException}.
     * @param request The web request that resulted in the exception.
     * @return A ResponseEntity indicating that the user information is incomplete, with a BAD_REQUEST status.
     */
    @ExceptionHandler(MissingUserInformationException.class)
    public ResponseEntity<Map<String, String>> handleMissingUserInformationException(MissingUserInformationException ex, WebRequest request) {
        // Returns a ResponseEntity with BAD_REQUEST status and the message from MissingUserInformationException
        String errorMessage = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", errorMessage));
    }

    /**
     * Handles cases where a requested entity is not found in the database.
     *
     * @param ex The thrown {@link EntityNotFoundException}.
     * @param request The web request that resulted in the exception.
     * @return A ResponseEntity indicating that the entity was not found, with a NOT_FOUND status.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        String errorMessage = ex.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", errorMessage));
    }


    /**
     * Handles data integrity violations such as constraint violations.
     *
     * @param ex The thrown {@link DataIntegrityViolationException}.
     * @return A ResponseEntity indicating the conflict, with a CONFLICT status.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String errorMessage = "Data integrity violation: " + ex.getMostSpecificCause().getMessage();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", errorMessage));
    }

    /**
     * Handles errors related to retrieving objects from the database.
     *
     * @param ex The thrown {@link JpaObjectRetrievalFailureException}.
     * @return A ResponseEntity indicating the entity retrieval failure, with a NOT_FOUND status.
     */
    @ExceptionHandler(JpaObjectRetrievalFailureException.class)
    public ResponseEntity<Map<String, String>> handleJpaObjectRetrievalFailureException(JpaObjectRetrievalFailureException ex) {
        String errorMessage = "Database retrieval error: " + ex.getMostSpecificCause().getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", errorMessage));
    }

    /**
     * Handles cases where a duplicate template meal with the same ingredients already exists.
     *
     * @param e The thrown {@link DuplicateMealException}.
     * @return A ResponseEntity indicating that a duplicate template meal exists, with a CONFLICT (409) status.
     */
    @ExceptionHandler(DuplicateMealException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateMealException(DuplicateMealException e) {
        log.warn("DuplicateMealException handler reached for message: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(DietPlanNotFoundException.class)
    public ResponseEntity<String> handleDietNotFoundException(DietPlanNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    /**
     * Handles cases where a duplicate dietPlan already exists for a user.
     *
     * @param e The thrown {@link DuplicateDietPlanException}.
     * @return A ResponseEntity indicating that a duplicate dietPlan exists, with a CONFLICT (409) status.
     */
    @ExceptionHandler(DuplicateDietPlanException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateDietPlanException(DuplicateDietPlanException e) {
        log.warn("DuplicateDietPlanException handler reached for message: {}", e.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MealStillInUseException.class)
    public ResponseEntity<Map<String, Object>> handleMealStillInUseException(MealStillInUseException e) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("error", e.getMessage());
        errorBody.put("diets", e.getDiets()); // hier geef je dus de volledige lijst terug
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody);
    }

    @ExceptionHandler(MealInDietException.class)
    public ResponseEntity<?> handleMealInDietException(MealInDietException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error", ex.getMessage(),
                        "diets", ex.getDietNames()
                ));
    }

    @ExceptionHandler(ForbiddenActionException.class)
    public ResponseEntity<Map<String, String>> handleForbiddenActionException(ForbiddenActionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }
}

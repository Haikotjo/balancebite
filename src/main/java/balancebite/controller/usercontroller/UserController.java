package balancebite.controller.usercontroller;

import balancebite.dto.user.UserRegistrationInputDTO;
import balancebite.dto.user.UserLoginInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserDetailsInputDTO;
import balancebite.errorHandling.DailyIntakeNotFoundException;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.service.user.ConsumeMealService;
import balancebite.service.RecommendedDailyIntakeService;
import balancebite.service.user.UserService;
import balancebite.security.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller responsible for managing user-related actions.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final ConsumeMealService consumeMealService;
    private final RecommendedDailyIntakeService recommendedDailyIntakeService;
    private final JwtService jwtService;

    /**
     * Constructor to initialize the UserController with the necessary services.
     *
     * @param userService                   The service responsible for user-related business logic.
     * @param recommendedDailyIntakeService The service responsible for recommended daily intake logic.
     * @param consumeMealService            The service responsible for handling meal consumption logic.
     */
    public UserController(UserService userService, RecommendedDailyIntakeService recommendedDailyIntakeService, ConsumeMealService consumeMealService, JwtService jwtService) {
        this.userService = userService;
        this.consumeMealService = consumeMealService;
        this.recommendedDailyIntakeService = recommendedDailyIntakeService;
        this.jwtService = jwtService;
    }

    /**
     * Endpoint to update the basic information of the currently logged-in user.
     *
     * @param userRegistrationInputDTO The input data for updating the user.
     * @param authorizationHeader      The Authorization header containing the JWT token.
     * @return The updated UserDTO with 200 status code, or a 404 status code if the user is not found.
     */
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/basic-info")
    public ResponseEntity<?> updateOwnBasicInfo(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody UserRegistrationInputDTO userRegistrationInputDTO) {
        log.info("Updating basic info for the currently logged-in user.");

        try {
            // Extract user ID from the token
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            Long userId = jwtService.extractUserId(token);

            // Call the service method
            UserDTO updatedUser = userService.updateUserBasicInfo(userId, userRegistrationInputDTO);

            log.info("Successfully updated basic info for logged-in user with ID: {}", userId);
            return ResponseEntity.ok(updatedUser);
        } catch (EntityAlreadyExistsException e) {
            log.warn("Entity already exists during user update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (UserNotFoundException e) {
            log.warn("User not found during update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during user update: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint to update the detailed information of an existing user.
     *
     * @param id                  The ID of the user to update.
     * @param userDetailsInputDTO The input data for updating the user's detailed information.
     * @return The updated UserDTO with 200 status code, or a 404 status code if the user is not found.
     */
    @PutMapping("/{id}/details")
    public ResponseEntity<?> updateUserDetails(@PathVariable Long id, @Valid @RequestBody UserDetailsInputDTO userDetailsInputDTO) {
        log.info("Updating details for user with ID: {}", id);
        try {
            UserDTO updatedUser = userService.updateUserDetails(id, userDetailsInputDTO);
            log.info("Successfully updated user details for ID: {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            log.warn("User not found during detail update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (DailyIntakeNotFoundException e) {
            log.warn("Daily intake not found during user detail update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during user detail update: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint to retrieve a specific user by ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The UserDTO with 200 status code, or a 404 status code if the user is not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        log.info("Retrieving user by ID: {}", id);
        try {
            UserDTO user = userService.getUserById(id);
            log.info("Successfully retrieved user with ID: {}", id);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            log.warn("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during user retrieval by ID: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint to delete an existing user by ID.
     *
     * @param id The ID of the user to delete.
     * @return A 204 No Content status code if successful, or a 404 status code with an error message if the user is not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);
        try {
            userService.deleteUser(id);
            log.info("Successfully deleted user with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            log.warn("User not found while attempting to delete: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during user deletion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

}

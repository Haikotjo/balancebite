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
     * Endpoint to update the detailed information of the currently logged-in user.
     *
     * @param authorizationHeader The Authorization header containing the JWT token.
     * @param userDetailsInputDTO The input data for updating the user's detailed information.
     * @return The updated UserDTO with 200 status code, or a 404 status code if the user is not found.
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/details")
    public ResponseEntity<?> updateOwnDetails(
            @RequestHeader("Authorization") String authorizationHeader,
            @Valid @RequestBody UserDetailsInputDTO userDetailsInputDTO) {
        log.info("Updating details for the currently logged-in user.");

        try {
            // Extract user ID from the token
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            Long userId = jwtService.extractUserId(token);

            // Call the service method
            UserDTO updatedUser = userService.updateUserDetails(userId, userDetailsInputDTO);

            log.info("Successfully updated detailed info for logged-in user with ID: {}", userId);
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
     * Endpoint to retrieve the currently logged-in user's details.
     *
     * @param authorizationHeader The Authorization header containing the JWT token.
     * @return The UserDTO with 200 status code, or a 404 status code if the user is not found.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public ResponseEntity<?> getOwnDetails(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("Retrieving details for the currently logged-in user.");

        try {
            // Extract user ID from the token
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            Long userId = jwtService.extractUserId(token);

            // Call the service method
            UserDTO user = userService.getOwnDetails(userId);

            log.info("Successfully retrieved details for logged-in user with ID: {}", userId);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            log.warn("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during user retrieval: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint to delete the currently logged-in user.
     *
     * @param authorizationHeader The Authorization header containing the JWT token.
     * @return A 204 No Content status code if successful, or a 404 status code with an error message if the user is not found.
     */
    @DeleteMapping()
    public ResponseEntity<?> deleteLoggedInUser(@RequestHeader("Authorization") String authorizationHeader) {
        log.info("Deleting the currently logged-in user.");

        try {
            // Extract user ID from the token
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            Long userId = jwtService.extractUserId(token);

            // Call the service method
            userService.deleteLoggedInUser(userId);

            log.info("Successfully deleted logged-in user with ID: {}", userId);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            log.warn("Logged-in user not found while attempting to delete: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during logged-in user deletion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }
}

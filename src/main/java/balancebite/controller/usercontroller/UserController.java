package balancebite.controller.usercontroller;

import balancebite.dto.user.UserBasicInfoInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserDetailsInputDTO;
import balancebite.errorHandling.DailyIntakeNotFoundException;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.service.user.ConsumeMealService;
import balancebite.service.RecommendedDailyIntakeService;
import balancebite.service.user.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller responsible for managing user-related actions.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserService userService;
    private final ConsumeMealService consumeMealService;
    private final RecommendedDailyIntakeService recommendedDailyIntakeService;

    /**
     * Constructor to initialize the UserController with the UserService and other necessary services.
     *
     * @param userService                   The service responsible for user-related business logic.
     * @param recommendedDailyIntakeService  The service responsible for recommended daily intake logic.
     * @param consumeMealService             The service responsible for handling meal consumption logic.
     */
    public UserController(UserService userService, RecommendedDailyIntakeService recommendedDailyIntakeService, ConsumeMealService consumeMealService) {
        this.userService = userService;
        this.consumeMealService = consumeMealService;
        this.recommendedDailyIntakeService = recommendedDailyIntakeService;
    }

    /**
     * Endpoint to create a new user.
     *
     * @param userBasicInfoInputDTO The input data for creating the user.
     * @return The created UserDTO with 201 status code, or a 409 status code if the user already exists.
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserBasicInfoInputDTO userBasicInfoInputDTO) {
        try {
            log.info("Creating a new user with email: {}", userBasicInfoInputDTO.getEmail());
            UserDTO createdUser = userService.createUser(userBasicInfoInputDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (EntityAlreadyExistsException e) {
            log.warn("User already exists: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during user creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint to update the basic information of an existing user.
     *
     * @param id                   The ID of the user to update.
     * @param userBasicInfoInputDTO The input data for updating the user.
     * @return The updated UserDTO with 200 status code, or a 404 status code if the user is not found.
     */
    @PatchMapping("/{id}/basic-info")
    public ResponseEntity<?> updateUserBasicInfo(@PathVariable Long id, @Valid @RequestBody UserBasicInfoInputDTO userBasicInfoInputDTO) {
        try {
            log.info("Updating basic information for user ID: {}", id);
            UserDTO updatedUser = userService.updateUserBasicInfo(id, userBasicInfoInputDTO);
            return ResponseEntity.ok(updatedUser);
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
     * @param id                   The ID of the user to update.
     * @param userDetailsInputDTO  The input data for updating the user's detailed information.
     * @return The updated UserDTO with 200 status code, or a 404 status code if the user is not found.
     */
    @PutMapping("/{id}/details")
    public ResponseEntity<?> updateUserDetails(@PathVariable Long id, @Valid @RequestBody UserDetailsInputDTO userDetailsInputDTO) {
        try {
            log.info("Updating detailed information for user ID: {}", id);
            UserDTO updatedUser = userService.updateUserDetails(id, userDetailsInputDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            log.warn("User not found during detail update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during detail update: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint to retrieve all users.
     *
     * @return A list of UserDTOs with 200 status code, or a 204 status code if no users are found.
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            log.info("Retrieving all users from the system.");
            List<UserDTO> users = userService.getAllUsers();
            if (users.isEmpty()) {
                log.info("No users found.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Unexpected error during user retrieval: {}", e.getMessage(), e);
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
        try {
            log.info("Retrieving user by ID: {}", id);
            UserDTO user = userService.getUserById(id);
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
     * @return A 204 No Content status code if successful, or a 404 status code if the user is not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            log.info("Deleting user with ID: {}", id);
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            log.warn("User not found while attempting to delete: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Unexpected error during user deletion: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

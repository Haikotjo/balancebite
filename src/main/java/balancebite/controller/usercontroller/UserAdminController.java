package balancebite.controller.usercontroller;

import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserRegistrationInputDTO;
import balancebite.dto.user.UserRoleUpdateDTO;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.service.RecommendedDailyIntakeService;
import balancebite.service.user.ConsumeMealService;
import balancebite.service.user.UserAdminService;
import balancebite.service.user.UserService;
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
@RequestMapping("/admins")
public class UserAdminController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserAdminService userAdminService;
    private final ConsumeMealService consumeMealService;
    private final RecommendedDailyIntakeService recommendedDailyIntakeService;

    /**
     * Constructor to initialize the UserController with the necessary services.
     *
     * @param userAdminService                   The service responsible for user-related business logic.
     * @param recommendedDailyIntakeService The service responsible for recommended daily intake logic.
     * @param consumeMealService            The service responsible for handling meal consumption logic.
     */
    public UserAdminController(UserAdminService userAdminService, RecommendedDailyIntakeService recommendedDailyIntakeService, ConsumeMealService consumeMealService) {
        this.userAdminService = userAdminService;
        this.consumeMealService = consumeMealService;
        this.recommendedDailyIntakeService = recommendedDailyIntakeService;
    }

    /**
     * Endpoint to update the basic information of an existing user.
     * Only accessible to admins.
     *
     * @param userRegistrationInputDTO The input data for updating the user, including the ID of the user to update.
     * @return The updated UserDTO with 200 status code, or a 404 status code if the user is not found.
     */
    @PreAuthorize("hasRole('ADMIN')") // Ensures only users with ADMIN role can access this endpoint
    @PatchMapping("/users/update-basic-info")
    public ResponseEntity<?> updateUserBasicInfo(@Valid @RequestBody UserRegistrationInputDTO userRegistrationInputDTO) {
        log.info("Updating basic info for user with ID: {}", userRegistrationInputDTO.getId());
        try {
            // Call the service method using the provided ID
            UserDTO updatedUser = userAdminService.updateUserBasicInfoForAdmin(userRegistrationInputDTO);
            log.info("Successfully updated user with ID: {}", userRegistrationInputDTO.getId());
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
     * Endpoint to retrieve all users.
     *
     * @return A list of UserDTOs with 200 status code, or a 204 status code if no users are found.
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        log.info("Retrieving all users from the system.");
        try {
            List<UserDTO> users = userAdminService.getAllUsers();
            if (users.isEmpty()) {
                log.info("No users found in the system.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            log.info("Retrieved {} users from the system.", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Unexpected error during user retrieval: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint to delete an existing user by ID.
     *
     * @param userRegistrationInputDTO The input DTO containing the user ID to delete.
     * @return A 204 No Content status code if successful, or a 404 status code with an error message if the user is not found.
     */
    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser(@RequestBody UserRegistrationInputDTO userRegistrationInputDTO) {
        Long id = userRegistrationInputDTO.getId();
        log.info("Deleting user with ID: {}", id);
        try {
            userAdminService.deleteUser(id);
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

    @PatchMapping("/users/update-role")
    public ResponseEntity<?> updateUserRoles(@Valid @RequestBody UserRoleUpdateDTO dto) {
        log.info("Updating roles for user with email: {}", dto.getEmail());
        try {
            userAdminService.updateUserRolesByEmail(dto.getEmail(), dto.getRoles());
            log.info("Successfully updated roles for user: {}", dto.getEmail());
            return ResponseEntity.ok(Map.of("message", "User roles updated successfully"));
        } catch (UserNotFoundException e) {
            log.warn("User not found during role update: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            log.warn("Invalid role data provided: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during role update: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    @PostMapping("/create-user")
    public ResponseEntity<?> registerUserAsAdmin(@Valid @RequestBody UserRegistrationInputDTO dto) {
        log.info("Admin attempting to register new user: {}", dto.getEmail());
        try {
            userAdminService.registerUserAsAdmin(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User created successfully."));
        } catch (EntityAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during admin user creation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

}

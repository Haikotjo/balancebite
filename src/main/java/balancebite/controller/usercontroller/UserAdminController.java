package balancebite.controller.usercontroller;

import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserRegistrationInputDTO;
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
     * @param id                   The ID of the user to update.
     * @param userRegistrationInputDTO The input data for updating the user.
     * @return The updated UserDTO with 200 status code, or a 404 status code if the user is not found.
     */
    @PreAuthorize("hasRole('ADMIN')") // Ensures only users with ADMIN role can access this endpoint
    @PatchMapping("/{id}/basic-info")
    public ResponseEntity<?> updateUserBasicInfo(@PathVariable Long id, @Valid @RequestBody UserRegistrationInputDTO userRegistrationInputDTO) {
        log.info("Updating basic info for user with ID: {}", id);
        try {
            UserDTO updatedUser = userAdminService.updateUserBasicInfoForAdmin(id, userRegistrationInputDTO); // Removed boolean
            log.info("Successfully updated user with ID: {}", id);
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
     * Endpoint to retrieve all users.
     *
     * @return A list of UserDTOs with 200 status code, or a 204 status code if no users are found.
     */
    @GetMapping
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
}

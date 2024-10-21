package balancebite.controller;

import balancebite.dto.user.UserBasicInfoInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserDetailsInputDTO;
import balancebite.model.RecommendedDailyIntake;
import balancebite.service.RecommendedDailyIntakeService;
import balancebite.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
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

    private final UserService userService;
    private final RecommendedDailyIntakeService recommendedDailyIntakeService;  // Injecteer RecommendedDailyIntakeService

    /**
     * Constructor to initialize the UserController with the UserService and RecommendedDailyIntakeService.
     *
     * @param userService The service responsible for user-related business logic.
     * @param recommendedDailyIntakeService The service responsible for recommended daily intake logic.
     */
    public UserController(UserService userService, RecommendedDailyIntakeService recommendedDailyIntakeService) {
        this.userService = userService;
        this.recommendedDailyIntakeService = recommendedDailyIntakeService;  // Constructor injectie voor RecommendedDailyIntakeService
    }

    /**
     * Endpoint to create a new user.
     * Meals are not added during user creation.
     *
     * @param userBasicInfoInputDTO The input data for creating the user.
     * @return The created UserDTO and 201 status code.
     */
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserBasicInfoInputDTO userBasicInfoInputDTO) {
        try {
            UserDTO createdUser = userService.createUser(userBasicInfoInputDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (EntityExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    /**
     * Endpoint to update the basic information of an existing user.
     *
     * @param id The ID of the user to update.
     * @param userBasicInfoInputDTO The input data for updating the user.
     * @return The updated UserDTO and 200 status code if successful.
     */
    @PatchMapping("/{id}/basic-info")
    public ResponseEntity<?> updateUserBasicInfoEndpoint(@PathVariable Long id, @Valid @RequestBody UserBasicInfoInputDTO userBasicInfoInputDTO) {
        try {
            UserDTO updatedUser = userService.updateUserBasicInfo(id, userBasicInfoInputDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    /**
     * Endpoint to update the detailed information of an existing user.
     *
     * @param id The ID of the user to update.
     * @param userDetailsInputDTO The input data for updating the user's detailed information.
     * @return The updated UserDTO and 200 status code if successful.
     */
    @PutMapping("/{id}/details")
    public ResponseEntity<?> updateUserDetailsEndpoint(@PathVariable Long id, @Valid @RequestBody UserDetailsInputDTO userDetailsInputDTO) {
        try {
            UserDTO updatedUser = userService.updateUserDetails(id, userDetailsInputDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    /**
     * Endpoint to retrieve all users.
     *
     * @return A list of UserDTOs and 200 status code if successful.
     */
    @GetMapping
    public ResponseEntity<?> getAllUsersEndpoint() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            // Handle any unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    /**
     * Endpoint to retrieve a specific user by ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The UserDTO and 200 status code.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            UserDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    /**
     * Endpoint to delete an existing user by ID.
     *
     * @param id The ID of the user to delete.
     * @return A 204 No Content status code if successful.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint to add an existing meal to a user's list of meals.
     *
     * @param userId The ID of the user to whom the meal will be added.
     * @param mealId The ID of the meal to be added.
     * @return The updated UserDTO and a 200 status code if successful.
     */
    @PatchMapping("/{userId}/meals/{mealId}")
    public ResponseEntity<UserDTO> addMealToUserEndpoint(@PathVariable Long userId, @PathVariable Long mealId) {
        try {
            UserDTO user = userService.addMealToUser(userId, mealId);
            return ResponseEntity.ok(user);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Endpoint to remove a meal from the user's list of meals.
     *
     * @param userId The ID of the user.
     * @param mealId The ID of the meal to remove.
     * @return A 200 OK status code if the meal was removed successfully.
     */
    @DeleteMapping("/{userId}/meals/{mealId}")
    public ResponseEntity<UserDTO> removeMealFromUser(@PathVariable Long userId, @PathVariable Long mealId) {
        UserDTO updatedUser = userService.removeMealFromUser(userId, mealId);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/{userId}/eat-meal/{mealId}")
    public ResponseEntity<Map<String, Double>> eatMeal(@PathVariable Long userId, @PathVariable Long mealId) {
        Map<String, Double> remainingIntakes = userService.eatMeal(userId, mealId);
        return ResponseEntity.ok(remainingIntakes);
    }
}

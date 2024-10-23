package balancebite.controller;

import balancebite.dto.user.UserBasicInfoInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserDetailsInputDTO;
import balancebite.errorHandling.DailyIntakeNotFoundException;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.service.ConsumeMealService;
import balancebite.service.RecommendedDailyIntakeService;
import balancebite.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
    private final RecommendedDailyIntakeService recommendedDailyIntakeService;  // Injecteer RecommendedDailyIntakeService

    /**
     * Constructor to initialize the UserController with the UserService and RecommendedDailyIntakeService.
     *
     * @param userService The service responsible for user-related business logic.
     * @param recommendedDailyIntakeService The service responsible for recommended daily intake logic.
     */
    public UserController(UserService userService, RecommendedDailyIntakeService recommendedDailyIntakeService, ConsumeMealService consumeMealService) {
        this.userService = userService;
        this.consumeMealService = consumeMealService;
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
        } catch (EntityAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());  // Return 409 Conflict for existing resource
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
    public ResponseEntity<?> updateUserBasicInfo(@PathVariable Long id, @Valid @RequestBody UserBasicInfoInputDTO userBasicInfoInputDTO) {
        try {
            UserDTO updatedUser = userService.updateUserBasicInfo(id, userBasicInfoInputDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException  e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Log the exception for debugging purposes
            log.error("Unexpected error occurred while updating user information: {}", e.getMessage());
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
    public ResponseEntity<?> updateUserDetails(@PathVariable Long id, @Valid @RequestBody UserDetailsInputDTO userDetailsInputDTO) {
        try {
            UserDTO updatedUser = userService.updateUserDetails(id, userDetailsInputDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Log the exception for debugging purposes
            log.error("Unexpected error occurred while updating user details: {}", e.getMessage());
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
            if (users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // Return 204 if no users are found
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            // Log the unexpected error
            log.error("Unexpected error occurred while retrieving all users: {}", e.getMessage());
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
        } catch (UserNotFoundException e) {
            log.error("User not found: {}", e.getMessage()); // Log the error for debugging
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while retrieving user by ID: {}", e.getMessage()); // Log the unexpected error
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
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            log.error("User not found while attempting to delete: {}", e.getMessage()); // Log the error for debugging
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting user: {}", e.getMessage()); // Log the unexpected error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint to add an existing meal to a user's list of meals.
     *
     * @param userId The ID of the user to whom the meal will be added.
     * @param mealId The ID of the meal to be added.
     * @return The updated UserDTO and a 200 status code if successful.
     */
    @PatchMapping("/{userId}/meals/{mealId}")
    public ResponseEntity<UserDTO> addMealToUser(@PathVariable Long userId, @PathVariable Long mealId) {
        try {
            UserDTO user = userService.addMealToUser(userId, mealId);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            log.error("User not found: {}", e.getMessage()); // Log the error for debugging
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (MealNotFoundException e) {
            log.error("Meal not found: {}", e.getMessage()); // Log the error for debugging
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("Unexpected error occurred while adding meal to user: {}", e.getMessage()); // Log unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Endpoint to remove a meal from the user's list of meals.
     * The user can only delete meals that they have added to their own list.
     *
     * @param userId The ID of the user requesting the deletion.
     * @param mealId The ID of the meal to be deleted from the user's list.
     * @return ResponseEntity containing a status message indicating the result of the operation.
     */
    @DeleteMapping("/{userId}/meals/{mealId}")
    public ResponseEntity<?> removeMealFromUser(@PathVariable Long userId, @PathVariable Long mealId) {
        try {
            UserDTO updatedUser = userService.removeMealFromUser(userId, mealId);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            log.error("User not found: {}", e.getMessage()); // Log the error for debugging
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (MealNotFoundException e) {
            log.error("Meal not found: {}", e.getMessage()); // Log the error for debugging
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error occurred while removing meal from user: {}", e.getMessage()); // Log unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    /**
     * Endpoint for processing the consumption of a meal by a user.
     * This method will call the "eatMeal" function from the ConsumeMealService.
     *
     * @param userId The ID of the user consuming the meal.
     * @param mealId The ID of the meal being consumed.
     * @return A ResponseEntity containing the remaining daily intake for each nutrient after meal consumption.
     */
    @PostMapping("/{userId}/consume-meal/{mealId}")
    public ResponseEntity<Object> consumeMeal(@PathVariable Long userId, @PathVariable Long mealId) {
        try {
            Map<String, Double> remainingIntakes = consumeMealService.consumeMeal(userId, mealId);
            return ResponseEntity.ok(remainingIntakes);
        } catch (UserNotFoundException | MealNotFoundException e) {
            log.error("Error occurred while processing meal consumption: {}", e.getMessage(), e);
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (DailyIntakeNotFoundException e) {
            log.error("Daily intake not found for user ID: {}, message: {}", userId, e.getMessage(), e);
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error occurred: {}", e.getMessage(), e);
            Map<String, String> errorResponse = Map.of("error", "An unexpected error occurred. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

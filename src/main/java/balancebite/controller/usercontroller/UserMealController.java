package balancebite.controller.usercontroller;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.DailyIntakeNotFoundException;
import balancebite.errorHandling.DuplicateMealException;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.service.user.ConsumeMealService;
import balancebite.service.user.UserMealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller responsible for managing user-meal-related actions.
 */
@RestController
@RequestMapping("/users")
public class UserMealController {

    private static final Logger log = LoggerFactory.getLogger(UserMealController.class);

    private final UserMealService userMealService;
    private final ConsumeMealService consumeMealService;

    /**
     * Constructor to initialize the UserMealController with the necessary services.
     *
     * @param userMealService The service responsible for adding and removing meals for a user.
     * @param consumeMealService The service responsible for handling meal consumption logic.
     */
    public UserMealController(UserMealService userMealService, ConsumeMealService consumeMealService) {
        this.userMealService = userMealService;
        this.consumeMealService = consumeMealService;
    }

    /**
     * Endpoint to add an existing meal to a user's list of meals.
     *
     * @param userId The ID of the user to whom the meal will be added.
     * @param mealId The ID of the meal to be added.
     * @return The updated UserDTO with 200 status code, or a 404 status code if the user or meal is not found.
     */
    @PatchMapping("/{userId}/meals/{mealId}")
    public ResponseEntity<?> addMealToUser(@PathVariable Long userId, @PathVariable Long mealId) {
        try {
            log.info("Adding meal ID {} to user ID {}", mealId, userId);
            UserDTO user = userMealService.addMealToUser(userId, mealId);
            return ResponseEntity.ok(user);
        } catch (DuplicateMealException e) {
            log.warn("Duplicate meal detected for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (UserNotFoundException | MealNotFoundException e) {
            log.warn("Error occurred during meal addition: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during meal addition: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Retrieves all meals for a specific user by user ID.
     *
     * @param userId the ID of the user
     * @return ResponseEntity containing a list of MealDTO objects representing the user's meals,
     *         or a 204 NO CONTENT if no meals are found for the user
     */
    @GetMapping("/{userId}/meals")
    public ResponseEntity<?> getAllMealsForUser(@PathVariable Long userId) {
        try {
            log.info("Retrieving all meals for user with ID: {}", userId);
            List<MealDTO> mealDTOs = userMealService.getAllMealsForUser(userId);
            if (mealDTOs.isEmpty()) {
                log.info("No meals found for user with ID: {}", userId);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(mealDTOs);
        } catch (Exception e) {
            log.error("Unexpected error during retrieval of meals for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint to remove a meal from the user's list of meals.
     *
     * @param userId The ID of the user.
     * @param mealId The ID of the meal to remove.
     * @return The updated UserDTO with 200 status code, or a 404 status code if the user or meal is not found.
     */
    @DeleteMapping("/{userId}/meals/{mealId}")
    public ResponseEntity<?> removeMealFromUser(@PathVariable Long userId, @PathVariable Long mealId) {
        try {
            log.info("Removing meal ID {} from user ID {}", mealId, userId);
            UserDTO updatedUser = userMealService.removeMealFromUser(userId, mealId);
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException | MealNotFoundException e) {
            log.warn("Error occurred during meal removal: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during meal removal: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }



    /**
     * Endpoint for processing the consumption of a meal by a user.
     *
     * @param userId The ID of the user consuming the meal.
     * @param mealId The ID of the meal being consumed.
     * @return A ResponseEntity containing the remaining daily intake for each nutrient after meal consumption.
     */
    @PostMapping("/{userId}/consume-meal/{mealId}")
    public ResponseEntity<?> consumeMeal(@PathVariable Long userId, @PathVariable Long mealId) {
        try {
            log.info("Processing meal consumption for user ID {} and meal ID {}", userId, mealId);
            Map<String, Double> remainingIntakes = consumeMealService.consumeMeal(userId, mealId);
            return ResponseEntity.ok(remainingIntakes);
        } catch (UserNotFoundException | MealNotFoundException | DailyIntakeNotFoundException e) {
            log.warn("Error occurred during meal consumption: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during meal consumption: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }
}

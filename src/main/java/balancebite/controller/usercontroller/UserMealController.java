package balancebite.controller.usercontroller;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.*;
import balancebite.service.user.ConsumeMealService;
import balancebite.service.user.UserMealService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller responsible for managing user-meal-related actions.
 * Provides endpoints to create, update, add, remove, and consume meals for users.
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
     * @param userMealService     The service responsible for managing meals for a user.
     * @param consumeMealService  The service responsible for handling meal consumption logic.
     */
    public UserMealController(UserMealService userMealService, ConsumeMealService consumeMealService) {
        this.userMealService = userMealService;
        this.consumeMealService = consumeMealService;
    }

    /**
     * Creates a new Meal entity for a specific user based on the provided MealInputDTO.
     *
     * @param mealInputDTO The input data for creating the meal.
     * @param userId       The ID of the user to associate the meal with.
     * @return ResponseEntity containing the created MealDTO with 201 status code, or an error response with an appropriate status.
     */
    @PostMapping("/{userId}/meal")
    public ResponseEntity<?> createMealForUser(@RequestBody MealInputDTO mealInputDTO, @PathVariable Long userId) {
        try {
            log.info("Creating new meal for user ID: {}", userId);
            MealDTO createdMeal = userMealService.createMealForUser(mealInputDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMeal);
        } catch (DuplicateMealException e) {
            log.warn("Duplicate meal detected for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            log.warn("User not found with ID {} during meal creation: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (InvalidFoodItemException e) {
            log.warn("Invalid food item for user meal creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during meal creation for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Updates a user's meal by ID, only allowing updates to meals in their list.
     *
     * @param userId       The ID of the user.
     * @param mealId       The ID of the meal to update.
     * @param mealInputDTO The new details of the meal.
     * @return ResponseEntity containing the updated MealDTO with 200 status code, or an error response with an appropriate status.
     */
    @PatchMapping("/{userId}/update-meal/{mealId}")
    public ResponseEntity<?> updateUserMeal(@PathVariable Long userId, @PathVariable Long mealId, @RequestBody MealInputDTO mealInputDTO) {
        try {
            log.info("Updating meal with ID: {} for user ID: {}", mealId, userId);
            MealDTO updatedMeal = userMealService.updateUserMeal(userId, mealId, mealInputDTO);
            return ResponseEntity.ok(updatedMeal);
        } catch (DuplicateMealException e) {
            log.warn("Duplicate meal detected during update for user ID {} and meal ID {}: {}", userId, mealId, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            log.warn("User or meal not found for user ID {} and meal ID {}: {}", userId, mealId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (InvalidFoodItemException e) {
            log.warn("Invalid food item in meal update for user ID {} and meal ID {}: {}", userId, mealId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during meal update for user ID {} and meal ID {}: {}", userId, mealId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Adds an existing meal to a user's list of meals.
     *
     * @param userId The ID of the user to whom the meal will be added.
     * @param mealId The ID of the meal to be added.
     * @return ResponseEntity containing the updated UserDTO with 200 status code, or an error response with an appropriate status.
     */
    @PatchMapping("/{userId}/add-meal/{mealId}")
    public ResponseEntity<?> addMealToUser(@PathVariable Long userId, @PathVariable Long mealId) {
        try {
            log.info("Adding meal ID {} to user ID {}", mealId, userId);
            UserDTO user = userMealService.addMealToUser(userId, mealId);
            return ResponseEntity.ok(user);
        } catch (DuplicateMealException e) {
            log.warn("Duplicate meal detected for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (UserNotFoundException | MealNotFoundException e) {
            log.warn("Error occurred during meal addition for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during meal addition for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Retrieves all meals for a specific user by user ID.
     *
     * @param userId The ID of the user.
     * @return ResponseEntity containing a list of MealDTO objects representing the user's meals,
     *         or a 204 NO CONTENT if no meals are found for the user.
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
     * Removes a meal from the user's list of meals.
     *
     * @param userId The ID of the user.
     * @param mealId The ID of the meal to remove.
     * @return ResponseEntity with 204 status if successful, or an error response with an appropriate status.
     */
    @DeleteMapping("/{userId}/meals/{mealId}")
    public ResponseEntity<?> removeMealFromUser(@PathVariable Long userId, @PathVariable Long mealId) {
        try {
            log.info("Removing meal ID {} from user ID {}", mealId, userId);
            userMealService.removeMealFromUser(userId, mealId);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException | MealNotFoundException e) {
            log.warn("Error occurred during meal removal for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during meal removal for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Processes the consumption of a meal by a user.
     *
     * @param userId The ID of the user consuming the meal.
     * @param mealId The ID of the meal being consumed.
     * @return ResponseEntity containing the remaining daily intake for each nutrient after meal consumption.
     */
    @PostMapping("/{userId}/consume-meal/{mealId}")
    public ResponseEntity<?> consumeMeal(@PathVariable Long userId, @PathVariable Long mealId) {
        try {
            log.info("Processing meal consumption for user ID {} and meal ID {}", userId, mealId);
            Map<String, Double> remainingIntakes = consumeMealService.consumeMeal(userId, mealId);
            return ResponseEntity.ok(remainingIntakes);
        } catch (UserNotFoundException | MealNotFoundException | DailyIntakeNotFoundException e) {
            log.warn("Error occurred during meal consumption for user ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during meal consumption for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }
}

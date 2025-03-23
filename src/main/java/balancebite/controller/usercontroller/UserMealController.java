package balancebite.controller.usercontroller;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.*;
import balancebite.security.JwtService;
import balancebite.service.user.ConsumeMealService;
import balancebite.service.user.UserMealService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final JwtService jwtService;

    /**
     * Constructor to initialize the UserMealController with the necessary services.
     *
     * @param userMealService     The service responsible for managing meals for a user.
     * @param consumeMealService  The service responsible for handling meal consumption logic.
     */
    public UserMealController(UserMealService userMealService, ConsumeMealService consumeMealService, JwtService jwtService) {
        this.userMealService = userMealService;
        this.consumeMealService = consumeMealService;
        this.jwtService = jwtService;
    }

    @PostMapping(value = "/create-meal", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createMealForAuthenticatedUser(
            @RequestPart("mealInputDTO") String mealInputDTOJson,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            log.info("Received request to create a new meal for the authenticated user.");

            // Parse JSON string to MealInputDTO
            ObjectMapper objectMapper = new ObjectMapper();
            MealInputDTO mealInputDTO = objectMapper.readValue(mealInputDTOJson, MealInputDTO.class);

            // Attach the file if present
            if (imageFile != null && !imageFile.isEmpty()) {
                mealInputDTO.setImageFile(imageFile);
            }

            // Extract user ID from token
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            Long userId = jwtService.extractUserId(token);

            // Call the service layer
            MealDTO createdMeal = userMealService.createMealForUser(mealInputDTO, userId);

            log.info("Successfully created meal for user ID: {}", userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMeal);
        } catch (EntityNotFoundException e) {
            log.warn("User not found or invalid: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error occurred during meal creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Adds an existing meal to the authenticated user's list of meals.
     *
     * @param mealId The ID of the meal to be added.
     * @param authorizationHeader The Authorization header containing the JWT token.
     * @return ResponseEntity containing the updated UserDTO with 200 status code, or an error response.
     */
    @PatchMapping("/add-meal/{mealId}")
    public ResponseEntity<?> addMealToAuthenticatedUser(@PathVariable Long mealId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            log.info("Received request to add meal with ID: {} to the authenticated user's list.", mealId);

            // 🔥 Extract userId from the token (identiek aan createMealForUser)
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            Long userId = jwtService.extractUserId(token);

            // Call the service layer with userId
            UserDTO user = userMealService.addMealToUser(userId, mealId);

            log.info("Successfully added meal with ID: {} to authenticated user ID: {}", mealId, userId);
            return ResponseEntity.ok(user);

        } catch (DuplicateMealException e) {
            log.warn("Duplicate meal detected for authenticated user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));

        } catch (UserNotFoundException | MealNotFoundException e) {
            log.warn("Error occurred during meal addition for authenticated user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error occurred while adding meal to authenticated user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Updates a user's meal by ID, only allowing updates to meals in their list.
     *
     * @param mealId       The ID of the meal to update.
     * @param mealInputDTO The new details of the meal.
     * @param authorizationHeader The Authorization header containing the JWT token.
     * @return ResponseEntity containing the updated MealDTO with 200 status code, or an error response with an appropriate status.
     */
    @PatchMapping("/update-meal/{mealId}")
    public ResponseEntity<?> updateUserMeal(
            @PathVariable Long mealId,
            @RequestBody MealInputDTO mealInputDTO,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            log.info("Received request to update meal with ID: {}", mealId);

            // Extract userId from the token
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            Long userId = jwtService.extractUserId(token);

            // Call the service method
            MealDTO updatedMeal = userMealService.updateUserMeal(userId, mealId, mealInputDTO);

            log.info("Successfully updated meal with ID: {} for user ID: {}", mealId, userId);
            return ResponseEntity.ok(updatedMeal);

        } catch (DuplicateMealException e) {
            log.warn("Duplicate meal detected during update for meal ID {}: {}", mealId, e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));

        } catch (EntityNotFoundException e) {
            log.warn("User or meal not found for meal ID {}: {}", mealId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));

        } catch (InvalidFoodItemException e) {
            log.warn("Invalid food item in meal update for meal ID {}: {}", mealId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error during meal update for meal ID {}: {}", mealId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Retrieves paginated and sorted meals for the authenticated user with optional filtering.
     *
     * Users can filter meals by cuisine, diet, meal type, and food items.
     * Meals can be sorted by name, total calories, protein, fat, or carbs.
     * Results are paginated.
     *
     * @param authorizationHeader The Authorization header containing the JWT token.
     * @param cuisines (Optional) Filter for meal cuisine.
     * @param diets (Optional) Filter for meal diet.
     * @param mealTypes (Optional) Filter for meal type (BREAKFAST, LUNCH, etc.).
     * @param foodItems (Optional) List of food items to filter meals by (comma-separated).
     * @param sortBy (Optional) Sorting field (calories, protein, fat, carbs, name).
     * @param sortOrder (Optional) Sorting order ("asc" for ascending, "desc" for descending).
     * @param pageable Pageable object for pagination and sorting.
     * @return ResponseEntity containing a paginated list of MealDTO objects matching the filters.
     */
    @GetMapping("/meals")
    public ResponseEntity<Page<MealDTO>> getAllMealsForAuthenticatedUser(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(required = false) List<String> cuisines,
            @RequestParam(required = false) List<String> diets,
            @RequestParam(required = false) List<String> mealTypes,

            @RequestParam(required = false) List<String> foodItems,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            Pageable pageable
    ) {
        try {
            log.info("Retrieving meals for authenticated user with filters and sorting.");

            // Extract userId from the JWT token
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            Long userId = jwtService.extractUserId(token);

            // Fetch meals with filtering, sorting, and pagination
            Page<MealDTO> mealDTOs = userMealService.getAllMealsForUser(
                    userId, cuisines, diets, mealTypes, foodItems, sortBy, sortOrder, pageable
            );

            if (mealDTOs.isEmpty()) {
                log.info("No meals found for authenticated user ID: {}", userId);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            log.info("Successfully retrieved meals for authenticated user ID: {}", userId);
            return ResponseEntity.ok(mealDTOs);

        } catch (UserNotFoundException e) {
            log.warn("User not found during meal retrieval: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Page.empty());
        }
        catch (Exception e) {
            log.error("Unexpected error during meal retrieval: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Page.empty());
        }
    }

    /**
     * Retrieves paginated and sorted meals created by the authenticated user with optional filtering.
     *
     * Users can filter meals by cuisine, diet, meal type, and food items.
     * Meals can be sorted by name, total calories, protein, fat, or carbs.
     * Results are paginated.
     *
     * @param authorizationHeader The Authorization header containing the JWT token.
     * @param cuisines (Optional) Filter for meal cuisine.
     * @param diets (Optional) Filter for meal diet.
     * @param mealTypes (Optional) Filter for meal type (BREAKFAST, LUNCH, etc.).
     * @param foodItems (Optional) List of food items to filter meals by (comma-separated).
     * @param sortBy (Optional) Sorting field (calories, protein, fat, carbs, name).
     * @param sortOrder (Optional) Sorting order ("asc" for ascending, "desc" for descending).
     * @param pageable Pageable object for pagination and sorting.
     * @return ResponseEntity containing a paginated list of MealDTO objects matching the filters.
     */
    @GetMapping("/created-meals")
    public ResponseEntity<Page<MealDTO>> getMealsCreatedByAuthenticatedUser(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(required = false) List<String> cuisines,
            @RequestParam(required = false) List<String> diets,
            @RequestParam(required = false) List<String> mealTypes,

            @RequestParam(required = false) List<String> foodItems,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            Pageable pageable
    ) {
        try {
            log.info("Retrieving meals created by the authenticated user with filters and sorting.");

            // Extract userId from the JWT token
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            Long userId = jwtService.extractUserId(token);

            // Fetch meals with filtering, sorting, and pagination
            Page<MealDTO> mealDTOs = userMealService.getMealsCreatedByUser(
                    userId, cuisines, diets, mealTypes, foodItems, sortBy, sortOrder, pageable
            );

            if (mealDTOs.isEmpty()) {
                log.info("No meals created by authenticated user ID: {}", userId);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            log.info("Successfully retrieved meals created by authenticated user ID: {}", userId);
            return ResponseEntity.ok(mealDTOs);

        } catch (UserNotFoundException e) {
            log.warn("User not found during meal retrieval: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Page.empty());
        }
        catch (Exception e) {
            log.error("Unexpected error during meal retrieval: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Page.empty());
        }
    }

    /**
     * Retrieves a Meal by its ID, only if it belongs to the authenticated user.
     *
     * @param id                  The ID of the Meal.
     * @param authorizationHeader The Authorization header containing the JWT token.
     * @return ResponseEntity containing the MealDTO with 200 status code, or an error response with an appropriate status.
     */
    @GetMapping("/meal/{id}")
    public ResponseEntity<?> getUserMealById(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            log.info("Received request to retrieve user-specific meal with ID: {}", id);

            // Extract userId from the token
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            Long userId = jwtService.extractUserId(token);
            log.debug("Extracted user ID {} from token.", userId);

            // Call the service method
            MealDTO mealDTO = userMealService.getUserMealById(id, userId);

            log.info("Successfully retrieved user-specific meal with ID: {}", id);
            return ResponseEntity.ok(mealDTO);

        } catch (EntityNotFoundException e) {
            log.warn("Meal with ID {} not found or not associated with user: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error occurred while retrieving meal with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Removes a meal from the authenticated user's list of meals.
     *
     * @param mealId The ID of the meal to remove.
     * @param authorizationHeader The Authorization header containing the JWT token.
     * @return ResponseEntity with 204 status if successful, or an error response with an appropriate status.
     */
    @DeleteMapping("/meal/{mealId}")
    public ResponseEntity<?> removeMealFromAuthenticatedUser(
            @PathVariable Long mealId,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            log.info("Received request to remove meal ID {} from the authenticated user's list.", mealId);

            // Extract userId from the token
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            Long userId = jwtService.extractUserId(token);

            // Call the service method
            userMealService.removeMealFromUser(userId, mealId);

            log.info("Successfully removed meal ID {} from authenticated user ID: {}", mealId, userId);
            return ResponseEntity.noContent().build();

        } catch (UserNotFoundException | MealNotFoundException e) {
            log.warn("Error occurred during meal removal for authenticated user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error occurred during meal removal for authenticated user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Processes the consumption of a meal by the authenticated user.
     *
     * @param mealId The ID of the meal being consumed.
     * @param authorizationHeader The Authorization header containing the JWT token.
     * @return ResponseEntity containing the remaining daily intake for each nutrient after meal consumption.
     */
    @PostMapping("/consume-meal/{mealId}")
    public ResponseEntity<?> consumeMeal(@PathVariable Long mealId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            log.info("Processing meal consumption for authenticated user and meal ID {}", mealId);

            // Extract userId from the token
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            Long userId = jwtService.extractUserId(token);

            // Call the service method
            Map<String, Double> remainingIntakes = consumeMealService.consumeMeal(userId, mealId);

            log.info("Meal consumption processed successfully for user ID {} and meal ID {}", userId, mealId);
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

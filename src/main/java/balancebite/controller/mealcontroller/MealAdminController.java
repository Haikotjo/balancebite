package balancebite.controller.mealcontroller;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.DuplicateMealException;
import balancebite.errorHandling.InvalidFoodItemException;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.security.JwtService;
import balancebite.service.meal.MealAdminService;
import balancebite.service.user.UserMealService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing Meal-related operations.
 * Provides endpoints to create, update, retrieve, and calculate nutrients for meals.
 */
@RestController
@RequestMapping("/meals-admin")
public class MealAdminController {
    private static final Logger log = LoggerFactory.getLogger(MealController.class);

    private final MealAdminService mealAdminService;
    private final UserMealService userMealService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    /**
     * Constructor for dependency injection.
     *
     * @param mealAdminService Service for managing Meal operations.
     */
    public MealAdminController(MealAdminService mealAdminService, UserMealService userMealService, JwtService jwtService, ObjectMapper objectMapper) {
        this.mealAdminService = mealAdminService;
        this.userMealService = userMealService;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    /**
     * Creates a new Meal entity and associates it with a specific User if provided.
     * If no userId is provided, the meal is assigned to the authenticated User creating it (admin or chef).
     *
     * @param mealInputDTOJson        The input data for creating the meal.
     * @param userId              Optional: The ID of the user to associate the meal with.
     * @param authorizationHeader The Authorization header containing the JWT token.
     * @return ResponseEntity containing the created MealDTO with 201 status code, or an error response with an appropriate status.
     */
    @PostMapping(value = "/create-meal", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createMealForAdmin(
            @RequestPart("mealInputDTO") String mealInputDTOJson,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestParam(required = false) Long userId,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            log.info("Received request to create a new meal (admin). Target user ID: {}", userId);

            // Parse JSON string into MealInputDTO (use Spring-injected ObjectMapper)
            MealInputDTO mealInputDTO = objectMapper.readValue(mealInputDTOJson, MealInputDTO.class);

            if (imageFiles != null && !imageFiles.isEmpty()) {
                mealInputDTO.setImageFiles(imageFiles);
            }

            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            Long authenticatedUserId = jwtService.extractUserId(token);

            MealDTO createdMeal = mealAdminService.createMealForAdmin(mealInputDTO, authenticatedUserId, userId);

            log.info("Successfully created meal with name: {}", mealInputDTO.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMeal);

        } catch (DuplicateMealException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (InvalidFoodItemException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during meal creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Updates an existing Meal entity by its ID with the provided MealInputDTO.
     *
     * @param mealId           The ID of the meal to update.
     * @param mealInputDTOJson The new details of the meal.
     * @return ResponseEntity containing the updated MealDTO with 200 status code, or an error response with an appropriate status.
     */
    @PatchMapping(value = "/update-meal/{mealId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMealForAdmin(
            @PathVariable Long mealId,
            @RequestPart("mealInputDTO") String mealInputDTOJson,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            log.info("Received request to update meal (admin) with ID: {}", mealId);

            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            Long authenticatedUserId = jwtService.extractUserId(token);

            MealInputDTO mealInputDTO = objectMapper.readValue(mealInputDTOJson, MealInputDTO.class);

            if (imageFiles != null && !imageFiles.isEmpty()) {
                mealInputDTO.setImageFiles(imageFiles);
            }

            MealDTO updatedMeal = mealAdminService.updateMealForAdmin(authenticatedUserId, mealId, mealInputDTO);

            log.info("Successfully updated meal (admin) with ID: {}", mealId);
            return ResponseEntity.ok(updatedMeal);

        } catch (DuplicateMealException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (InvalidFoodItemException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during meal update (admin)", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }


    /**
     * Adds a copy of an existing meal to a user's list of meals.
     * If no userId is provided, the meal is added to the authenticated user's list.
     * Allows ADMIN or CHEF roles to assign meals to other users.
     *
     * @param mealId              The ID of the meal to be added.
     * @param userId              Optional: The ID of the user to whom the meal will be assigned.
     * @param authorizationHeader The Authorization header containing the JWT token.
     * @return ResponseEntity containing the updated UserDTO with 200 status code, or an error response with an appropriate status.
     */
    @PatchMapping("/add-meal/{mealId}")
    public ResponseEntity<?> addMealToUserAsAdmin(
            @PathVariable Long mealId,
            @RequestParam(required = false) Long userId,
            @RequestHeader("Authorization") String authorizationHeader) {
        try {
            log.info("Received request to add meal with ID: {}. Target user ID: {}", mealId, userId);

            // Extract userId from the token
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            Long authenticatedUserId = jwtService.extractUserId(token);
            log.debug("Authenticated user ID extracted: {}", authenticatedUserId);

            // Use authenticated userId if no userId is provided
            Long targetUserId = (userId != null) ? userId : authenticatedUserId;

            // Call the service method
            UserDTO user = mealAdminService.addMealToUserAsAdmin(targetUserId, mealId, authenticatedUserId);

            log.info("Successfully added meal with ID: {} to user ID: {}", mealId, targetUserId);
            return ResponseEntity.ok(user);

        } catch (DuplicateMealException e) {
            log.warn("Duplicate meal detected for target user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));

        } catch (UserNotFoundException | MealNotFoundException e) {
            log.warn("Error occurred during meal addition: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error occurred while adding meal: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Retrieves all Meal entities from the repository, regardless of their isTemplate value.
     *
     * @return ResponseEntity containing a list of MealDTO objects representing all meals,
     *         or a 204 NO CONTENT if no meals are found.
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllMeals() {
        try {
            log.info("Retrieving all meals, regardless of template status.");
            List<MealDTO> mealDTOs = mealAdminService.getAllMeals();
            if (mealDTOs.isEmpty()) {
                log.info("No meals found in the system.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(mealDTOs);
        } catch (Exception e) {
            log.error("Unexpected error during retrieval of all meals: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Retrieves a Meal by its ID, regardless of ownership or template status.
     *
     * @param id The ID of the Meal.
     * @return ResponseEntity containing the MealDTO with 200 status code, or an error response with an appropriate status.
     */
    @GetMapping("/meal/{id}")
    public ResponseEntity<?> getMealById(@PathVariable Long id) {
        try {
            log.info("Admin received request to retrieve meal with ID: {}", id);

            // Call the service method to fetch the meal
            MealDTO mealDTO = mealAdminService.getMealById(id);

            log.info("Admin successfully retrieved meal with ID: {}", id);
            return ResponseEntity.ok(mealDTO);

        } catch (EntityNotFoundException e) {
            log.warn("Meal with ID {} not found: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error occurred while admin retrieving meal with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Deletes a specific Meal entity by its ID.
     *
     * @param mealId The ID of the meal to delete.
     * @return ResponseEntity with 204 NO CONTENT status if deletion is successful, or an error response with an appropriate status.
     */
    @DeleteMapping("/delete-meal/{mealId}")
    public ResponseEntity<?> deleteMeal(@PathVariable Long mealId) {
        try {
            log.info("Deleting meal with ID: {}", mealId);
            mealAdminService.deleteMeal(mealId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.warn("Meal not found for deletion with ID: {}", mealId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during meal deletion for ID {}: {}", mealId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }
}

package balancebite.controller;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.errorHandling.MissingUserInformationException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.service.RecommendedDailyIntakeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller responsible for managing recommended daily intake-related actions.
 */
@RestController
@RequestMapping("/daily-intake")
public class RecommendedDailyIntakeController {

    private static final Logger log = LoggerFactory.getLogger(RecommendedDailyIntakeController.class);

    private final RecommendedDailyIntakeService recommendedDailyIntakeService;

    /**
     * Constructor to initialize the RecommendedDailyIntakeController with its service dependency.
     *
     * @param recommendedDailyIntakeService The service responsible for recommended daily intake logic.
     */
    public RecommendedDailyIntakeController(RecommendedDailyIntakeService recommendedDailyIntakeService) {
        this.recommendedDailyIntakeService = recommendedDailyIntakeService;
    }

    /**
     * Endpoint to create or retrieve the recommended daily intake for a specific user.
     *
     * @param userId The ID of the user for whom to retrieve or create the recommended daily intake.
     * @return ResponseEntity containing the RecommendedDailyIntakeDTO with a 200 status if successful,
     *         or a detailed error message if user information is missing or user is not found.
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<Object> createRecommendedDailyIntakeForUser(@PathVariable Long userId) {
        try {
            log.info("Creating or retrieving daily intake for user ID: {}", userId);
            RecommendedDailyIntakeDTO dailyIntake = recommendedDailyIntakeService.getOrCreateDailyIntakeForUser(userId);
            return ResponseEntity.ok(dailyIntake);
        } catch (MissingUserInformationException e) {
            log.warn("User information missing for ID {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (UserNotFoundException e) {
            log.warn("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during daily intake creation for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint to delete the recommended daily intake for a specific user.
     *
     * @param userId The ID of the user whose recommended daily intake will be deleted.
     * @return ResponseEntity with no content and 204 status code if successful, or 404 status if the user is not found.
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteRecommendedDailyIntakeForUser(@PathVariable Long userId) {
        try {
            log.info("Deleting recommended daily intake for user ID: {}", userId);
            recommendedDailyIntakeService.deleteRecommendedDailyIntakeForUser(userId);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            log.warn("User not found while attempting to delete daily intake: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Unexpected error during daily intake deletion for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint to retrieve the cumulative recommended nutrient intake for the current week for a specific user.
     *
     * @param userId The ID of the user to retrieve the weekly recommended intake.
     * @return ResponseEntity containing a map of nutrient totals for the remaining days of the current week with 200 status.
     */
    @GetMapping("/user/{userId}/week")
    public ResponseEntity<?> getWeeklyRecommendedDailyIntakeForUser(@PathVariable Long userId) {
        try {
            log.info("Calculating weekly intake for user ID: {}", userId);
            Map<String, Double> weeklyIntake = recommendedDailyIntakeService.getAdjustedWeeklyIntakeForUser(userId);
            return ResponseEntity.ok(weeklyIntake);
        } catch (UserNotFoundException e) {
            log.warn("User not found while retrieving weekly intake: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (MissingUserInformationException e) {
            log.warn("Missing user information while retrieving weekly intake: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during weekly intake retrieval for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint to retrieve the cumulative recommended nutrient intake for the current month for a specific user.
     *
     * @param userId The ID of the user to retrieve the monthly recommended intake.
     * @return ResponseEntity containing a map of nutrient totals for the remaining days of the current month with 200 status.
     */
    @GetMapping("/user/{userId}/month")
    public ResponseEntity<?> getMonthlyRecommendedDailyIntakeForUser(@PathVariable Long userId) {
        try {
            log.info("Calculating monthly intake for user ID: {}", userId);
            Map<String, Double> monthlyIntake = recommendedDailyIntakeService.getAdjustedMonthlyIntakeForUser(userId);
            return ResponseEntity.ok(monthlyIntake);
        } catch (UserNotFoundException e) {
            log.warn("User not found while retrieving monthly intake: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (MissingUserInformationException e) {
            log.warn("Missing user information while retrieving monthly intake: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during monthly intake retrieval for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }
}

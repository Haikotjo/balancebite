package balancebite.controller;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.errorHandling.MissingUserInformationException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.service.RecommendedDailyIntakeService;
import balancebite.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller responsible for handling recommended daily intake-related actions.
 */
@RestController
@RequestMapping("/daily-intake")
public class RecommendedDailyIntakeController {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final RecommendedDailyIntakeService recommendedDailyIntakeService;

    public RecommendedDailyIntakeController(RecommendedDailyIntakeService recommendedDailyIntakeService) {
        this.recommendedDailyIntakeService = recommendedDailyIntakeService;
    }

    /**
     * POST endpoint to create or retrieve the recommended daily intake for a specific user.
     *
     * This endpoint uses the service to fetch or create the recommended daily intake.
     * If any required user information is missing, a proper error response is returned.
     *
     * @param userId The ID of the user to assign the recommended daily intake to.
     * @return ResponseEntity containing the created or retrieved RecommendedDailyIntakeDTO,
     *         or an error response if user information is missing.
     */
    @PostMapping("/user/{userId}/daily-intake")
    public ResponseEntity<Object> createRecommendedDailyIntakeForUser(@PathVariable Long userId) {
        try {
            // Delegate logic to service
            RecommendedDailyIntakeDTO createdIntake = recommendedDailyIntakeService.getOrCreateDailyIntakeForUser(userId);
            return ResponseEntity.ok(createdIntake);
        } catch (MissingUserInformationException e) {
            log.error("User information missing for user ID: {}, message: {}", userId, e.getMessage(), e);
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (UserNotFoundException e) {
            log.error("User not found: {}, message: {}", userId, e.getMessage(), e);
            Map<String, String> errorResponse = Map.of("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error occurred while creating daily intake for user ID: {}, message: {}", userId, e.getMessage(), e);
            Map<String, String> errorResponse = Map.of("error", "An unexpected error occurred. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * DELETE endpoint to delete the recommended daily intake for a specific user.
     *
     * @param userId The ID of the user whose recommended daily intake will be deleted.
     * @return ResponseEntity with no content if the delete was successful.
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteRecommendedDailyIntakeForUser(@PathVariable Long userId) {
        recommendedDailyIntakeService.deleteRecommendedDailyIntakeForUser(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET endpoint to retrieve the cumulative recommended nutrient intake for the current week for a specific user.
     *
     * @param userId The ID of the user to retrieve the recommended daily intake for the current week.
     * @return ResponseEntity containing the total nutrient values for the remaining days of the current week.
     */
    @GetMapping("/user/{userId}/week")
    public ResponseEntity<Map<String, Double>> getWeeklyRecommendedDailyIntakeForUser(@PathVariable Long userId) {
        Map<String, Double> weeklyIntake = recommendedDailyIntakeService.getAdjustedWeeklyIntakeForUser(userId);
        return ResponseEntity.ok(weeklyIntake);
    }

    /**
     * GET endpoint to retrieve the cumulative recommended nutrient intake for the current week for a specific user.
     *
     * @param userId The ID of the user to retrieve the recommended daily intake for the current week.
     * @return ResponseEntity containing the total nutrient values for the remaining days of the current week.
     */
    @GetMapping("/user/{userId}/month")
    public ResponseEntity<Map<String, Double>> getMonthlyRecommendedDailyIntakeForUser(@PathVariable Long userId) {
        Map<String, Double> monthlyIntake = recommendedDailyIntakeService.getAdjustedMonthlyIntakeForUser(userId);
        return ResponseEntity.ok(monthlyIntake);
    }
}

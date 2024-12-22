package balancebite.controller;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.errorHandling.DailyIntakeNotFoundException;
import balancebite.errorHandling.MissingUserInformationException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.security.JwtService;
import balancebite.service.RecommendedDailyIntakeService;
import balancebite.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * REST controller responsible for managing recommended daily intake-related actions.
 */
@RestController
@RequestMapping("/daily-intake")
public class RecommendedDailyIntakeController {

    private static final Logger log = LoggerFactory.getLogger(RecommendedDailyIntakeController.class);

    private final RecommendedDailyIntakeService recommendedDailyIntakeService;
    private final JwtService jwtService;

    /**
     * Constructor to initialize the RecommendedDailyIntakeController with its service dependency.
     *
     * @param recommendedDailyIntakeService The service responsible for recommended daily intake logic.
     */
    public RecommendedDailyIntakeController(RecommendedDailyIntakeService recommendedDailyIntakeService, JwtService jwtService) {
        this.recommendedDailyIntakeService = recommendedDailyIntakeService;
        this.jwtService = jwtService;
    }

    /**
     * Endpoint to retrieve the recommended daily intake for the authenticated user.
     *
     * @param authorizationHeader The Authorization header containing the JWT token.
     * @return ResponseEntity containing the RecommendedDailyIntakeDTO with a 200 status if successful,
     *         or a detailed error message if no intake is found or user is not found.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user")
    public ResponseEntity<Object> getRecommendedDailyIntakeForAuthenticatedUser(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            log.info("Retrieving daily intake for the authenticated user.");

            // Extract user ID from the token
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix
            Long userId = jwtService.extractUserId(token);

            // Retrieve daily intake
            RecommendedDailyIntakeDTO dailyIntake = recommendedDailyIntakeService.getDailyIntakeForUser(userId);

            log.info("Successfully retrieved daily intake for user ID: {}", userId);
            return ResponseEntity.ok(dailyIntake);
        } catch (DailyIntakeNotFoundException e) {
            log.warn("Daily intake not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (UserNotFoundException e) {
            log.warn("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during daily intake retrieval: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
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

    /**
     * Endpoint to retrieve the recommended daily intake for a specific user on a specific date.
     *
     * @param userId The ID of the user.
     * @param date   The specific date to retrieve the intake for (format: yyyy-MM-dd).
     * @return ResponseEntity containing the recommended daily intake for the given date with 200 status.
     */
    @GetMapping("/user/{userId}/date")
    public ResponseEntity<?> getDailyRecommendedIntakeForUserOnDate(
            @PathVariable Long userId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            log.info("Fetching recommended daily intake for user ID {} on date {}", userId, date);
            RecommendedDailyIntakeDTO intake = recommendedDailyIntakeService.getDailyIntakeForDate(userId, date);
            return ResponseEntity.ok(intake);
        } catch (UserNotFoundException e) {
            log.warn("User not found while retrieving daily intake on date {}: {}", date, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (DailyIntakeNotFoundException e) {
            log.warn("Daily intake not found for user on date {}: {}", date, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error while retrieving daily intake for user ID {} on date {}: {}", userId, date, e.getMessage(), e);
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
}

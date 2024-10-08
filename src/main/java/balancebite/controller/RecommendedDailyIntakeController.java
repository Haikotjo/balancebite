package balancebite.controller;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.service.RecommendedDailyIntakeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller responsible for handling recommended daily intake-related actions.
 */
@RestController
@RequestMapping("/daily-intake")
public class RecommendedDailyIntakeController {

    private final RecommendedDailyIntakeService recommendedDailyIntakeService;

    public RecommendedDailyIntakeController(RecommendedDailyIntakeService recommendedDailyIntakeService) {
        this.recommendedDailyIntakeService = recommendedDailyIntakeService;
    }

    /**
     * POST endpoint to create a new recommended daily intake for a specific user.
     *
     * @param userId The ID of the user to assign the recommended daily intake to.
     * @return ResponseEntity containing the created RecommendedDailyIntakeDTO.
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<RecommendedDailyIntakeDTO> createRecommendedDailyIntakeForUser(
            @PathVariable Long userId) {

        // Create the recommended daily intake without requiring a request body
        RecommendedDailyIntakeDTO createdIntake = recommendedDailyIntakeService.createRecommendedDailyIntakeForUser(userId);
        return ResponseEntity.ok(createdIntake);
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
     * GET endpoint to retrieve the recommended daily intake for a specific user.
     *
     * @param userId The ID of the user whose recommended daily intake will be retrieved.
     * @return ResponseEntity containing the RecommendedDailyIntakeDTO.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<RecommendedDailyIntakeDTO> getRecommendedDailyIntakeForUser(
            @PathVariable Long userId) {

        RecommendedDailyIntakeDTO intakeDTO = recommendedDailyIntakeService.getRecommendedDailyIntakeForUser(userId);
        return ResponseEntity.ok(intakeDTO);
    }
}

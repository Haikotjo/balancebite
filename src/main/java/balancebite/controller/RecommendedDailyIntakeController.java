package balancebite.controller;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;

import balancebite.service.RecommendedDailyIntakeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for handling recommended daily intake-related actions.
 */
@RestController
@RequestMapping("/api/daily-intake")
public class RecommendedDailyIntakeController {

    private final RecommendedDailyIntakeService recommendedDailyIntakeService;

    public RecommendedDailyIntakeController(RecommendedDailyIntakeService recommendedDailyIntakeService) {
        this.recommendedDailyIntakeService = recommendedDailyIntakeService;
    }

    /**
     * GET endpoint to retrieve the recommended daily intake for a specific user.
     *
     * @param userId The ID of the user.
     * @return ResponseEntity containing the RecommendedDailyIntakeDTO.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<RecommendedDailyIntakeDTO> getRecommendedDailyIntakeByUserId(@PathVariable Long userId) {
        RecommendedDailyIntakeDTO dailyIntake = recommendedDailyIntakeService.getRecommendedDailyIntakeByUserId(userId);
        return ResponseEntity.ok(dailyIntake);
    }



}

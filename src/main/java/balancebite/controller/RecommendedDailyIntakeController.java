package balancebite.controller;

import balancebite.dto.RecommendedDailyIntakeDTO;

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
     * GET endpoint to retrieve all recommended daily intake data.
     *
     * @return ResponseEntity containing the RecommendedDailyIntakeDTO.
     */
    @GetMapping("/all")
    public ResponseEntity<RecommendedDailyIntakeDTO> getAllRecommendedDailyIntake() {
        RecommendedDailyIntakeDTO dailyIntake = recommendedDailyIntakeService.getRecommendedDailyIntake();
        return ResponseEntity.ok(dailyIntake);
    }

//    @Autowired
//    private RecommendedDailyIntake recommendedDailyIntake;
//
//    @Autowired
//    private MealRepository mealRepository;
//
//    // POST endpoint om maaltijd van de daily intake af te trekken
//    @PostMapping("/subtractMeal/{mealId}")
//    public ResponseEntity<RecommendedDailyIntakeService> subtractMealFromDailyIntake(@PathVariable Long mealId) {
//        // Haal de dagelijkse inname op, in dit geval de eerste die in de database staat
//        RecommendedDailyIntake dailyIntake = recommendedDailyIntake.findById(1L)
//                .orElseThrow(() -> new RuntimeException("Daily intake not found"));
//
//        // Haal de maaltijd op die van de dagelijkse inname moet worden afgetrokken
//        Meal meal = mealRepository.findById(mealId)
//                .orElseThrow(() -> new RuntimeException("Meal not found"));
//
//        // Trek de maaltijd af van de dagelijkse inname
//        dailyIntake.subtractMealIntake(meal);
//
//        // Sla de geüpdatete dagelijkse inname op in de database
//        recommendedDailyIntake.save(recommendedDailyIntake);
//
//        // Retourneer de geüpdatete dagelijkse inname
//        return ResponseEntity.ok(recommendedDailyIntake);
//    }
}

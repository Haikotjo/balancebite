//package balancebite.controller;
//
//import balancebite.model.DailyIntake;
//import balancebite.model.Meal;
//import balancebite.repository.DailyIntakeRepository;
//import balancebite.repository.MealRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/dailyintake")
//public class DailyIntakeController {
//
//    @Autowired
//    private DailyIntakeRepository dailyIntakeRepository;
//
//    @Autowired
//    private MealRepository mealRepository;
//
//    // POST endpoint om maaltijd van de daily intake af te trekken
//    @PostMapping("/subtractMeal/{mealId}")
//    public ResponseEntity<DailyIntake> subtractMealFromDailyIntake(@PathVariable Long mealId) {
//        // Haal de dagelijkse inname op, in dit geval de eerste die in de database staat
//        DailyIntake dailyIntake = dailyIntakeRepository.findById(1L)
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
//        dailyIntakeRepository.save(dailyIntake);
//
//        // Retourneer de geüpdatete dagelijkse inname
//        return ResponseEntity.ok(dailyIntake);
//    }
//}

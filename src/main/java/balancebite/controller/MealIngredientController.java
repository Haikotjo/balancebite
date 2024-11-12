//package balancebite.controller;
//
//import balancebite.dto.mealingredient.MealIngredientInputDTO;
//import balancebite.errorHandling.MealNotFoundException;
//import balancebite.service.MealIngredientService;
//import jakarta.validation.Valid;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
///**
// * REST controller for managing MealIngredient-related operations.
// * Provides endpoints for adding ingredients to meals.
// */
//@RestController
//@RequestMapping("/meals/{mealId}/ingredients")
//public class MealIngredientController {
//
//    private static final Logger log = LoggerFactory.getLogger(MealIngredientController.class);
//
//    private final MealIngredientService mealIngredientService;
//
//    /**
//     * Constructor for dependency injection.
//     *
//     * @param mealIngredientService Service for managing MealIngredient operations.
//     */
//    public MealIngredientController(MealIngredientService mealIngredientService) {
//        this.mealIngredientService = mealIngredientService;
//    }
//
//    /**
//     * Adds a meal ingredient to a specific meal.
//     *
//     * @param mealId    The ID of the meal to which the ingredient should be added.
//     * @param inputDTO  The DTO containing the data of the meal ingredient to be added.
//     * @return ResponseEntity with 201 status code if successful, or an error response with an appropriate status.
//     */
//    @PostMapping
//    public ResponseEntity<?> addMealIngredient(@PathVariable Long mealId, @Valid @RequestBody MealIngredientInputDTO inputDTO) {
//        try {
//            log.info("Adding ingredient to meal with ID: {}", mealId);
//            mealIngredientService.addMealIngredient(mealId, inputDTO);
//            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Meal ingredient added successfully."));
//        } catch (IllegalArgumentException e) {
//            log.warn("Invalid meal ID provided: {}", mealId);
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            log.error("Unexpected error during meal ingredient addition for meal ID {}: {}", mealId, e.getMessage(), e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
//        }
//    }
//}

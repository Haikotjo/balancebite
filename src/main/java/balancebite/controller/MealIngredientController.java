package balancebite.controller;

import balancebite.dto.MealIngredientInputDTO;
import balancebite.service.MealIngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mealIngredients")
public class MealIngredientController {

    @Autowired
    private MealIngredientService mealIngredientService;

    @PostMapping("/add/{mealId}")
    public void addMealIngredient(@PathVariable Long mealId, @RequestBody MealIngredientInputDTO inputDTO) {
        mealIngredientService.addMealIngredient(mealId, inputDTO);
    }
}

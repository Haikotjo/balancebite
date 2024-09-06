package balancebite.controller;

import balancebite.dto.MealDTO;
import balancebite.dto.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.mapper.MealMapper;
import balancebite.model.Meal;
import balancebite.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/meals")
public class MealController {

    @Autowired
    private MealService mealService;

    @Autowired
    private MealMapper mealMapper;

    @PostMapping
    public MealDTO createMeal(@RequestBody MealInputDTO mealInputDTO) {
        // De service methode retourneert nu een MealDTO
        return mealService.createMeal(mealInputDTO);
    }

    @GetMapping("/nutrients/{id}")
    public Map<String, NutrientInfoDTO> calculateNutrients(@PathVariable Long id) {
        return mealService.calculateNutrients(id);
    }

    @GetMapping("/nutrients-per-food-item/{id}")
    public Map<Long, Map<String, NutrientInfoDTO>> calculateNutrientsPerFoodItem(@PathVariable Long id) {
        return mealService.calculateNutrientsPerFoodItem(id);
    }

    @GetMapping("/all")
    public List<MealDTO> getAllMeals() {
        List<Meal> meals = mealService.getAllMeals();
        return meals.stream()
                .map(mealMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MealDTO getMealById(@PathVariable Long id) {
        Meal meal = mealService.getMealById(id);
        return mealMapper.toDTO(meal);
    }

}

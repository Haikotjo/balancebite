package balancebite.controller;

import balancebite.dto.MealDTO;
import balancebite.dto.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.mapper.MealMapper;
import balancebite.model.Meal;
import balancebite.service.MealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/meals")
public class MealController {

    @Autowired
    private MealService mealService;

    @Autowired
    private MealMapper mealMapper;

    @PostMapping
    public MealDTO createMeal(@RequestBody MealInputDTO mealInputDTO) {
        Meal createdMeal = mealService.createMeal(mealInputDTO);
        return mealMapper.toDTO(createdMeal);  // Gebruik de instantie methode hier
    }

    @GetMapping("/{id}/nutrients")
    public Map<String, NutrientInfoDTO> calculateNutrients(@PathVariable Long id) {
        return mealService.calculateNutrients(id);
    }
}

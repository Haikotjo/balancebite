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

    /**
     * Creates a new Meal entity based on the provided MealInputDTO.
     *
     * @param mealInputDTO the input data for creating the meal.
     * @return the created MealDTO object.
     */
    @PostMapping
    public MealDTO createMeal(@RequestBody MealInputDTO mealInputDTO) {
        // The service method now returns a MealDTO
        return mealService.createMeal(mealInputDTO);
    }

    /**
     * Retrieves the total nutrients for a given Meal by its ID.
     *
     * @param id the ID of the meal.
     * @return a map of nutrient names and their corresponding total values.
     */
    @GetMapping("/nutrients/{id}")
    public Map<String, NutrientInfoDTO> calculateNutrients(@PathVariable Long id) {
        return mealService.calculateNutrients(id);
    }

    /**
     * Retrieves the nutrients per food item for a given Meal by its ID.
     *
     * @param id the ID of the meal.
     * @return a map where the key is the food item ID and the value is a map of nutrient names and their corresponding total values.
     */
    @GetMapping("/nutrients-per-food-item/{id}")
    public Map<Long, Map<String, NutrientInfoDTO>> calculateNutrientsPerFoodItem(@PathVariable Long id) {
        return mealService.calculateNutrientsPerFoodItem(id);
    }

    /**
     * Retrieves all meals from the repository.
     *
     * @return a list of MealDTO objects representing all meals.
     */
    @GetMapping("/all")
    public List<MealDTO> getAllMeals() {
        List<Meal> meals = mealService.getAllMeals();
        return meals.stream()
                .map(mealMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a Meal entity by its ID.
     *
     * @param id the ID of the meal to retrieve.
     * @return the corresponding MealDTO object.
     */
    @GetMapping("/{id}")
    public MealDTO getMealById(@PathVariable Long id) {
        Meal meal = mealService.getMealById(id);
        return mealMapper.toDTO(meal);
    }

    /**
     * Retrieves only the macronutrients for a given Meal by its ID.
     * This includes values such as Energy (kcal), Protein, Carbohydrates, and Fats,
     * where fats are grouped into a separate section.
     *
     * @param id the ID of the meal.
     * @return a map of macronutrient names and their corresponding values for the meal,
     *         with fats grouped into a separate section.
     */
    @GetMapping("/macronutrients/{id}")
    public Map<String, Object> getMacronutrients(@PathVariable Long id) {
        return mealService.getMacronutrients(id);
    }
}

package balancebite.service;

import balancebite.dto.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.model.FoodItem;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.repository.FoodItemRepository;
import balancebite.repository.MealRepository;
import balancebite.utils.NutrientCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MealService {

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Transactional
    public Meal createMeal(MealInputDTO mealInputDTO) {
        Meal meal = new Meal();
        meal.setName(mealInputDTO.getName());

        // Set the mealIngredients
        List<MealIngredient> mealIngredients = mealInputDTO.getMealIngredients().stream().map(inputDTO -> {
            FoodItem foodItem = foodItemRepository.findById(inputDTO.getFoodItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid food item ID: " + inputDTO.getFoodItemId()));
            return new MealIngredient(meal, foodItem, inputDTO.getQuantity());
        }).collect(Collectors.toList());
        meal.addMealIngredients(mealIngredients);

        // Calculate the nutrients using the NutrientCalculator
        Map<String, NutrientInfoDTO> nutrients = NutrientCalculator.calculateNutrients(meal);

        // Set the calculated macronutrients on the Meal object
        meal.setProteins(nutrients.getOrDefault("Proteins (g)", new NutrientInfoDTO()).getValue());
        meal.setCarbohydrates(nutrients.getOrDefault("Carbohydrates (g)", new NutrientInfoDTO()).getValue());
        meal.setFats(nutrients.getOrDefault("Fats (g)", new NutrientInfoDTO()).getValue());
        meal.setKcals(nutrients.getOrDefault("Energy (kcal)", new NutrientInfoDTO()).getValue());

        // Save the meal to the database
        return mealRepository.save(meal);
    }

    // Deze methode wordt niet meer gebruikt voor berekeningen, maar kan nog steeds worden gebruikt als je specifieke nutriënten wilt opvragen
    public Map<String, NutrientInfoDTO> calculateNutrients(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid meal ID: " + mealId));
        return NutrientCalculator.calculateNutrients(meal);
    }

    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    public Meal getMealById(Long id) {
        return mealRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid meal ID: " + id));
    }
}

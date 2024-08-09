package balancebite.service;

import balancebite.dto.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.model.FoodItem;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.NutrientInfo;
import balancebite.repository.FoodItemRepository;
import balancebite.repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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
        List<MealIngredient> mealIngredients = mealInputDTO.getMealIngredients().stream().map(inputDTO -> {
            FoodItem foodItem = foodItemRepository.findById(inputDTO.getFoodItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid food item ID: " + inputDTO.getFoodItemId()));
            return new MealIngredient(meal, foodItem, inputDTO.getQuantity());
        }).collect(Collectors.toList());
        meal.addMealIngredients(mealIngredients);
        return mealRepository.save(meal);
    }

    public Map<String, NutrientInfoDTO> calculateNutrients(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid meal ID: " + mealId));
        Map<String, NutrientInfoDTO> totalNutrients = new HashMap<>();

        for (MealIngredient ingredient : meal.getMealIngredients()) {
            FoodItem foodItem = ingredient.getFoodItem();
            for (NutrientInfo nutrient : foodItem.getNutrients()) {
                double nutrientValue = nutrient.getValue() * (ingredient.getQuantity() / 100.0);
                totalNutrients.computeIfAbsent(nutrient.getNutrientName(), k -> new NutrientInfoDTO(nutrient.getNutrientName(), 0.0, nutrient.getUnitName()))
                        .setValue(totalNutrients.get(nutrient.getNutrientName()).getValue() + nutrientValue);
            }
        }

        return totalNutrients;
    }

    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    public Meal getMealById(Long id) {
        return mealRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid meal ID: " + id));
    }
}

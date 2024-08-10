package balancebite.utils;

import balancebite.dto.NutrientInfoDTO;
import balancebite.model.FoodItem;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.NutrientInfo;

import java.util.HashMap;
import java.util.Map;

public class NutrientCalculator {

    public static Map<String, NutrientInfoDTO> calculateNutrients(Meal meal) {
        Map<String, NutrientInfoDTO> totalNutrients = new HashMap<>();

        for (MealIngredient ingredient : meal.getMealIngredients()) {
            FoodItem foodItem = ingredient.getFoodItem();
            System.out.println("Calculating nutrients for FoodItem: " + foodItem.getName() + ", Quantity: " + ingredient.getQuantity());

            for (NutrientInfo nutrient : foodItem.getNutrients()) {
                double nutrientValue = nutrient.getValue() * (ingredient.getQuantity() / 100.0);

                // Negeer voedingsstoffen met een waarde van 0 of null
                if (nutrientValue > 0) {
                    String key = nutrient.getNutrientName() + " (" + nutrient.getUnitName() + ")";
                    totalNutrients.computeIfAbsent(key, k -> new NutrientInfoDTO(nutrient.getNutrientName(), 0.0, nutrient.getUnitName()))
                            .setValue(totalNutrients.get(key).getValue() + nutrientValue);

                    System.out.println("Updated total for " + key + ": " + totalNutrients.get(key).getValue());
                }
            }
        }

        return totalNutrients;
    }
}

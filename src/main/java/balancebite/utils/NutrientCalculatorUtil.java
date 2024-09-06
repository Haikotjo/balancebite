package balancebite.utils;

import balancebite.dto.NutrientInfoDTO;
import balancebite.model.MealIngredient;
import balancebite.model.NutrientInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NutrientCalculatorUtil {

    /**
     * Calculates the total nutrients for the provided meal ingredients.
     * Nutrients are retrieved from the associated FoodItem for each ingredient.
     *
     * @param mealIngredients the list of ingredients for the meal.
     * @return a map of nutrient names and their corresponding total values for the meal.
     */
    public static Map<String, NutrientInfoDTO> calculateTotalNutrients(List<MealIngredient> mealIngredients) {
        Map<String, NutrientInfoDTO> totalNutrients = new HashMap<>();

        // Step 1: Count occurrences of each nutrient name
        Map<String, Integer> nutrientNameCount = countNutrientNames(mealIngredients);

        // Step 2: Calculate nutrients based on quantity and uniqueness of nutrient names
        for (MealIngredient ingredient : mealIngredients) {
            List<NutrientInfo> nutrients = ingredient.getFoodItem().getNutrients();

            for (NutrientInfo nutrient : nutrients) {
                String nutrientName = nutrient.getNutrientName();

                // If the nutrient name appears more than once, append the unit to make it unique
                if (nutrientNameCount.get(nutrientName) > 1) {
                    nutrientName = nutrientName + " " + nutrient.getUnitName();
                }

                double adjustedValue = nutrient.getValue() * (ingredient.getQuantity() / 100.0);

                // Merge the nutrient values into the total map
                totalNutrients.merge(
                        nutrientName,
                        new NutrientInfoDTO(nutrientName, adjustedValue, nutrient.getUnitName(), nutrient.getNutrientId()),
                        (existing, newValue) -> {
                            existing.setValue(existing.getValue() + newValue.getValue());
                            return existing;
                        }
                );
            }
        }

        return totalNutrients;
    }

    /**
     * Calculates the nutrients per food item in the provided meal ingredients.
     * This will return the nutrients individually for each food item in the meal.
     *
     * @param mealIngredients the list of ingredients for the meal.
     * @return a map where the key is the food item ID, and the value is the map of nutrient names and their corresponding total values.
     */
    public static Map<Long, Map<String, NutrientInfoDTO>> calculateNutrientsPerFoodItem(List<MealIngredient> mealIngredients) {
        Map<Long, Map<String, NutrientInfoDTO>> nutrientsPerFoodItem = new HashMap<>();

        // Step 1: Count occurrences of each nutrient name
        Map<String, Integer> nutrientNameCount = countNutrientNames(mealIngredients);

        // Step 2: Calculate nutrients for each food item
        for (MealIngredient ingredient : mealIngredients) {
            Map<String, NutrientInfoDTO> nutrientMap = new HashMap<>();
            List<NutrientInfo> nutrients = ingredient.getFoodItem().getNutrients();

            for (NutrientInfo nutrient : nutrients) {
                String nutrientName = nutrient.getNutrientName();

                // If the nutrient name appears more than once, append the unit to make it unique
                if (nutrientNameCount.get(nutrientName) > 1) {
                    nutrientName = nutrientName + " " + nutrient.getUnitName();
                }

                double adjustedValue = nutrient.getValue() * (ingredient.getQuantity() / 100.0);

                nutrientMap.merge(
                        nutrientName,
                        new NutrientInfoDTO(nutrientName, adjustedValue, nutrient.getUnitName(), nutrient.getNutrientId()),
                        (existing, newValue) -> {
                            existing.setValue(existing.getValue() + newValue.getValue());
                            return existing;
                        }
                );
            }

            // Add the nutrients map to the result for this food item
            nutrientsPerFoodItem.put(ingredient.getFoodItem().getId(), nutrientMap);
        }

        return nutrientsPerFoodItem;
    }

    /**
     * Counts occurrences of each nutrient name across all meal ingredients.
     *
     * @param mealIngredients the list of ingredients for the meal.
     * @return a map of nutrient names and their corresponding counts.
     */
    private static Map<String, Integer> countNutrientNames(List<MealIngredient> mealIngredients) {
        Map<String, Integer> nutrientNameCount = new HashMap<>();

        for (MealIngredient ingredient : mealIngredients) {
            for (NutrientInfo nutrient : ingredient.getFoodItem().getNutrients()) {
                nutrientNameCount.merge(nutrient.getNutrientName(), 1, Integer::sum);
            }
        }

        return nutrientNameCount;
    }
}

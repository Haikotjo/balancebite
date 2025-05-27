package balancebite.utils;

import balancebite.dto.NutrientInfoDTO;
import balancebite.model.MealIngredient;
import balancebite.model.NutrientInfo;
import balancebite.model.diet.DietDay;
import balancebite.model.meal.Meal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for calculating the total nutrients in a meal,
 * either aggregated or per food item.
 */
public class NutrientCalculatorUtil {

    /**
     * Calculates the total nutrients for the provided meal ingredients.
     * Nutrients are retrieved from the associated FoodItem for each ingredient
     * and aggregated into a map of nutrient names and their corresponding total values.
     *
     * @param mealIngredients the list of ingredients for the meal.
     * @return a map of nutrient names and their corresponding total values for the meal.
     */
    public static Map<String, NutrientInfoDTO> calculateTotalNutrients(List<MealIngredient> mealIngredients) {
        Map<String, NutrientInfoDTO> totalNutrients = new HashMap<>();

        if (mealIngredients == null || mealIngredients.isEmpty()) {
            return totalNutrients; // Return an empty map if there are no ingredients
        }

        // Step 1: Count occurrences of each nutrient name
        Map<String, Integer> nutrientNameCount = countNutrientNames(mealIngredients);

        // Step 2: Calculate nutrients based on quantity and uniqueness of nutrient names
        for (MealIngredient ingredient : mealIngredients) {
            if (ingredient == null || ingredient.getFoodItem() == null) {
                continue; // Skip if ingredient or food item is null
            }

            double ingredientQuantityFactor = ingredient.getQuantity() / 100.0;
            List<NutrientInfo> nutrients = ingredient.getFoodItem().getNutrients();

            if (nutrients == null) {
                continue; // Skip if nutrients list is null
            }

            for (NutrientInfo nutrient : nutrients) {
                if (nutrient == null || nutrient.getNutrientName() == null || nutrient.getValue() == null) {
                    continue; // Skip if nutrient or its key attributes are null
                }

                String nutrientName = nutrient.getNutrientName();

                // If the nutrient name appears more than once, append the unit to make it unique
                if (nutrientNameCount.get(nutrientName) > 1) {
                    nutrientName = nutrientName + " " + nutrient.getUnitName();
                }

                double adjustedValue = nutrient.getValue() * ingredientQuantityFactor;

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
     * This returns the nutrients individually for each food item in the meal.
     *
     * @param mealIngredients the list of ingredients for the meal.
     * @return a map where the key is the food item ID, and the value is the map of nutrient names and their corresponding total values.
     */
    public static Map<Long, Map<String, NutrientInfoDTO>> calculateNutrientsPerFoodItem(List<MealIngredient> mealIngredients) {
        Map<Long, Map<String, NutrientInfoDTO>> nutrientsPerFoodItem = new HashMap<>();

        if (mealIngredients == null || mealIngredients.isEmpty()) {
            return nutrientsPerFoodItem; // Return an empty map if there are no ingredients
        }

        // Step 1: Count occurrences of each nutrient name
        Map<String, Integer> nutrientNameCount = countNutrientNames(mealIngredients);

        // Step 2: Calculate nutrients for each food item
        for (MealIngredient ingredient : mealIngredients) {
            if (ingredient == null || ingredient.getFoodItem() == null) {
                continue; // Skip if ingredient or food item is null
            }

            double ingredientQuantityFactor = ingredient.getQuantity() / 100.0;
            Map<String, NutrientInfoDTO> nutrientMap = new HashMap<>();
            List<NutrientInfo> nutrients = ingredient.getFoodItem().getNutrients();

            if (nutrients == null) {
                continue; // Skip if nutrients list is null
            }

            for (NutrientInfo nutrient : nutrients) {
                if (nutrient == null || nutrient.getNutrientName() == null || nutrient.getValue() == null) {
                    continue; // Skip if nutrient or its key attributes are null
                }

                String nutrientName = nutrient.getNutrientName();

                // If the nutrient name appears more than once, append the unit to make it unique
                if (nutrientNameCount.get(nutrientName) > 1) {
                    nutrientName = nutrientName + " " + nutrient.getUnitName();
                }

                double adjustedValue = nutrient.getValue() * ingredientQuantityFactor;

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
     * This is used to detect and handle duplicate nutrient names by appending unit names.
     *
     * @param mealIngredients the list of ingredients for the meal.
     * @return a map of nutrient names and their corresponding counts.
     */
    private static Map<String, Integer> countNutrientNames(List<MealIngredient> mealIngredients) {
        Map<String, Integer> nutrientNameCount = new HashMap<>();

        if (mealIngredients == null || mealIngredients.isEmpty()) {
            return nutrientNameCount; // Return an empty map if there are no ingredients
        }

        for (MealIngredient ingredient : mealIngredients) {
            if (ingredient == null || ingredient.getFoodItem() == null || ingredient.getFoodItem().getNutrients() == null) {
                continue; // Skip if any relevant data is null
            }

            for (NutrientInfo nutrient : ingredient.getFoodItem().getNutrients()) {
                if (nutrient != null && nutrient.getNutrientName() != null) {
                    nutrientNameCount.merge(nutrient.getNutrientName(), 1, Integer::sum);
                }
            }
        }

        return nutrientNameCount;
    }

    public static Map<String, NutrientInfoDTO> calculateTotalNutrientsForDiet(List<DietDay> dietDays) {
        List<MealIngredient> allIngredients = new ArrayList<>();

        for (DietDay day : dietDays) {
            for (Meal meal : day.getMeals()) {
                allIngredients.addAll(meal.getMealIngredients());
            }
        }

        return calculateTotalNutrients(allIngredients);
    }

    public static Map<String, NutrientInfoDTO> calculateTotalNutrientsForDay(DietDay dietDay) {
        if (dietDay == null || dietDay.getMeals() == null) {
            return new HashMap<>();
        }

        List<MealIngredient> allIngredients = new ArrayList<>();
        for (Meal meal : dietDay.getMeals()) {
            if (meal != null && meal.getMealIngredients() != null) {
                allIngredients.addAll(meal.getMealIngredients());
            }
        }

        return calculateTotalNutrients(allIngredients);
    }

    public static Map<String, Double> calculateAverages(Map<String, NutrientInfoDTO> totalNutrients, int dayCount) {
        Map<String, Double> averages = new HashMap<>();
        if (dayCount == 0 || totalNutrients == null) return averages;

        averages.put("avgCalories", (double) Math.round(getValue(totalNutrients, "Energy kcal") / dayCount));
        averages.put("avgProtein", (double) Math.round(getValue(totalNutrients, "Protein g") / dayCount));
        averages.put("avgCarbs", (double) Math.round(getValue(totalNutrients, "Carbohydrates g") / dayCount));
        averages.put("avgFat", (double) Math.round(getValue(totalNutrients, "Total lipid (fat) g") / dayCount));

        return averages;
    }

    private static double getValue(Map<String, NutrientInfoDTO> nutrients, String key) {
        return nutrients.containsKey(key) && nutrients.get(key).getValue() != null
                ? nutrients.get(key).getValue()
                : 0.0;
    }

}

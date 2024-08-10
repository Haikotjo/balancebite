package balancebite.utils;

import balancebite.dto.NutrientInfoDTO;
import balancebite.model.*;

import java.util.HashMap;
import java.util.Map;

public class NutrientCalculator {

    public static Map<String, NutrientInfoDTO> calculateNutrients(Meal meal) {
        Map<String, NutrientInfoDTO> totalNutrients = new HashMap<>();
        Macronutrients macronutrients = new Macronutrients();

        for (MealIngredient ingredient : meal.getMealIngredients()) {
            FoodItem foodItem = ingredient.getFoodItem();
            System.out.println("Calculating nutrients for FoodItem: " + foodItem.getName() + ", Quantity: " + ingredient.getQuantity());

            for (NutrientInfo nutrient : foodItem.getNutrients()) {
                double nutrientValue = nutrient.getValue() * (ingredient.getQuantity() / 100.0);

                if (nutrientValue > 0) {
                    String key = nutrient.getNutrientName() + " (" + nutrient.getUnitName() + ")";
                    totalNutrients.computeIfAbsent(key, k -> new NutrientInfoDTO(nutrient.getNutrientName(), 0.0, nutrient.getUnitName()))
                            .setValue(totalNutrients.get(key).getValue() + nutrientValue);

                    switch (nutrient.getNutrientName().toLowerCase()) {
                        case "energy":
                            if (nutrient.getUnitName().equalsIgnoreCase("kcal")) {
                                macronutrients.setKcals(macronutrients.getKcals() + nutrientValue);
                            } else if (nutrient.getUnitName().equalsIgnoreCase("kj")) {
                                macronutrients.setKcals(macronutrients.getKcals() + nutrientValue / 4.184);
                            }
                            break;
                        case "protein":
                            macronutrients.setProteins(macronutrients.getProteins() + nutrientValue);
                            break;
                        case "total lipid (fat)":
                            macronutrients.setFats(macronutrients.getFats() + nutrientValue);
                            break;
                        case "carbohydrates":
                            macronutrients.setCarbohydrates(macronutrients.getCarbohydrates() + nutrientValue);
                            break;
                        default:
                            break;
                    }

                    System.out.println("Updated total for " + key + ": " + totalNutrients.get(key).getValue());
                }
            }
        }

        // Voeg macronutriënten toe aan de totale nutriëntenkaart
        totalNutrients.put("Proteins (g)", new NutrientInfoDTO("Proteins", macronutrients.getProteins(), "g"));
        totalNutrients.put("Carbohydrates (g)", new NutrientInfoDTO("Carbohydrates", macronutrients.getCarbohydrates(), "g"));
        totalNutrients.put("Fats (g)", new NutrientInfoDTO("Fats", macronutrients.getFats(), "g"));
        totalNutrients.put("Energy (kcal)", new NutrientInfoDTO("Energy", macronutrients.getKcals(), "kcal"));

        // Log de uiteindelijke macronutriënten
        System.out.println("Final Macronutrient Totals:");
        System.out.println("Total Proteins: " + macronutrients.getProteins() + " g");
        System.out.println("Total Carbohydrates: " + macronutrients.getCarbohydrates() + " g");
        System.out.println("Total Fats: " + macronutrients.getFats() + " g");
        System.out.println("Total Energy: " + macronutrients.getKcals() + " kcal");

        return totalNutrients;
    }
}

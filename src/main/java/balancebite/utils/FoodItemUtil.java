package balancebite.utils;

import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.model.foodItem.FoodItem;
import balancebite.model.NutrientInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class that provides common operations for FoodItem entities.
 * This class contains static methods that help in converting USDA food response data into FoodItem entities.
 */
public class FoodItemUtil {

    /**
     * Converts a UsdaFoodResponseDTO to a FoodItem entity.
     * <p>
     * This method takes a response from the USDA API and converts it into a FoodItem entity, including
     * relevant nutritional information and portion details.
     * </p>
     *
     * @param response The USDA food response DTO containing details about the food item.
     * @return A FoodItem entity constructed from the response data.
     * @throws IllegalArgumentException if the response is null, lacks nutrients, or has an empty description.
     */
    public static FoodItem convertToFoodItem(UsdaFoodResponseDTO response) {
        // Validate the response to ensure it contains necessary data
        if (response == null || response.getFoodNutrients() == null || response.getDescription() == null || response.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Invalid USDA Food Response: The response must contain valid description and nutrient information.");
        }

        // Extract the primary portion from the response if available
        String portionDescription = null;
        double gramWeight = 0;
        if (response.getFoodPortions() != null && !response.getFoodPortions().isEmpty()) {
            UsdaFoodResponseDTO.FoodPortionDTO portion = response.getFoodPortions().get(0);
            portionDescription = portion.getAmount() + " " + (portion.getModifier() != null ? portion.getModifier() : portion.getMeasureUnit().getName());
            gramWeight = portion.getGramWeight();
        }

// Create the FoodItem entity using the data from the response
        FoodItem foodItem = new FoodItem(
                response.getDescription(), // The name/description of the food item
                response.getFdcId(), // The FDC ID from the response
                portionDescription, // Description of the portion size (e.g., "1 cup")
                gramWeight // Weight of the portion in grams
        );

// Set the nutrients using the setter method
        List<NutrientInfo> nutrients = response.getFoodNutrients().stream()
                .map(n -> new NutrientInfo(
                        n.getNutrient().getName(), // Name of the nutrient
                        n.getAmount(), // Amount of the nutrient in the food item
                        n.getUnitName(), // Unit of measurement for the nutrient
                        n.getNutrient().getNutrientId() // Unique ID of the nutrient
                ))
                .collect(Collectors.toList());
        foodItem.setNutrients(nutrients);

// Return the constructed FoodItem entity
        return foodItem;

    }
}

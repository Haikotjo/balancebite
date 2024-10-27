package balancebite.dto.fooditem;

import balancebite.dto.NutrientInfoDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object (DTO) representing a FoodItem.
 * This DTO is used to transfer food item data between layers of the application.
 * It calculates combined fat values for easier data handling.
 */
public class FoodItemDTO {

    /**
     * Unique identifier for the food item.
     */
    private final Long id;

    /**
     * Name of the food item.
     */
    private final String name;

    /**
     * The FDC (FoodData Central) ID associated with the food item.
     */
    private final int fdcId;

    /**
     * List of nutrients associated with the food item.
     */
    private final List<NutrientInfoDTO> nutrients;

    /**
     * Description of the portion, such as "1 medium banana".
     */
    private final String portionDescription;

    /**
     * The gram weight of the portion.
     */
    private final double gramWeight;

    /**
     * Parameterized constructor to create a FoodItemDTO.
     * Calculates the combined values for healthy and unhealthy fats.
     *
     * @param id The unique identifier of the food item.
     * @param name The name of the food item.
     * @param fdcId The FDC ID of the food item.
     * @param nutrients The list of nutrients associated with the food item.
     * @param portionDescription The description of the portion.
     * @param gramWeight The gram weight of the portion.
     */
    public FoodItemDTO(Long id, String name, int fdcId, List<NutrientInfoDTO> nutrients, String portionDescription, double gramWeight) {
        this.id = id;
        this.name = name;
        this.fdcId = fdcId;
        this.nutrients = processNutrients(nutrients);
        this.portionDescription = portionDescription;
        this.gramWeight = gramWeight;
    }

    /**
     * Processes the nutrients list to combine values of healthy and unhealthy fats.
     * Adds new entries for "Mono- and Polyunsaturated fats" and "Saturated and Trans fats".
     *
     * @param nutrients The original list of nutrients.
     * @return A list of nutrients with combined fat values.
     */
    private List<NutrientInfoDTO> processNutrients(List<NutrientInfoDTO> nutrients) {
        double totalHealthyFats = 0.0;
        double totalUnhealthyFats = 0.0;

        // Create a copy of the nutrient list to modify.
        List<NutrientInfoDTO> updatedNutrients = new ArrayList<>(nutrients);

        // Calculate the sum of healthy and unhealthy fats.
        for (NutrientInfoDTO nutrient : nutrients) {
            switch (nutrient.getNutrientName()) {
                case "Fatty acids, total monounsaturated":
                case "Fatty acids, total polyunsaturated":
                    totalHealthyFats += nutrient.getValue();
                    break;
                case "Fatty acids, total saturated":
                case "Fatty acids, total trans":
                    totalUnhealthyFats += nutrient.getValue();
                    break;
                default:
                    // No action needed for other nutrients.
                    break;
            }
        }

        // Remove individual fat entries from the nutrient list.
        updatedNutrients.removeIf(nutrient ->
                nutrient.getNutrientName().equals("Fatty acids, total monounsaturated") ||
                        nutrient.getNutrientName().equals("Fatty acids, total polyunsaturated") ||
                        nutrient.getNutrientName().equals("Fatty acids, total saturated") ||
                        nutrient.getNutrientName().equals("Fatty acids, total trans")
        );

        // Add combined entries if their values are greater than zero.
        if (totalHealthyFats > 0) {
            updatedNutrients.add(new NutrientInfoDTO("Mono- and Polyunsaturated fats", totalHealthyFats, "g"));
        }
        if (totalUnhealthyFats > 0) {
            updatedNutrients.add(new NutrientInfoDTO("Saturated and Trans fats", totalUnhealthyFats, "g"));
        }

        return List.copyOf(updatedNutrients);
    }

    // Getters only (no setters)

    /**
     * Gets the unique identifier of the food item.
     *
     * @return The ID of the food item.
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the name of the food item.
     *
     * @return The name of the food item.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the FDC ID of the food item.
     *
     * @return The FDC ID of the food item.
     */
    public int getFdcId() {
        return fdcId;
    }

    /**
     * Gets the list of nutrients associated with the food item.
     *
     * @return An unmodifiable list of nutrients.
     */
    public List<NutrientInfoDTO> getNutrients() {
        return List.copyOf(nutrients);  // Ensure the list cannot be mutated outside this DTO
    }

    /**
     * Gets the description of the portion.
     *
     * @return The description of the portion.
     */
    public String getPortionDescription() {
        return portionDescription;
    }

    /**
     * Gets the gram weight of the portion.
     *
     * @return The gram weight of the portion.
     */
    public double getGramWeight() {
        return gramWeight;
    }
}

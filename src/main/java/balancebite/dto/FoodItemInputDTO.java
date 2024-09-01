package balancebite.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing the input data for creating a FoodItem.
 * This DTO is used to receive food item data from the client.
 */
public class FoodItemInputDTO {

    /**
     * Name of the food item.
     */
    private String name;

    /**
     * List of nutrients associated with the food item.
     */
    private List<NutrientInfoDTO> nutrients;

    /**
     * Description of the portion, such as "1 medium banana".
     */
    private String portionDescription;

    /**
     * The gram weight of the portion.
     */
    private double gramWeight;

    /**
     * No-argument constructor required for deserialization.
     */
    public FoodItemInputDTO() {}

    /**
     * Parameterized constructor to create a FoodItemInputDTO.
     *
     * @param name The name of the food item.
     * @param nutrients The list of nutrients associated with the food item.
     * @param portionDescription The description of the portion.
     * @param gramWeight The gram weight of the portion.
     */
    public FoodItemInputDTO(String name, List<NutrientInfoDTO> nutrients, String portionDescription, double gramWeight) {
        this.name = name;
        this.nutrients = nutrients;
        this.portionDescription = portionDescription;
        this.gramWeight = gramWeight;
    }

    // Getters and setters

    /**
     * Gets the name of the food item.
     *
     * @return The name of the food item.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the food item.
     *
     * @param name The new name of the food item.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of nutrients associated with the food item.
     *
     * @return The list of nutrients.
     */
    public List<NutrientInfoDTO> getNutrients() {
        return nutrients;
    }

    /**
     * Sets the list of nutrients for the food item.
     *
     * @param nutrients The new list of nutrients.
     */
    public void setNutrients(List<NutrientInfoDTO> nutrients) {
        this.nutrients = nutrients;
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
     * Sets the description of the portion.
     *
     * @param portionDescription The new description of the portion.
     */
    public void setPortionDescription(String portionDescription) {
        this.portionDescription = portionDescription;
    }

    /**
     * Gets the gram weight of the portion.
     *
     * @return The gram weight of the portion.
     */
    public double getGramWeight() {
        return gramWeight;
    }

    /**
     * Sets the gram weight of the portion.
     *
     * @param gramWeight The new gram weight of the portion.
     */
    public void setGramWeight(double gramWeight) {
        this.gramWeight = gramWeight;
    }
}

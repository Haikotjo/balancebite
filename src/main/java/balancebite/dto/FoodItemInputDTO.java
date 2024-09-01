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
     * No-argument constructor required for deserialization.
     */
    public FoodItemInputDTO() {}

    /**
     * Parameterized constructor to create a FoodItemInputDTO.
     *
     * @param name The name of the food item.
     * @param nutrients The list of nutrients associated with the food item.
     */
    public FoodItemInputDTO(String name, List<NutrientInfoDTO> nutrients) {
        this.name = name;
        this.nutrients = nutrients;
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
}

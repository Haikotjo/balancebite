package balancebite.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a FoodItem.
 * This DTO is used to transfer food item data between layers of the application.
 */
public class FoodItemDTO {

    /**
     * Unique identifier for the food item.
     */
    private Long id;

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
    public FoodItemDTO() {}

    /**
     * Parameterized constructor to create a FoodItemDTO.
     *
     * @param id The unique identifier of the food item.
     * @param name The name of the food item.
     * @param nutrients The list of nutrients associated with the food item.
     */
    public FoodItemDTO(Long id, String name, List<NutrientInfoDTO> nutrients) {
        this.id = id;
        this.name = name;
        this.nutrients = nutrients;
    }

    // Getters and setters

    /**
     * Gets the unique identifier of the food item.
     *
     * @return The ID of the food item.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the food item.
     *
     * @param id The new ID of the food item.
     */
    public void setId(Long id) {
        this.id = id;
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

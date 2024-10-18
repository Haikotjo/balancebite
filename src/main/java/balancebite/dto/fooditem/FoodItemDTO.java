package balancebite.dto.fooditem;

import balancebite.dto.NutrientInfoDTO;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a FoodItem.
 * This DTO is used to transfer food item data between layers of the application.
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
     *
     * @param id The unique identifier of the food item.
     * @param name The name of the food item.
     * @param nutrients The list of nutrients associated with the food item.
     * @param portionDescription The description of the portion.
     * @param gramWeight The gram weight of the portion.
     */
    public FoodItemDTO(Long id, String name, List<NutrientInfoDTO> nutrients, String portionDescription, double gramWeight) {
        this.id = id;
        this.name = name;
        this.nutrients = (nutrients != null) ? List.copyOf(nutrients) : List.of();  // Gebruik een niet-wijzigbare lijst
        this.portionDescription = portionDescription;
        this.gramWeight = gramWeight;
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
     * Gets the list of nutrients associated with the food item.
     *
     * @return An unmodifiable list of nutrients.
     */
    public List<NutrientInfoDTO> getNutrients() {
        return List.copyOf(nutrients);  // Zorg ervoor dat de lijst niet gemuteerd kan worden buiten deze DTO
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

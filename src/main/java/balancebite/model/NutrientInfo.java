package balancebite.model;

import jakarta.persistence.Embeddable;

/**
 * Embeddable class representing nutrient information.
 * This class is used as part of the FoodItem entity to store nutrient details.
 */
@Embeddable
public class NutrientInfo {

    /**
     * Name of the nutrient.
     */
    private String nutrientName;

    /**
     * Amount of the nutrient.
     */
    private Double value;

    /**
     * Unit of the nutrient amount (e.g., mg, g).
     */
    private String unitName;

    /**
     * Unique identifier of the nutrient.
     */
    private Long nutrientId;

    /**
     * No-argument constructor required by JPA.
     */
    public NutrientInfo() {}

    /**
     * Parameterized constructor to create a NutrientInfo.
     *
     * @param nutrientName Name of the nutrient.
     * @param value Amount of the nutrient.
     * @param unitName Unit of the nutrient amount.
     * @param nutrientId Unique identifier of the nutrient.
     */
    public NutrientInfo(String nutrientName, Double value, String unitName, Long nutrientId) {
        this.nutrientName = nutrientName;
        this.value = value;
        this.unitName = unitName;
        this.nutrientId = nutrientId;
    }

    // Getters and setters

    /**
     * Gets the name of the nutrient.
     *
     * @return The name of the nutrient.
     */
    public String getNutrientName() {
        return nutrientName;
    }

    /**
     * Sets the name of the nutrient.
     *
     * @param nutrientName The new name of the nutrient.
     */
    public void setNutrientName(String nutrientName) {
        this.nutrientName = nutrientName;
    }

    /**
     * Gets the amount of the nutrient.
     *
     * @return The amount of the nutrient.
     */
    public Double getValue() {
        return value;
    }

    /**
     * Sets the amount of the nutrient.
     *
     * @param value The new amount of the nutrient.
     */
    public void setValue(Double value) {
        this.value = value;
    }

    /**
     * Gets the unit of the nutrient amount.
     *
     * @return The unit of the nutrient amount.
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * Sets the unit of the nutrient amount.
     *
     * @param unitName The new unit of the nutrient amount.
     */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    /**
     * Gets the unique identifier of the nutrient.
     *
     * @return The ID of the nutrient.
     */
    public Long getNutrientId() {
        return nutrientId;
    }

    /**
     * Sets the unique identifier of the nutrient.
     *
     * @param nutrientId The new ID of the nutrient.
     */
    public void setNutrientId(Long nutrientId) {
        this.nutrientId = nutrientId;
    }
}

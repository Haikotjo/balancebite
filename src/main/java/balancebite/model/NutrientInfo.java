package balancebite.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class NutrientInfo {
    private String nutrientName;
    private Double value;
    private String unitName;
    private Long nutrientId;

    // No-argument constructor
    public NutrientInfo() {}

    // Parameterized constructor
    public NutrientInfo(String nutrientName, Double value, String unitName, Long nutrientId) {
        this.nutrientName = nutrientName;
        this.value = value;
        this.unitName = unitName;
        this.nutrientId = nutrientId;
    }

    // Getters and setters
    public String getNutrientName() {
        return nutrientName;
    }

    public void setNutrientName(String nutrientName) {
        this.nutrientName = nutrientName;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Long getNutrientId() {
        return nutrientId;
    }

    public void setNutrientId(Long nutrientId) {
        this.nutrientId = nutrientId;
    }
}

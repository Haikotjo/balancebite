package balancebite.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class NutrientInfo {
    private String nutrientName;
    private Double value;
    private String unitName;

    // No-argument constructor
    public NutrientInfo() {}

    // Parameterized constructor
    public NutrientInfo(String nutrientName, Double value, String unitName) {
        this.nutrientName = nutrientName;
        this.value = value;
        this.unitName = unitName;
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
}

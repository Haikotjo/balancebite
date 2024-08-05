package balancebite.dto;

public class NutrientDTO {
    private Long id;
    private String nutrientName;
    private Double value;
    private String unitName;

    // No-argument constructor
    public NutrientDTO() {
    }

    // Parameterized constructor
    public NutrientDTO(Long id, String nutrientName, Double value, String unitName) {
        this.id = id;
        this.nutrientName = nutrientName;
        this.value = value;
        this.unitName = unitName;
    }

    // Getters en setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

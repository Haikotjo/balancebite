package balancebite.dto;

public class NutrientInfoDTO {
    private String nutrientName;
    private Double value;
    private String unitName;
    private Long nutrientId;
    private String typeName;

    // No-argument constructor
    public NutrientInfoDTO() {}

    // For calcualtion in mealservice
    public NutrientInfoDTO(String nutrientName, Double value, String unitName) {
        this.nutrientName = nutrientName;
        this.value = value;
        this.unitName = unitName;
    }

    // Parameterized constructor
    public NutrientInfoDTO(String nutrientName, Double value, String unitName, Long nutrientId, String typeName) {
        this.nutrientName = nutrientName;
        this.value = value;
        this.unitName = unitName;
        this.nutrientId = nutrientId;
        this.typeName = typeName;
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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}

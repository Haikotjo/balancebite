package balancebite.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Nutrient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nutrientName;
    private Double value;
    private String unitName;

    // No-argument constructor
    public Nutrient() {
    }

    // Parameterized constructor
    public Nutrient(String nutrientName, Double value, String unitName) {
        this.nutrientName = nutrientName;
        this.value = value;
        this.unitName = unitName;
    }

    // getters en setters

    public Long getId() {
        return id;
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
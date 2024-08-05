package balancebite.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NutrientAPIDTO {

    private String description;
    private List<FoodNutrientDTO> foodNutrients;

    // Getters and Setters

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FoodNutrientDTO> getFoodNutrients() {
        return foodNutrients;
    }

    public void setFoodNutrients(List<FoodNutrientDTO> foodNutrients) {
        this.foodNutrients = foodNutrients;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FoodNutrientDTO {

        @JsonProperty("nutrient")
        private NutrientDTO nutrient;

        @JsonProperty("amount")
        private double amount;

        // Getters and Setters

        public NutrientDTO getNutrient() {
            return nutrient;
        }

        public void setNutrient(NutrientDTO nutrient) {
            this.nutrient = nutrient;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getUnitName() {
            return nutrient != null ? nutrient.getUnitName() : null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NutrientDTO {
        @JsonProperty("name")
        private String name;

        @JsonProperty("unitName")
        private String unitName;

        // Getters and Setters

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUnitName() {
            return unitName;
        }

        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }
    }
}

package balancebite.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UsdaFoodResponseDTO {

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
        private Nutrient nutrient;

        @JsonProperty("amount")
        private double amount;

        // Getters and Setters

        public Nutrient getNutrient() {
            return nutrient;
        }

        public void setNutrient(Nutrient nutrient) {
            this.nutrient = nutrient;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Nutrient {
        @JsonProperty("name")
        private String name;

        // Getters and Setters

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

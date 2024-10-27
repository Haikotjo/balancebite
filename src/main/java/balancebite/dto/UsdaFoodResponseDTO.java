package balancebite.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object (DTO) for deserializing the response from the USDA FoodData Central API.
 * This DTO represents a food item and its associated nutrients and portion details.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsdaFoodResponseDTO {

    /**
     * The description or name of the food item.
     */
    private String description;

    /**
     * The list of nutrients associated with the food item.
     */
    private List<FoodNutrientDTO> foodNutrients;

    /**
     * The list of portion options associated with the food item.
     */
    private List<FoodPortionDTO> foodPortions;

    /**
     * The unique FDC ID of the food item.
     */
    @JsonProperty("fdcId")
    private int fdcId;

    // Define the list of nutrient names to include in the filtering.
    private static final List<String> NUTRIENT_NAMES = List.of(
            "Energy kcal", "Protein", "Total lipid (fat)", "Carbohydrates",
            "Carbohydrate, by difference", "Fiber, total dietary", "Total Sugars",
            "Fatty acids, total saturated", "Fatty acids, total monounsaturated",
            "Fatty acids, total polyunsaturated", "Fatty acids, total trans"
            // Uncomment the following lines if you wish to include them later:
            // "Calcium, Ca", "Iron, Fe", "Magnesium, Mg", "Phosphorus, P",
            // "Potassium, K", "Sodium, Na", "Zinc, Zn", "Vitamin C, total ascorbic acid",
            // "Vitamin D (D2 + D3)", "Vitamin B-12", "Vitamin A, RAE", "Vitamin E (alpha-tocopherol)"
            // "Copper, Cu", "Manganese, Mn", "Selenium, Se",
            // "Thiamin", "Riboflavin", "Niacin", "Pantothenic acid", "Vitamin B-6",
            // "Folate, total", "Folic acid", "Folate, food", "Folate, DFE",
            // "Choline, total", "Vitamin B-12, added", "Vitamin A, IU",
            // "Retinol", "Carotene, beta", "Carotene, alpha",
            // "Cryptoxanthin, beta", "Lycopene", "Lutein + zeaxanthin",
            // "Vitamin E, added", "Vitamin D (D2 + D3), International Units",
            // "Vitamin K (phylloquinone)", "Vitamin K (Dihydrophylloquinone)",
            // "SFA 4:0", "SFA 6:0", "SFA 8:0", "SFA 10:0", "SFA 12:0",
            // "SFA 14:0", "SFA 16:0", "SFA 18:0", "MUFA 16:1", "MUFA 18:1",
            // "MUFA 20:1", "MUFA 22:1", "PUFA 18:2", "PUFA 18:3", "PUFA 18:4",
            // "PUFA 20:4", "PUFA 20:5 n-3 (EPA)", "PUFA 22:5 n-3 (DPA)",
            // "PUFA 22:6 n-3 (DHA)", "Cholesterol", "Alcohol, ethyl", "Caffeine",
            // "Theobromine", "Sucrose", "Glucose", "Fructose", "Lactose",
            // "Maltose", "Galactose", "Starch", "Betaine", "Tocopherol, beta",
            // "Tocopherol, gamma", "Tocopherol, delta", "Tocotrienol, alpha",
            // "Tocotrienol, beta", "Tocotrienol, gamma", "Tocotrienol, delta",
            // "SFA 15:0", "SFA 17:0", "SFA 20:0", "SFA 22:0", "SFA 24:0",
            // "MUFA 14:1", "Fluoride, F", "Phytosterols"
    );

    // Getters and Setters

    /**
     * Gets the description or name of the food item.
     *
     * @return The description of the food item.
     */
    public String getDescription() {
        return description != null ? description : "";
    }

    /**
     * Sets the description or name of the food item.
     *
     * @param description The description of the food item.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the unique FDC ID of the food item.
     *
     * @return The FDC ID of the food item.
     */
    public int getFdcId() {
        return fdcId;
    }

    /**
     * Sets the unique FDC ID of the food item.
     *
     * @param fdcId The FDC ID of the food item.
     */
    public void setFdcId(int fdcId) {
        this.fdcId = fdcId;
    }

    /**
     * Gets the list of nutrients associated with the food item after filtering.
     *
     * @return The list of filtered FoodNutrientDTO representing the nutrients.
     */
    public List<FoodNutrientDTO> getFoodNutrients() {
        // Apply filtering based on nutrient name.
        return foodNutrients != null
                ? foodNutrients.stream()
                .filter(nutrient -> NUTRIENT_NAMES.contains(nutrient.getNutrient().getName()))
                .collect(Collectors.toList())
                : List.of();
    }

    /**
     * Sets the list of nutrients associated with the food item.
     *
     * @param foodNutrients The list of FoodNutrientDTO representing the nutrients.
     */
    public void setFoodNutrients(List<FoodNutrientDTO> foodNutrients) {
        this.foodNutrients = foodNutrients;
    }

    /**
     * Gets the list of portion options associated with the food item.
     *
     * @return The list of FoodPortionDTO representing the portion options.
     */
    public List<FoodPortionDTO> getFoodPortions() {
        return foodPortions != null ? foodPortions : List.of();
    }

    /**
     * Sets the list of portion options associated with the food item.
     *
     * @param foodPortions The list of FoodPortionDTO representing the portion options.
     */
    public void setFoodPortions(List<FoodPortionDTO> foodPortions) {
        this.foodPortions = foodPortions;
    }

    /**
     * Nested DTO class representing the nutrient information.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FoodNutrientDTO {

        @JsonProperty("nutrient")
        private NutrientDTO nutrient;

        @JsonProperty("amount")
        private double amount;

        // Getters and Setters

        /**
         * Gets the nutrient details.
         *
         * @return The NutrientDTO representing the nutrient details.
         */
        public NutrientDTO getNutrient() {
            return nutrient != null ? nutrient : new NutrientDTO();
        }

        /**
         * Sets the nutrient details.
         *
         * @param nutrient The NutrientDTO representing the nutrient details.
         */
        public void setNutrient(NutrientDTO nutrient) {
            this.nutrient = nutrient;
        }

        /**
         * Gets the amount of the nutrient in the food item.
         *
         * @return The amount of the nutrient.
         */
        public double getAmount() {
            return amount;
        }

        /**
         * Sets the amount of the nutrient in the food item.
         *
         * @param amount The amount of the nutrient.
         */
        public void setAmount(double amount) {
            this.amount = amount;
        }

        /**
         * Gets the unit name of the nutrient (e.g., mg, g).
         *
         * @return The unit name of the nutrient.
         */
        public String getUnitName() {
            return nutrient != null ? nutrient.getUnitName() : "";
        }
    }

    /**
     * Nested DTO class representing the detailed information about a nutrient.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NutrientDTO {

        @JsonProperty("id")
        private Long nutrientId;  // The ID of the nutrient

        @JsonProperty("name")
        private String name;

        @JsonProperty("unitName")
        private String unitName;

        // Getters and Setters

        /**
         * Gets the ID of the nutrient.
         *
         * @return The nutrient ID.
         */
        public Long getNutrientId() {
            return nutrientId != null ? nutrientId : 0L;
        }

        /**
         * Sets the ID of the nutrient.
         *
         * @param nutrientId The nutrient ID.
         */
        public void setNutrientId(Long nutrientId) {
            this.nutrientId = nutrientId;
        }

        /**
         * Gets the name of the nutrient.
         *
         * @return The name of the nutrient.
         */
        public String getName() {
            return name != null ? name : "";
        }

        /**
         * Sets the name of the nutrient.
         *
         * @param name The name of the nutrient.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets the unit name of the nutrient.
         *
         * @return The unit name of the nutrient.
         */
        public String getUnitName() {
            return unitName != null ? unitName : "";
        }

        /**
         * Sets the unit name of the nutrient.
         *
         * @param unitName The unit name of the nutrient.
         */
        public void setUnitName(String unitName) {
            this.unitName = unitName;
        }
    }

    /**
     * Nested DTO class representing the portion information.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FoodPortionDTO {

        @JsonProperty("amount")
        private double amount;

        @JsonProperty("gramWeight")
        private double gramWeight;

        @JsonProperty("measureUnit")
        private MeasureUnitDTO measureUnit;

        @JsonProperty("modifier")
        private String modifier;

        // Getters and Setters

        /**
         * Gets the amount of the portion.
         *
         * @return The amount of the portion.
         */
        public double getAmount() {
            return amount;
        }

        /**
         * Sets the amount of the portion.
         *
         * @param amount The amount of the portion.
         */
        public void setAmount(double amount) {
            this.amount = amount;
        }

        /**
         * Gets the gram weight of the portion.
         *
         * @return The gram weight of the portion.
         */
        public double getGramWeight() {
            return gramWeight;
        }

        /**
         * Sets the gram weight of the portion.
         *
         * @param gramWeight The gram weight of the portion.
         */
        public void setGramWeight(double gramWeight) {
            this.gramWeight = gramWeight;
        }

        /**
         * Gets the measure unit details of the portion.
         *
         * @return The MeasureUnitDTO representing the measure unit details.
         */
        public MeasureUnitDTO getMeasureUnit() {
            return measureUnit != null ? measureUnit : new MeasureUnitDTO();
        }

        /**
         * Sets the measure unit details of the portion.
         *
         * @... {

        this.measureUnit = measureUnit;
        }

        /**
         * Gets the modifier for the portion (e.g., medium, large).
         *
         * @return The modifier for the portion.
         */
        public String getModifier() {
            return modifier != null ? modifier : "";
        }

        /**
         * Sets the modifier for the portion (e.g., medium, large).
         *
         * @param modifier The modifier for the portion.
         */
        public void setModifier(String modifier) {
            this.modifier = modifier;
        }
    }

    /**
     * Nested DTO class representing the measurement unit details.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MeasureUnitDTO {

        @JsonProperty("name")
        private String name;

        // Getter and Setter

        /**
         * Gets the name of the measurement unit.
         *
         * @return The name of the measurement unit.
         */
        public String getName() {
            return name != null ? name : "";
        }

        /**
         * Sets the name of the measurement unit.
         *
         * @param name The name of the measurement unit.
         */
        public void setName(String name) {
            this.name = name;
        }
    }
}

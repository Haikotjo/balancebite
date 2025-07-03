package balancebite.dto.fooditem;

import balancebite.dto.NutrientInfoDTO;
import balancebite.model.foodItem.FoodCategory;
import jakarta.validation.constraints.*;
import balancebite.model.foodItem.FoodSource;


import java.util.List;

/**
 * Input DTO class for creating or updating a FoodItem.
 * This DTO is used to transfer input data from client to server.
 */
public class FoodItemInputDTO {

    /**
     * Name of the food item.
     * Cannot be null or empty.
     */
    @NotBlank(message = "The name of the food item cannot be blank.")
    private String name;

    /**
     * The FDC (FoodData Central) ID associated with the food item.
     * Must be greater than zero.
     */
    @NotNull(message = "The FDC ID must be provided.")
    @Positive(message = "The FDC ID must be a positive integer.")
    private int fdcId;

    /**
     * List of nutrients associated with the food item.
     * Cannot be null.
     */
    @NotEmpty(message = "Nutrients list must contain at least one nutrient.")
    private List<NutrientInfoDTO> nutrients;

    /**
     * Description of the portion, such as "1 medium banana".
     */
    private String portionDescription;

    /**
     * The gram weight of the portion.
     * Must be greater than zero.
     */
    @NotNull(message = "The gram weight must be provided.")
    @PositiveOrZero(message = "The gram weight must be greater than or equal to zero.")
    private Double gramWeight;

    /**
     * Optional source indicating where the food item was purchased (e.g., supermarket name).
     */
    private String source;

    /**
     * Optional structured source of the food item, selected from predefined values (e.g., supermarkets).
     * This field complements the free-text 'source' field and provides consistent reference.
     */
    private FoodSource foodSource;

    private FoodCategory foodCategory;

    // Constructor, getters, and setters

    public FoodItemInputDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFdcId() {
        return fdcId;
    }

    public void setFdcId(int fdcId) {
        this.fdcId = fdcId;
    }

    public List<NutrientInfoDTO> getNutrients() {
        return nutrients;
    }

    public void setNutrients(List<NutrientInfoDTO> nutrients) {
        this.nutrients = nutrients;
    }

    public String getPortionDescription() {
        return portionDescription;
    }

    public void setPortionDescription(String portionDescription) {
        this.portionDescription = portionDescription;
    }

    public Double getGramWeight() {
        return gramWeight;
    }

    public void setGramWeight(Double gramWeight) {
        this.gramWeight = gramWeight;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public FoodSource getFoodSource() {
        return foodSource;
    }

    public void setFoodSource(FoodSource foodSource) {
        this.foodSource = foodSource;
    }

    public FoodCategory getFoodCategory() {
        return foodCategory;
    }

    public void setFoodCategory(FoodCategory foodCategory) {
        this.foodCategory = foodCategory;
    }

}

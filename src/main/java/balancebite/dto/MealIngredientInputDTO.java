package balancebite.dto;

/**
 * Data Transfer Object (DTO) for capturing input data related to a Meal Ingredient.
 * This class is used for receiving data from the client-side when creating or updating
 * a MealIngredient in the application.
 */
public class MealIngredientInputDTO {

    private Long foodItemId;
    private double quantity;
    private Boolean usePortion;

    // Constructors

    /**
     * Default constructor for MealIngredientInputDTO.
     */
    public MealIngredientInputDTO() {}

    /**
     * Parameterized constructor for MealIngredientInputDTO.
     *
     * @param foodItemId the ID of the food item associated with this ingredient.
     * @param quantity the quantity of the food item in this meal.
     * @param usePortion flag indicating whether the standard portion size should be used.
     */
    public MealIngredientInputDTO(Long foodItemId, double quantity, Boolean usePortion) {
        this.foodItemId = foodItemId;
        this.quantity = quantity;
        this.usePortion = usePortion;
    }

    // Getters

    /**
     * Gets the ID of the food item associated with this ingredient.
     *
     * @return the ID of the food item.
     */
    public Long getFoodItemId() {
        return foodItemId;
    }

    /**
     * Gets the quantity of the food item in this meal.
     *
     * @return the quantity of the food item.
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Gets the flag indicating whether the standard portion size should be used.
     *
     * @return true if the standard portion size should be used, false otherwise.
     */
    public Boolean getUsePortion() {
        return usePortion;
    }

    // Setters

    /**
     * Sets the ID of the food item associated with this ingredient.
     *
     * @param foodItemId the ID of the food item to set.
     */
    public void setFoodItemId(Long foodItemId) {
        this.foodItemId = foodItemId;
    }

    /**
     * Sets the quantity of the food item in this meal.
     *
     * @param quantity the quantity of the food item to set.
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    /**
     * Sets the flag indicating whether the standard portion size should be used.
     *
     * @param usePortion true to use the standard portion size, false otherwise.
     */
    public void setUsePortion(Boolean usePortion) {
        this.usePortion = usePortion;
    }
}

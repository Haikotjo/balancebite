package balancebite.dto.mealingredient;

/**
 * Data Transfer Object (DTO) for capturing input data related to a Meal Ingredient.
 * This class is used for receiving data from the client-side when creating or updating
 * a MealIngredient in the application.
 */
public class MealIngredientInputDTO {

    private Long foodItemId;
    private Double quantity;

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
     */
    public MealIngredientInputDTO(Long foodItemId, double quantity) {
        this.foodItemId = foodItemId;
        this.quantity = quantity;
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
    public Double getQuantity() {
        return quantity;
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
}

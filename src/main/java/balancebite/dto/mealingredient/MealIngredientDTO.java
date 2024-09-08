package balancebite.dto.mealingredient;

/**
 * Data Transfer Object (DTO) representing the data of a Meal Ingredient.
 * This class is used to transfer data between different layers of the application.
 */
public class MealIngredientDTO {

    private Long id;
    private Long mealId;
    private Long foodItemId;
    private double quantity;

    // Constructors

    /**
     * Default constructor for MealIngredientDTO.
     */
    public MealIngredientDTO() {}

    /**
     * Parameterized constructor for MealIngredientDTO.
     *
     * @param id the ID of the meal ingredient.
     * @param mealId the ID of the meal associated with this ingredient.
     * @param foodItemId the ID of the food item associated with this ingredient.
     * @param quantity the quantity of the food item in this meal.
     */
    public MealIngredientDTO(Long id, Long mealId, Long foodItemId, double quantity) {
        this.id = id;
        this.mealId = mealId;
        this.foodItemId = foodItemId;
        this.quantity = quantity;
    }

    // Getters

    /**
     * Gets the ID of the meal ingredient.
     *
     * @return the ID of the meal ingredient.
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the ID of the meal associated with this ingredient.
     *
     * @return the ID of the meal.
     */
    public Long getMealId() {
        return mealId;
    }

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

    // Setters

    /**
     * Sets the ID of the meal ingredient.
     *
     * @param id the ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the ID of the meal associated with this ingredient.
     *
     * @param mealId the ID of the meal to set.
     */
    public void setMealId(Long mealId) {
        this.mealId = mealId;
    }

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
     * @param quantity the quantity to set.
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}

package balancebite.dto.mealingredient;

/**
 * Data Transfer Object (DTO) representing the data of a Meal Ingredient.
 * This class is used to transfer data between different layers of the application.
 */
public class MealIngredientDTO {

    private final Long id;
    private final Long mealId;
    private final Long foodItemId;
    private final String foodItemName;
    private final double quantity;

    // Constructor

    /**
     * Parameterized constructor for MealIngredientDTO.
     *
     * @param id the ID of the meal ingredient.
     * @param mealId the ID of the meal associated with this ingredient.
     * @param foodItemId the ID of the food item associated with this ingredient.
     * @param foodItemName the name of the food item associated with this ingredient.
     * @param quantity the quantity of the food item in this meal.
     */
    public MealIngredientDTO(Long id, Long mealId, Long foodItemId, String foodItemName, double quantity) {
        this.id = id;
        this.mealId = mealId;
        this.foodItemId = foodItemId;
        this.foodItemName = foodItemName;
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
     * Gets the name of the food item associated with this ingredient.
     *
     * @return the name of the food item.
     */
    public String getFoodItemName() {
        return foodItemName;
    }

    /**
     * Gets the quantity of the food item in this meal.
     *
     * @return the quantity of the food item.
     */
    public double getQuantity() {
        return quantity;
    }
}

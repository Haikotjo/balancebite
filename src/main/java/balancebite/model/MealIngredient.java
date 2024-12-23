package balancebite.model;

import jakarta.persistence.*;

/**
 * Entity representing an ingredient in a meal.
 */
@Entity
@Table(name = "meal_ingredients")
public class MealIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    private FoodItem foodItem;

    private String foodItemName;

    private double quantity;

    /**
     * No-argument constructor for JPA.
     */
    public MealIngredient() {}

    /**
     * Parameterized constructor for creating a MealIngredient instance.
     *
     * @param meal the meal to which this ingredient belongs.
     * @param foodItem the food item that makes up this ingredient.
     * @param quantity the quantity of the food item in the meal.
     */
    public MealIngredient(Meal meal, FoodItem foodItem, double quantity) {
        this.meal = meal;
        this.foodItem = foodItem;
        this.foodItemName = foodItem.getName();
        this.quantity = quantity;
    }

    // Getters and setters

    /**
     * Gets the ID of this meal ingredient.
     *
     * @return the ID of the meal ingredient.
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the meal to which this ingredient belongs.
     *
     * @return the meal associated with this ingredient.
     */
    public Meal getMeal() {
        return meal;
    }

    /**
     * Sets the meal to which this ingredient belongs.
     *
     * @param meal the meal to associate with this ingredient.
     */
    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    /**
     * Gets the food item that makes up this ingredient.
     *
     * @return the food item associated with this ingredient.
     */
    public FoodItem getFoodItem() {
        return foodItem;
    }

    /**
     * Sets the food item that makes up this ingredient.
     *
     * @param foodItem the food item to associate with this ingredient.
     */
    public void setFoodItem(FoodItem foodItem) {
        this.foodItem = foodItem;
        this.foodItemName = foodItem.getName();
    }

    /**
     * Gets the name of the food item.
     *
     * @return the name of the food item.
     */
    public String getFoodItemName() {
        return foodItemName;
    }

    /**
     * Sets the name of the food item.
     *
     * @param foodItemName the name to set for the food item.
     */
    public void setFoodItemName(String foodItemName) {
        this.foodItemName = foodItemName;
    }

    /**
     * Gets the quantity of the food item in the meal.
     *
     * @return the quantity of the food item.
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the food item in the meal.
     *
     * @param quantity the quantity to set for the food item in the meal.
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}

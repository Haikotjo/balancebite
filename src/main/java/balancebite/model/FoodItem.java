package balancebite.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class representing a food item.
 * This class maps to the "food_items" table in the database.
 */
@Entity
@Table(name = "food_items")
public class FoodItem {

    /**
     * Unique identifier for the food item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the food item.
     */
    private String name;

    /**
     * List of nutrients associated with the food item.
     * This list is stored in the "food_item_nutrients" table with a foreign key reference to this FoodItem.
     */
    @ElementCollection
    @CollectionTable(name = "food_item_nutrients", joinColumns = @JoinColumn(name = "food_item_id"))
    private List<NutrientInfo> nutrients;

    /**
     * List of meal ingredients associated with this food item.
     * This relationship is managed by the MealIngredient entity.
     */
    @OneToMany(mappedBy = "foodItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealIngredient> mealIngredients = new ArrayList<>();

    /**
     * No-argument constructor required by JPA.
     */
    public FoodItem() {}

    /**
     * Constructor to initialize a FoodItem with a name and a list of nutrients.
     *
     * @param name Name of the food item.
     * @param nutrients List of nutrients associated with the food item.
     */
    public FoodItem(String name, List<NutrientInfo> nutrients) {
        this.name = name;
        this.nutrients = nutrients;
    }

    // Getters and setters

    /**
     * Gets the unique identifier of the food item.
     *
     * @return The ID of the food item.
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the name of the food item.
     *
     * @return The name of the food item.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the food item.
     *
     * @param name The new name of the food item.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of nutrients associated with the food item.
     *
     * @return The list of nutrients.
     */
    public List<NutrientInfo> getNutrients() {
        return nutrients;
    }

    /**
     * Sets the list of nutrients for the food item.
     *
     * @param nutrients The new list of nutrients.
     */
    public void setNutrients(List<NutrientInfo> nutrients) {
        this.nutrients = nutrients;
    }

    /**
     * Gets the list of meal ingredients associated with the food item.
     *
     * @return The list of meal ingredients.
     */
    public List<MealIngredient> getMealIngredients() {
        return mealIngredients;
    }

    /**
     * Sets the list of meal ingredients for the food item.
     *
     * @param mealIngredients The new list of meal ingredients.
     */
    public void setMealIngredients(List<MealIngredient> mealIngredients) {
        this.mealIngredients = mealIngredients;
    }
}

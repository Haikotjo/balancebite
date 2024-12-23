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
     * The FDC (FoodData Central) ID associated with the food item.
     * This ID is used to uniquely identify food items in the USDA database.
     */
    private int fdcId;

    /**
     * List of nutrients associated with the food item.
     * This list is stored in the "food_item_nutrients" table with a foreign key reference to this FoodItem.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "food_item_nutrients", joinColumns = @JoinColumn(name = "food_item_id"))
    private List<NutrientInfo> nutrients = new ArrayList<>();

    /**
     * Description of the portion, such as "1 medium banana".
     */
    private String portionDescription;

    /**
     * The gram weight of the portion.
     */
    private double gramWeight;

    /**
     * List of meal ingredients associated with this food item.
     * This relationship is managed by the MealIngredient entity.
     */
    @OneToMany(mappedBy = "foodItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MealIngredient> mealIngredients = new ArrayList<>();

    /**
     * No-argument constructor required by JPA.
     */
    public FoodItem() {}

    /**
     * Constructor to initialize a FoodItem with a name, FDC ID, portion description, and gram weight.
     *
     * @param name The name of the food item.
     * @param fdcId The FDC ID associated with the food item.
     * @param portionDescription The description of the portion.
     * @param gramWeight The gram weight of the portion.
     */
    public FoodItem(String name, int fdcId, String portionDescription, double gramWeight) {
        this.name = name;
        this.fdcId = fdcId;
        this.portionDescription = portionDescription;
        this.gramWeight = gramWeight;
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
     * Gets the FDC ID of the food item.
     *
     * @return The FDC ID.
     */
    public int getFdcId() {
        return fdcId;
    }

    /**
     * Sets the FDC ID of the food item.
     *
     * @param fdcId The new FDC ID.
     */
    public void setFdcId(int fdcId) {
        this.fdcId = fdcId;
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
     * Gets the description of the portion.
     *
     * @return The description of the portion.
     */
    public String getPortionDescription() {
        return portionDescription;
    }

    /**
     * Sets the description of the portion.
     *
     * @param portionDescription The new description of the portion.
     */
    public void setPortionDescription(String portionDescription) {
        this.portionDescription = portionDescription;
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
     * @param gramWeight The new gram weight of the portion.
     */
    public void setGramWeight(double gramWeight) {
        this.gramWeight = gramWeight;
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

    /**
     * Gets the name of the food item for display purposes.
     * This is used in conjunction with MealIngredient entities.
     *
     * @return The name of the food item.
     */
    public String getDisplayName() {
        return this.name;
    }
}

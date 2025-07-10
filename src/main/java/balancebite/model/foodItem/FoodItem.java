package balancebite.model.foodItem;

import balancebite.model.MealIngredient;
import balancebite.model.NutrientInfo;
import jakarta.persistence.*;

import java.math.BigDecimal;
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
    @Column(nullable = true)
    private String portionDescription;

    /**
     * The gram weight of the portion.
     */
    private double gramWeight;

    /**
     * Optional source indicating where the food item was purchased (e.g., supermarket name).
     */
    private String source;

    /**
     * Optional enum representing a predefined food source.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "food_source", nullable = true)
    private FoodSource foodSource;

    /**
     * List of meal ingredients associated with this food item.
     * This relationship is managed by the MealIngredient entity.
     */
    @OneToMany(mappedBy = "foodItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MealIngredient> mealIngredients = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "food_category", nullable = true)
    private FoodCategory foodCategory;

    @Column
    private String image; // Base64-encoded image

    @Column
    private String imageUrl; // externe of interne URL

    private BigDecimal price;
    private BigDecimal salePrice;
    private BigDecimal salePercentage;
    private String saleDescription;
    private BigDecimal grams;

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

    /**
     * Constructor to initialize a FoodItem with name, FDC ID, portion description, gram weight, and source.
     *
     * @param name The name of the food item.
     * @param fdcId The FDC ID associated with the food item.
     * @param portionDescription The description of the portion.
     * @param gramWeight The gram weight of the portion.
     * @param source The source where the food item was purchased (optional, e.g., supermarket name).
     */
    public FoodItem(String name, int fdcId, String portionDescription, double gramWeight, String source) {
        this.name = name;
        this.fdcId = fdcId;
        this.portionDescription = portionDescription;
        this.gramWeight = gramWeight;
        this.source = source;
    }

    public FoodItem(String name, int fdcId, String portionDescription, double gramWeight, String source, FoodSource foodSource) {
        this.name = name;
        this.fdcId = fdcId;
        this.portionDescription = portionDescription;
        this.gramWeight = gramWeight;
        this.source = source;
        this.foodSource = foodSource;
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

    /**
     * Gets the source of the food item.
     *
     * @return The source (e.g., supermarket name), or null if not specified.
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source of the food item.
     *
     * @param source The new source (e.g., supermarket name).
     */
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getSalePercentage() {
        return salePercentage;
    }

    public void setSalePercentage(BigDecimal salePercentage) {
        this.salePercentage = salePercentage;
    }

    public String getSaleDescription() {
        return saleDescription;
    }

    public void setSaleDescription(String saleDescription) {
        this.saleDescription = saleDescription;
    }

    public BigDecimal getGrams() {
        return grams;
    }

    public void setGrams(BigDecimal grams) {
        this.grams = grams;
    }
}




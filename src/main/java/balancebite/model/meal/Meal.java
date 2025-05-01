package balancebite.model.meal;

import balancebite.config.DurationConverter;
import balancebite.model.MealIngredient;
import balancebite.model.meal.references.Cuisine;
import balancebite.model.meal.references.Diet;
import balancebite.model.meal.references.MealType;
import balancebite.model.user.User;
import balancebite.model.user.userenums.ActivityLevel;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Entity class representing a meal.
 * This class maps to the "meals" table in the database.
 */
@Entity
@Table(name = "meals")
public class Meal {

    private static final String CREATED_BY_USER_ID_COLUMN = "created_by_user_id";
    private static final String ADJUSTED_BY_USER_ID_COLUMN = "adjusted_by_user_id";

    /**
     * Unique identifier for the meal.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the meal.
     */
    private String name;

    /**
     * Description of the meal.
     */
    @Column(length = 1000)
    private String mealDescription;

    /**
     * Base64-encoded image representing the meal.
     * This field is optional and can be used to store an image of the meal.
     */
    private String image;

    /**
     * URL of the image representing the meal.
     * This field is optional and can be used instead of a Base64-encoded image.
     */
    private String imageUrl;

    /**
     * The original identifier of the meal.
     * Used to track updates and ensure the correct versioning of meals.
     */
    private Long originalMealId;

    /**
     * The version timestamp of the meal.
     * This is updated whenever a meal is modified to keep track of changes.
     */
    private LocalDateTime version;

    /**
     * List of meal ingredients associated with the meal.
     * Each ingredient corresponds to a food item with a specified quantity.
     * Using orphanRemoval = true to ensure that all MealIngredient entities
     * are deleted from the database when the associated Meal is removed.
     */
    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MealIngredient> mealIngredients = new ArrayList<>();

    /**
     * Count of users who have added this meal.
     * This field tracks how many unique users have added this meal.
     * Increments each time a user adds the meal and decrements if they remove it.
     */
    @Column(name = "user_count", nullable = false)
    private int userCount = 0;

    /**
     * The user who created this meal.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = CREATED_BY_USER_ID_COLUMN, updatable = true, nullable = true)
    private User createdBy;

    /**
     * The user who has added and potentially adjusted this meal.
     * This field is used to track if a user has created a personalized copy of the meal.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ADJUSTED_BY_USER_ID_COLUMN)
    private User adjustedBy;

    /**
     * Indicates whether this meal is a template (original meal).
     * If true, this meal is a template; if false, it is a user-specific copy.
     */
    @Column(name = "is_template", nullable = false)
    private boolean isTemplate = true;

    /**
     * The types of meal (e.g., breakfast, lunch, dinner, or snack).
     * This allows multiple classifications per meal.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "meal_meal_types", joinColumns = @JoinColumn(name = "meal_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type")
    private Set<MealType> mealTypes = new HashSet<>();

    /**
     * The cuisine types of the meal (e.g., Italian, French, Japanese).
     * Allows associating multiple cuisines per meal.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "meal_cuisines", joinColumns = @JoinColumn(name = "meal_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "cuisine")
    private Set<Cuisine> cuisines = new HashSet<>();

    /**
     * The dietary categories of the meal (e.g., vegetarian, vegan, gluten-free).
     * Allows multiple dietary tags for filtering and preferences.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "meal_diets", joinColumns = @JoinColumn(name = "meal_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "diet")
    private Set<Diet> diets = new HashSet<>();


    /**
     * Stores the total calorie count of the meal.
     * This value is updated whenever meal ingredients change.
     */
    @Column(name = "total_calories", nullable = false)
    private double totalCalories = 0.0;

    /**
     * Stores the total protein content of the meal (grams).
     */
    @Column(name = "total_protein", nullable = false)
    private double totalProtein = 0.0;

    /**
     * Stores the total carbohydrate content of the meal (grams).
     */
    @Column(name = "total_carbs", nullable = false)
    private double totalCarbs = 0.0;

    /**
     * Stores the total fat content of the meal (grams).
     */
    @Column(name = "total_fat", nullable = false)
    private double totalFat = 0.0;

    /**
     * Stores a concatenated string of food item names in the meal.
     * This allows for searching and sorting based on included food items.
     */
    @Column(name = "food_items_string", length = 5000)
    private String foodItemsString = "";

    /**
     * Estimated preparation time for the meal.
     * Allows hour-minute-second precision.
     */
    @Convert(converter = DurationConverter.class)
    @Column(name = "preparation_time")
    private Duration preparationTime;


    /**
     * No-argument constructor required by JPA.
     */
    public Meal() {}

    /**
     * Updates the total nutrient values based on the meal's ingredients.
     * This method should be called whenever meal ingredients change.
     */
    public void updateNutrients() {
        if (mealIngredients == null || mealIngredients.isEmpty()) {
            this.totalCalories = 0.0;
            this.totalProtein = 0.0;
            this.totalCarbs = 0.0;
            this.totalFat = 0.0;
            this.foodItemsString = "";
            return;
        }

        this.totalCalories = mealIngredients.stream()
                .filter(mi -> mi.getFoodItem() != null && mi.getFoodItem().getNutrients() != null)
                .flatMap(mi -> mi.getFoodItem().getNutrients().stream()
                        .filter(n -> "Energy".equalsIgnoreCase(n.getNutrientName()) && n.getValue() != null)
                        .map(n -> n.getValue() * (mi.getQuantity() / 100.0)))
                .mapToDouble(Double::doubleValue)
                .sum();

        this.totalProtein = mealIngredients.stream()
                .filter(mi -> mi.getFoodItem() != null && mi.getFoodItem().getNutrients() != null)
                .flatMap(mi -> mi.getFoodItem().getNutrients().stream()
                        .filter(n -> "Protein".equalsIgnoreCase(n.getNutrientName()) && n.getValue() != null)
                        .map(n -> n.getValue() * (mi.getQuantity() / 100.0)))
                .mapToDouble(Double::doubleValue)
                .sum();

        this.totalCarbs = mealIngredients.stream()
                .filter(mi -> mi.getFoodItem() != null && mi.getFoodItem().getNutrients() != null)
                .flatMap(mi -> mi.getFoodItem().getNutrients().stream()
                        .filter(n -> "Carbohydrates".equalsIgnoreCase(n.getNutrientName()) && n.getValue() != null)
                        .map(n -> n.getValue() * (mi.getQuantity() / 100.0)))
                .mapToDouble(Double::doubleValue)
                .sum();

        this.totalFat = mealIngredients.stream()
                .filter(mi -> mi.getFoodItem() != null && mi.getFoodItem().getNutrients() != null)
                .flatMap(mi -> mi.getFoodItem().getNutrients().stream()
                        .filter(n -> "Total lipid (fat)".equalsIgnoreCase(n.getNutrientName()) && n.getValue() != null)
                        .map(n -> n.getValue() * (mi.getQuantity() / 100.0)))
                .mapToDouble(Double::doubleValue)
                .sum();

        this.foodItemsString = mealIngredients.stream()
                .filter(mi -> mi.getFoodItem() != null)
                .map(mi -> mi.getFoodItem().getName())
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));
    }

    /**
     * Constructor to initialize a Meal with a name and description.
     *
     * @param name the name of the meal.
     * @param mealDescription the description of the meal.
     */
    public Meal(String name, String mealDescription) {
        this.name = name;
        this.mealDescription = mealDescription;
    }

    /**
     * Gets the unique identifier of the meal.
     *
     * @return the ID of the meal.
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the name of the meal.
     *
     * @return the name of the meal.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the meal.
     *
     * @param name the name of the meal.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the description of the meal.
     *
     * @return the description of the meal.
     */
    public String getMealDescription() {
        return mealDescription;
    }

    /**
     * Sets the description of the meal.
     *
     * @param mealDescription the description of the meal.
     */
    public void setMealDescription(String mealDescription) {
        this.mealDescription = mealDescription;
    }

    /**
     * Gets the image of the meal.
     *
     * @return the Base64-encoded image of the meal.
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets the image of the meal.
     *
     * @param image the Base64-encoded image of the meal.
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Gets the ID of the original meal.
     * This field is used to track whether this meal is a copy of another meal.
     * If this meal is an original, the value will be null.
     *
     * @return the ID of the original meal, or null if this meal is an original.
     */
    public Long getOriginalMealId() {
        return originalMealId;
    }

    /**
     * Sets the ID of the original meal.
     * This should be set when creating a copy of an existing meal.
     *
     * @param originalMealId the ID of the original meal.
     */
    public void setOriginalMealId(Long originalMealId) {
        this.originalMealId = originalMealId;
    }

    /**
     * Gets the version timestamp of the meal.
     * This represents the last modification time of the meal.
     * It is used to determine whether an update is available.
     *
     * @return the LocalDateTime representing the last modification time.
     */
    public LocalDateTime getVersion() {
        return version;
    }

    /**
     * Sets the version timestamp of the meal.
     * This should be updated whenever the meal is modified.
     *
     * @param version the new LocalDateTime representing the last modification time.
     */
    public void setVersion(LocalDateTime version) {
        this.version = version;
    }

    /**
     * Gets the imageUrl of the meal.
     *
     * @return the imageUrl for the image of the meal.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the image url of the meal.
     *
     * @param imageUrl for the image of the meal.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the list of ingredients associated with the meal.
     *
     * @return the list of meal ingredients.
     */
    public List<MealIngredient> getMealIngredients() {
        return mealIngredients;
    }

    /**
     * Adds a meal ingredient to the list and sets the relationship.
     * This method ensures that the bidirectional relationship between Meal and MealIngredient is maintained.
     * It adds the given MealIngredient to the list and sets the "meal" property of the MealIngredient to this Meal.
     *
     * @param mealIngredient the meal ingredient to add.
     */
    public void addMealIngredient(MealIngredient mealIngredient) {
        mealIngredients.add(mealIngredient);
        mealIngredient.setMeal(this);
    }

    /**
     * Adds a list of meal ingredients to the meal and sets the relationship.
     * This method iterates through the provided list of MealIngredients and uses the addMealIngredient method
     * to ensure that each ingredient is correctly associated with this meal.
     *
     * @param mealIngredients the list of meal ingredients to add.
     */
    public void addMealIngredients(List<MealIngredient> mealIngredients) {
        for (MealIngredient mealIngredient : mealIngredients) {
            addMealIngredient(mealIngredient);
        }
    }

    /**
     * Gets the user count for the meal.
     *
     * @return the count of users who have added this meal.
     */
    public int getUserCount() {
        return userCount;
    }

    /**
     * Increments the user count for this meal by 1, tracking users who add this meal.
     */
    public void incrementUserCount() {
        this.userCount++;
    }

    /**
     * Decrements the user count for this meal by 1, ensuring it does not go below 0.
     * This is used to track users removing the meal.
     */
    public void decrementUserCount() {
        if (this.userCount > 0) {
            this.userCount--;
        }
    }

    /**
     * Gets the user who created this meal.
     *
     * @return the user who created this meal.
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the user who created this meal.
     *
     * @param createdBy the user who created the meal.
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the user who added and potentially adjusted this meal.
     *
     * @return the user who adjusted this meal.
     */
    public User getAdjustedBy() {
        return adjustedBy;
    }

    /**
     * Sets the user who added and potentially adjusted this meal.
     * This allows tracking of user-specific copies of the original meal.
     *
     * @param adjustedBy the user who adjusted the meal.
     */
    public void setAdjustedBy(User adjustedBy) {
        this.adjustedBy = adjustedBy;
    }

    /**
     * Checks if this meal is a template (original).
     *
     * @return true if this meal is a template, false if it is a user-specific copy.
     */
    public boolean isTemplate() {
        return isTemplate;
    }

    /**
     * Sets whether this meal is a template.
     *
     * @param isTemplate true if this meal is a template, false if it is a user-specific copy.
     */
    public void setIsTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    /**
     * Gets the dietary categories of the meal.
     *
     * @return the set of diet types of the meal.
     */
    public Set<Diet> getDiets() {
        return diets;
    }

    /**
     * Sets the dietary categories of the meal.
     *
     * @param diets the set of diet types to set for the meal.
     */
    public void setDiets(Set<Diet> diets) {
        this.diets = diets;
    }

    /**
     * Gets the types of the meal (e.g., breakfast, lunch, dinner, or snack).
     *
     * @return the set of meal types.
     */
    public Set<MealType> getMealTypes() {
        return mealTypes;
    }

    /**
     * Sets the types of the meal (e.g., breakfast, lunch, dinner, or snack).
     *
     * @param mealTypes the set of meal types to set.
     */
    public void setMealTypes(Set<MealType> mealTypes) {
        this.mealTypes = mealTypes;
    }

    /**
     * Gets the cuisine types of the meal (e.g., Italian, French, Japanese).
     *
     * @return the set of cuisine types of the meal.
     */
    public Set<Cuisine> getCuisines() {
        return cuisines;
    }

    /**
     * Sets the cuisine types of the meal (e.g., Italian, French, Japanese).
     *
     * @param cuisines the set of cuisine types to set.
     */
    public void setCuisines(Set<Cuisine> cuisines) {
        this.cuisines = cuisines;
    }


    public double getTotalCalories() { return totalCalories; }

    public double getTotalProtein() { return totalProtein; }

    public double getTotalCarbs() { return totalCarbs; }

    public double getTotalFat() { return totalFat; }

    public String getFoodItemsString() { return foodItemsString; }

    public Duration getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(Duration preparationTime) {
        this.preparationTime = preparationTime;
    }
}

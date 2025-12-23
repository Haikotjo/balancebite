package balancebite.model.meal;

import balancebite.config.DurationConverter;
import balancebite.model.MealIngredient;
import balancebite.model.meal.mealImage.MealImage;
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
     * Images associated with this meal.
     * Stored separately and removed automatically when the meal is deleted.
     */
    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MealImage> images = new ArrayList<>();

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

    @Column(nullable = true)
    private boolean isPrivate = false;

    @Column(name = "isRestricted")
    private boolean isRestricted = false;

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

    private Double totalSugars;
    private Double totalSaturatedFat;
    private Double totalUnsaturatedFat;


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

    @ManyToMany(mappedBy = "savedMeals")
    private Set<User> users = new HashSet<>();

    @Column(name = "save_count")
    private Long saveCount = 0L;

    @Column(name = "weekly_save_count")
    private Long weeklySaveCount = 0L;

    @Column(name = "monthly_save_count")
    private Long monthlySaveCount = 0L;

    public Long getSaveCount() {
        return saveCount;
    }

    /**
     * External video URL for this meal (we only store the link).
     */
    @Column(name = "video_url", length = 2048)
    // Optional: validate URL format (Hibernate Validator)
    // @org.hibernate.validator.constraints.URL(regexp = "https?://.*", message = "Must be a valid HTTP/HTTPS URL")
    private String videoUrl;

    /**
     * Source URL pointing to the original meal page (e.g., Allerhande).
     */
    @Column(name = "source_url", length = 2048)
    // @org.hibernate.validator.constraints.URL(regexp = "https?://.*", message = "Must be a valid HTTP/HTTPS URL")
    private String sourceUrl;

    @Lob
    @Column(name = "meal_preparation") // CLOB/TEXT
    private String mealPreparation;

    /** External video specifically showing the preparation steps. */
    @Column(name = "preparation_video_url", length = 2048)
    private String preparationVideoUrl;

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
            this.totalSugars = 0.0;
            this.totalSaturatedFat = 0.0;
            this.totalUnsaturatedFat = 0.0;
            this.foodItemsString = "";
            return;
        }

        this.totalCalories = getTotalByName("Energy");
        this.totalProtein = getTotalByName("Protein");
        this.totalCarbs = getTotalByName("Carbohydrates");
        this.totalFat = getTotalByName("Total lipid (fat)");

        List<String> sugarNames = List.of("Total Sugars", "Sugars, total");
        List<String> saturatedFatNames = List.of("Fatty acids, total saturated");
        List<String> unsaturatedFatNames = List.of(
                "Fatty acids, total monounsaturated",
                "Fatty acids, total polyunsaturated",
                "Fatty acids, total unsaturated"
        );

        this.totalSugars = getTotalByNames(sugarNames);
        this.totalSaturatedFat = getTotalByNames(saturatedFatNames);
        this.totalUnsaturatedFat = getTotalByNames(unsaturatedFatNames);

        this.foodItemsString = mealIngredients.stream()
                .filter(mi -> mi.getFoodItem() != null)
                .map(mi -> mi.getFoodItem().getName())
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));
    }

    private double getTotalByName(String name) {
        return mealIngredients.stream()
                .filter(mi -> mi.getFoodItem() != null && mi.getFoodItem().getNutrients() != null)
                .flatMap(mi -> mi.getFoodItem().getNutrients().stream()
                        .filter(n -> name.equalsIgnoreCase(n.getNutrientName()) && n.getValue() != null)
                        .map(n -> n.getValue() * (mi.getQuantity() / 100.0)))
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private double getTotalByNames(List<String> names) {
        return mealIngredients.stream()
                .filter(mi -> mi.getFoodItem() != null && mi.getFoodItem().getNutrients() != null)
                .flatMap(mi -> mi.getFoodItem().getNutrients().stream()
                        .filter(n -> names.stream().anyMatch(name -> name.equalsIgnoreCase(n.getNutrientName()))
                                && n.getValue() != null)
                        .map(n -> n.getValue() * (mi.getQuantity() / 100.0)))
                .mapToDouble(Double::doubleValue)
                .sum();
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
     * Adds an image to this meal and maintains the bidirectional relationship.
     *
     * @param image the meal image to add
     */
    public void addImage(MealImage image) {
        if (image == null) return;
        images.add(image);
        image.setMeal(this);
    }

    /**
     * Removes an image from this meal and maintains the bidirectional relationship.
     *
     * @param image the meal image to remove
     */
    public void removeImage(MealImage image) {
        images.remove(image);
        image.setMeal(null);
    }


    /**
     * Returns the images associated with this meal.
     *
     * @return list of meal images
     */
    public List<MealImage> getImages() {
        return images;
    }

    /**
     * Sets the images for this meal.
     *
     * @param images list of meal images
     */
    public void setImages(List<MealImage> images) {
        this.images = images;
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

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public boolean isRestricted() {
        return isRestricted;
    }

    public void setRestricted(boolean restricted) {
        isRestricted = restricted;
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

    public Double getTotalSugars() {
        return totalSugars;
    }

    public Double getTotalSaturatedFat() {
        return totalSaturatedFat;
    }

    public Double getTotalUnsaturatedFat() {
        return totalUnsaturatedFat;
    }

    public String getFoodItemsString() { return foodItemsString; }

    public Duration getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(Duration preparationTime) {
        this.preparationTime = preparationTime;
    }

    public Long getMonthlySaveCount() {
        return monthlySaveCount;
    }

    public void setMonthlySaveCount(Long monthlySaveCount) {
        this.monthlySaveCount = monthlySaveCount;
    }

    public Long getWeeklySaveCount() {
        return weeklySaveCount;
    }

    public void setWeeklySaveCount(Long weeklySaveCount) {
        this.weeklySaveCount = weeklySaveCount;
    }

    public void setSaveCount(Long saveCount) {
        this.saveCount = saveCount;
    }

    // equals/hashCode on id so Set.remove() works with proxies
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Meal m)) return false;
        return id != null && id.equals(m.id);
    }

    @Override
    public int hashCode() { return 31; }

    /** Returns the external video URL (if any). */
    public String getVideoUrl() { return videoUrl; }

    /** Sets the external video URL. */
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    /** Returns the original source URL (if any). */
    public String getSourceUrl() { return sourceUrl; }

    /** Sets the original source URL. */
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getMealPreparation() { return mealPreparation; }
    public void setMealPreparation(String mealPreparation) { this.mealPreparation = mealPreparation; }
    public String getPreparationVideoUrl() { return preparationVideoUrl; }
    public void setPreparationVideoUrl(String preparationVideoUrl) { this.preparationVideoUrl = preparationVideoUrl; }

}

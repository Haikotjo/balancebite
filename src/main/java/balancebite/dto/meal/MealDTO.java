package balancebite.dto.meal;

import balancebite.dto.mealingredient.MealIngredientDTO;
import balancebite.dto.user.PublicUserDTO;
import balancebite.dto.user.UserDTO;
import balancebite.model.meal.references.Cuisine;
import balancebite.model.meal.references.Diet;
import balancebite.model.meal.references.MealType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Data Transfer Object (DTO) for transferring Meal data between layers of the application.
 * This class contains the essential fields required for the response when a meal is created
 * or retrieved.
 */
public class MealDTO {

    /**
     * Unique identifier of the meal.
     */
    private final Long id;

    /**
     * Name of the meal.
     */
    private final String name;

    /**
     * Description of the meal.
     */
    private final String mealDescription;

    /**
     * Base64-encoded image representing the meal.
     * This field is optional.
     */
    private final String image;

    /**
     * URL of the image representing the meal.
     * This field is optional.
     */
    private final String imageUrl;

    private final Long originalMealId;

    /**
     * The version timestamp of the meal.
     * This is updated whenever a meal is modified to keep track of changes.
     * It helps the frontend determine if an update is available.
     */
    private final LocalDateTime version;

    /**
     * List of ingredients associated with the meal.
     */
    private final List<MealIngredientDTO> mealIngredients;

    /**
     * The user who originally created the meal.
     */
    private final PublicUserDTO createdBy;

    /**
     * The user who added and potentially adjusted the meal.
     * This field allows identification of customized meal copies.
     */
    private final PublicUserDTO adjustedBy;

    private final boolean isTemplate;

    private final boolean isPrivate;

    private final boolean isRestricted;

    /**
     * The types of the meal (e.g., breakfast, lunch, dinner, or snack).
     * Allows multiple classifications based on the time of day or purpose.
     */
    private final Set<MealType> mealTypes;

    /**
     * The dietary categories of the meal (e.g., vegetarian, vegan, gluten-free).
     * Used for filtering meals based on dietary restrictions or preferences.
     */
    private final Set<Diet> diets;

    /**
     * The cuisine types of the meal (e.g., Italian, French, Japanese).
     * Represents the cultural or regional origins of the meal.
     */
    private final Set<Cuisine> cuisines;


    /**
     * Total calculated calories of the meal.
     */
    private final double totalCalories;

    /**
     * Total calculated protein content of the meal (grams).
     */
    private final double totalProtein;

    /**
     * Total calculated carbohydrate content of the meal (grams).
     */
    private final double totalCarbs;

    /**
     * Total calculated fat content of the meal (grams).
     */
    private final double totalFat;

    private final double totalSugars;
    private final double totalSaturatedFat;
    private final double totalUnsaturatedFat;


    /**
     * A concatenated string of food item names in the meal.
     * This allows for searching and sorting based on included food items.
     */
    private final String foodItemsString;

    /**
     * Estimated preparation time for the meal (e.g., "PT30M", "PT1H").
     * Follows ISO-8601 duration format.
     */
    private final String preparationTime;

    private final long saveCount;
    private final long weeklySaveCount;
    private final long monthlySaveCount;

    /**
     * Constructor for creating a MealDTO with essential meal information.
     *
     * @param id               the unique identifier of the meal.
     * @param name             the name of the meal.
     * @param mealDescription  the description of the meal.
     * @param image            the Base64-encoded image of the meal (optional).
     * @param imageUrl         the URL of the meal image (optional).
     * @param originalMealId
     * @param version          the version timestamp of the meal, used to track updates.
     * @param mealIngredients  the list of ingredients in the meal.
     * @param createdBy        the user who originally created the meal.
     * @param adjustedBy       the user who adjusted the meal (if applicable).
     * @param isTemplate
     * @param isPrivate
     * @param isRestricted
     * @param mealTypes         the types of meal (optional). Allows classification as breakfast, lunch, dinner, or snack.
     * @param cuisines          the cuisine types of the meal (optional). Represents cultural or regional origins.
     * @param diets             the dietary categories of the meal (optional). Used for filtering based on dietary preferences.
     * @param totalCalories    the total calculated calorie count of the meal.
     * @param totalProtein     the total calculated protein content of the meal (grams).
     * @param totalCarbs       the total calculated carbohydrate content of the meal (grams).
     * @param totalFat         the total calculated fat content of the meal (grams).
     * @param totalSugars          the total calculated sugar content of the meal (grams).
     * @param totalSaturatedFat    the total calculated saturated fat content of the meal (grams).
     * @param totalUnsaturatedFat  the total calculated unsaturated fat content of the meal (grams).
     * @param foodItemsString  the concatenated string of food items in the meal.
     */
    public MealDTO(Long id, String name, String mealDescription, String image, String imageUrl, Long originalMealId,
                   LocalDateTime version, List<MealIngredientDTO> mealIngredients, PublicUserDTO createdBy,
                   PublicUserDTO adjustedBy ,boolean  isTemplate, boolean  isPrivate, boolean  isRestricted, Set<MealType> mealTypes, Set<Cuisine> cuisines, Set<Diet> diets, double totalCalories, double totalProtein, double totalCarbs, double totalSugars, double totalSaturatedFat, double totalUnsaturatedFat,
                   double totalFat, String foodItemsString, String preparationTime, long saveCount, long weeklySaveCount, long monthlySaveCount ) {
        this.id = id;
        this.name = name;
        this.mealDescription = mealDescription;
        this.image = image;
        this.imageUrl = imageUrl;
        this.originalMealId = originalMealId;
        this.version = version;
        this.mealIngredients = (mealIngredients != null) ? List.copyOf(mealIngredients) : List.of(); // Use an unmodifiable list
        this.createdBy = createdBy;
        this.adjustedBy = adjustedBy;
        this.isTemplate = isTemplate;
        this.isPrivate = isPrivate;
        this.isRestricted = isRestricted;
        this.mealTypes = mealTypes != null ? Set.copyOf(mealTypes) : Set.of();
        this.cuisines = cuisines != null ? Set.copyOf(cuisines) : Set.of();
        this.diets = diets != null ? Set.copyOf(diets) : Set.of();
        this.totalCalories = totalCalories;
        this.totalProtein = totalProtein;
        this.totalCarbs = totalCarbs;
        this.totalFat = totalFat;
        this.totalSugars = totalSugars;
        this.totalSaturatedFat = totalSaturatedFat;
        this.totalUnsaturatedFat = totalUnsaturatedFat;
        this.foodItemsString = foodItemsString;
        this.preparationTime = preparationTime;
        this.saveCount = saveCount;
        this.weeklySaveCount = weeklySaveCount;
        this.monthlySaveCount = monthlySaveCount;
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
     * Gets the description of the meal.
     *
     * @return the description of the meal.
     */
    public String getMealDescription() {
        return mealDescription;
    }

    /**
     * Gets the Base64-encoded image of the meal.
     *
     * @return the Base64-encoded image of the meal.
     */
    public String getImage() {
        return image;
    }

    /**
     * Gets the URL of the image representing the meal.
     *
     * @return the URL of the image.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    public Long getOriginalMealId() { return originalMealId; }

    /**
     * Gets the version timestamp of the meal.
     * This represents the last modification time of the meal.
     *
     * @return the LocalDateTime representing the last modification time.
     */
    public LocalDateTime getVersion() {
        return version;
    }

    /**
     * Gets the list of ingredients associated with the meal.
     * Returns an unmodifiable copy to maintain immutability.
     *
     * @return an unmodifiable list of meal ingredients.
     */
    public List<MealIngredientDTO> getMealIngredients() {
        return List.copyOf(mealIngredients); // Ensure the list cannot be mutated outside this DTO
    }

    /**
     * Gets the user who originally created this meal.
     *
     * @return the creator of the meal.
     */
    public PublicUserDTO getCreatedBy() {
        return createdBy;
    }
    /**
     * Gets the user who added and potentially adjusted this meal.
     *
     * @return the user who adjusted the meal.
     */
    public PublicUserDTO getAdjustedBy() {
        return adjustedBy;
    }

    public boolean getIsTemplate() {return isTemplate;}

    public boolean getIsPrivate() {return isPrivate;}

    public boolean getIsRestricted() {return isRestricted;}

    /**
     * Gets the types of the meal (e.g., breakfast, lunch, dinner, or snack).
     * Allows multiple classifications based on the time of day or purpose.
     *
     * @return a set of meal types.
     */
    public Set<MealType> getMealTypes() {
        return Set.copyOf(mealTypes);
    }

    /**
     * Gets the cuisine types of the meal.
     * Represents the cultural or regional origins of the meal (e.g., Italian, French, Japanese).
     *
     * @return a set of cuisine types.
     */
    public Set<Cuisine> getCuisines() {
        return Set.copyOf(cuisines);
    }

    /**
     * Gets the dietary categories of the meal.
     * Used for filtering meals based on dietary restrictions or preferences (e.g., vegetarian, vegan, gluten-free).
     *
     * @return a set of dietary categories.
     */
    public Set<Diet> getDiets() {
        return Set.copyOf(diets);
    }


    /**
     * Gets the total calculated calories of the meal.
     *
     * @return the total calories.
     */
    public double getTotalCalories() {
        return totalCalories;
    }

    /**
     * Gets the total calculated protein content of the meal (grams).
     *
     * @return the total protein.
     */
    public double getTotalProtein() {
        return totalProtein;
    }

    /**
     * Gets the total calculated carbohydrate content of the meal (grams).
     *
     * @return the total carbohydrates.
     */
    public double getTotalCarbs() {
        return totalCarbs;
    }

    /**
     * Gets the total calculated fat content of the meal (grams).
     *
     * @return the total fat.
     */
    public double getTotalFat() {
        return totalFat;
    }

    public double getTotalSugars() {
        return totalSugars;
    }

    public double getTotalSaturatedFat() {
        return totalSaturatedFat;
    }

    public double getTotalUnsaturatedFat() {
        return totalUnsaturatedFat;
    }

    /**
     * Gets the concatenated string of food items in the meal.
     *
     * @return the food items string.
     */
    public String getFoodItemsString() {
        return foodItemsString;
    }

    /**
     * Gets the estimated preparation time in ISO-8601 format (e.g., "PT30M").
     *
     * @return the preparation time as a string.
     */
    public String getPreparationTime() {
        return preparationTime;
    }

    public long getSaveCount() {
        return saveCount;
    }

    public long getWeeklySaveCount() {
        return weeklySaveCount;
    }

    public long getMonthlySaveCount() {
        return monthlySaveCount;
    }
}

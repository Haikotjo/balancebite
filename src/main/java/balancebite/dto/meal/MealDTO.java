package balancebite.dto.meal;

import balancebite.dto.mealingredient.MealIngredientDTO;
import balancebite.dto.user.UserDTO;
import balancebite.model.meal.references.Cuisine;
import balancebite.model.meal.references.Diet;
import balancebite.model.meal.references.MealType;

import java.time.LocalDateTime;
import java.util.List;

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
     * Count of users who have added this meal.
     */
    private final int userCount;

    /**
     * The user who originally created the meal.
     */
    private final UserDTO createdBy;

    /**
     * The user who added and potentially adjusted the meal.
     * This field allows identification of customized meal copies.
     */
    private final UserDTO adjustedBy;

    private final boolean isTemplate;

    /**
     * The type of meal (e.g., breakfast, lunch, dinner, or snack).
     * This enum helps categorize meals based on the time of day or purpose.
     */
    private final MealType mealType;

    /**
     * The cuisine type of the meal (e.g., Italian, French, Japanese).
     * This enum represents the cultural or regional origin of the meal.
     */
    private final Cuisine cuisine;

    /**
     * The dietary category of the meal (e.g., vegetarian, vegan, gluten-free).
     * This enum helps users filter meals based on dietary restrictions or preferences.
     */
    private final Diet diet;

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

    /**
     * A concatenated string of food item names in the meal.
     * This allows for searching and sorting based on included food items.
     */
    private final String foodItemsString;

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
     * @param userCount        the count of users who have added the meal.
     * @param createdBy        the user who originally created the meal.
     * @param adjustedBy       the user who adjusted the meal (if applicable).
     * @param isTemplate
     * @param mealType         the type of meal (optional). Defines if the meal is breakfast, lunch, dinner, or snack.
     * @param cuisine          the cuisine type of the meal (optional). Represents the cultural or regional origin.
     * @param diet             the dietary category of the meal (optional). Used for filtering meals based on diet.
     * @param totalCalories    the total calculated calorie count of the meal.
     * @param totalProtein     the total calculated protein content of the meal (grams).
     * @param totalCarbs       the total calculated carbohydrate content of the meal (grams).
     * @param totalFat         the total calculated fat content of the meal (grams).
     * @param foodItemsString  the concatenated string of food items in the meal.
     */
    public MealDTO(Long id, String name, String mealDescription, String image, String imageUrl, Long originalMealId,
                   LocalDateTime version, List<MealIngredientDTO> mealIngredients, int userCount, UserDTO createdBy,
                   UserDTO adjustedBy,Boolean isTemplate , MealType mealType, Cuisine cuisine, Diet diet,
                   double totalCalories, double totalProtein, double totalCarbs,
                   double totalFat, String foodItemsString) {
        this.id = id;
        this.name = name;
        this.mealDescription = mealDescription;
        this.image = image;
        this.imageUrl = imageUrl;
        this.originalMealId = originalMealId;
        this.version = version;
        this.mealIngredients = (mealIngredients != null) ? List.copyOf(mealIngredients) : List.of(); // Use an unmodifiable list
        this.userCount = userCount;
        this.createdBy = createdBy;
        this.adjustedBy = adjustedBy;
        this.isTemplate = isTemplate;
        this.mealType = mealType;
        this.cuisine = cuisine;
        this.diet = diet;
        this.totalCalories = totalCalories;
        this.totalProtein = totalProtein;
        this.totalCarbs = totalCarbs;
        this.totalFat = totalFat;
        this.foodItemsString = foodItemsString;
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
     * Gets the count of users associated with the meal.
     *
     * @return the user count.
     */
    public int getUserCount() {
        return userCount;
    }

    /**
     * Gets the user who originally created this meal.
     *
     * @return the creator of the meal.
     */
    public UserDTO getCreatedBy() {
        return createdBy;
    }

    /**
     * Gets the user who added and potentially adjusted this meal.
     *
     * @return the user who adjusted the meal.
     */
    public UserDTO getAdjustedBy() {
        return adjustedBy;
    }

    public boolean getIsTemplate() {return isTemplate;}

    /**
     * Gets the type of meal (e.g., breakfast, lunch, dinner, or snack).
     * This enum helps categorize meals based on the time of day or purpose.
     *
     * @return the meal type.
     */
    public MealType getMealType() {
        return mealType;
    }

    /**
     * Gets the cuisine type of the meal.
     * Represents the cultural or regional origin of the meal (e.g., Italian, French, Japanese).
     *
     * @return the cuisine type of the meal.
     */
    public Cuisine getCuisine() {
        return cuisine;
    }

    /**
     * Gets the dietary category of the meal.
     * Used for filtering meals based on dietary restrictions or preferences (e.g., vegetarian, vegan, gluten-free).
     *
     * @return the diet type of the meal.
     */
    public Diet getDiet() {
        return diet;
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

    /**
     * Gets the concatenated string of food items in the meal.
     *
     * @return the food items string.
     */
    public String getFoodItemsString() {
        return foodItemsString;
    }
}

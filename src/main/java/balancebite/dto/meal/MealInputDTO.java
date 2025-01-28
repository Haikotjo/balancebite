package balancebite.dto.meal;

import balancebite.dto.mealingredient.MealIngredientInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.model.meal.references.Cuisine;
import balancebite.model.meal.references.Diet;
import balancebite.model.meal.references.MealType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Data Transfer Object (DTO) for capturing input data related to a Meal.
 * This class is used for receiving data from the client-side when creating or updating
 * a Meal in the application.
 */
public class MealInputDTO {

    /**
     * The name of the meal. This field is mandatory and cannot be blank.
     */
    @NotBlank(message = "The name of the meal cannot be blank. Please provide a valid name.")
    @Size(max = 100, message = "The name of the meal must not exceed 100 characters.")
    private String name;

    /**
     * List of ingredients that make up the meal. This field is mandatory and
     * requires at least one ingredient.
     */
    @NotEmpty(message = "The meal must contain at least one ingredient. Please provide ingredients.")
    @Valid
    private List<MealIngredientInputDTO> mealIngredients;

    /**
     * Optional description of the meal. The description length is limited to 1000 characters.
     */
    @Size(max = 1000, message = "The meal description must not exceed 1000 characters.")
    private String mealDescription;

    /**
     * Base64-encoded image representing the meal.
     * Optional field for meal creation or update.
     */
    @Size(max = 500000, message = "Image size must not exceed 500 KB.")
    private String image;

    /**
     * URL of the image representing the meal.
     * Optional field for meal creation or update.
     */
    @Size(max = 500, message = "Image URL must not exceed 500 characters.")
    private String imageUrl;

    /**
     * MultipartFile for handling direct file uploads of the meal's image.
     * This field is optional.
     */
    private MultipartFile imageFile;

    /**
     * The user who created this meal (if applicable).
     * This field is managed by the system and should not be set by the client directly.
     */
    @Valid
    private UserDTO createdBy;

    /**
     * The type of meal (e.g., breakfast, lunch, dinner, or snack).
     * This field is optional.
     */
    private MealType mealType;

    /**
     * The cuisine type of the meal (e.g., Italian, French, Japanese).
     * This field is optional.
     */
    private Cuisine cuisine;

    /**
     * The dietary category of the meal (e.g., vegetarian, vegan, gluten-free).
     * This field is optional.
     */
    private Diet diet;

    /**
     * Default constructor for frameworks that require a no-argument constructor.
     */
    public MealInputDTO() {}

    /**
     * Constructor for creating a MealInputDTO with basic meal information.
     *
     * @param name            The name of the meal. Must not be blank and must not exceed 100 characters.
     * @param mealIngredients The list of ingredients that make up the meal. Must not be empty.
     * @param mealDescription The description of the meal (optional). Must not exceed 1000 characters.
     * @param image           Base64-encoded image of the meal (optional). Maximum size: 500 KB.
     * @param imageUrl        URL of the image representing the meal (optional). Maximum length: 500 characters.
     * @param imageFile       MultipartFile for direct file uploads of the meal's image (optional).
     * @param createdBy       The user who created the meal (optional). Managed by the system.
     * @param mealType        The type of meal (optional). Defines if the meal is breakfast, lunch, dinner, or snack.
     * @param cuisine         The cuisine type of the meal (optional). Represents the cultural or regional origin.
     * @param diet            The dietary category of the meal (optional). Used for filtering meals based on diet.
     */
    public MealInputDTO(
            @NotBlank(message = "The name of the meal cannot be blank. Please provide a valid name.")
            @Size(max = 100, message = "The name of the meal must not exceed 100 characters.") String name,
            @NotEmpty(message = "The meal must contain at least one ingredient. Please provide ingredients.")
            @Valid List<MealIngredientInputDTO> mealIngredients,
            @Size(max = 1000, message = "The meal description must not exceed 1000 characters.") String mealDescription,
            @Size(max = 500000, message = "Image size must not exceed 500 KB.") String image,
            @Size(max = 500, message = "Image URL must not exceed 500 characters.") String imageUrl,
            MultipartFile imageFile,
            @Valid UserDTO createdBy,
            MealType mealType,
            Cuisine cuisine,
            Diet diet) {
        this.name = name;
        this.mealIngredients = mealIngredients;
        this.mealDescription = mealDescription;
        this.image = image;
        this.imageUrl = imageUrl;
        this.imageFile = imageFile;
        this.createdBy = createdBy;
        this.mealType = mealType;
        this.cuisine = cuisine;
        this.diet = diet;
    }

    /**
     * Gets the name of the meal.
     *
     * @return The name of the meal.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the meal.
     *
     * @param name The name of the meal. Must not be blank and must not exceed 100 characters.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of ingredients that make up the meal.
     *
     * @return The list of meal ingredients.
     */
    public List<MealIngredientInputDTO> getMealIngredients() {
        return mealIngredients;
    }

    /**
     * Sets the list of ingredients that make up the meal.
     *
     * @param mealIngredients The list of ingredients to set. Must not be empty.
     */
    public void setMealIngredients(List<MealIngredientInputDTO> mealIngredients) {
        this.mealIngredients = mealIngredients;
    }

    /**
     * Gets the description of the meal.
     *
     * @return The description of the meal.
     */
    public String getMealDescription() {
        return mealDescription;
    }

    /**
     * Sets the description of the meal.
     *
     * @param mealDescription The description of the meal. Must not exceed 1000 characters.
     */
    public void setMealDescription(String mealDescription) {
        this.mealDescription = mealDescription;
    }

    /**
     * Gets the Base64-encoded image of the meal.
     *
     * @return The Base64-encoded image of the meal.
     */
    public String getImage() {
        return image;
    }

    /**
     * Sets the Base64-encoded image of the meal.
     *
     * @param image The Base64-encoded image of the meal.
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Gets the URL of the image representing the meal.
     *
     * @return The URL of the image.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the URL of the image representing the meal.
     *
     * @param imageUrl The URL of the image.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the uploaded file of the image.
     *
     * @return The MultipartFile containing the uploaded image.
     */
    public MultipartFile getImageFile() {
        return imageFile;
    }

    /**
     * Sets the uploaded file of the image.
     *
     * @param imageFile The MultipartFile containing the uploaded image.
     */
    public void setImageFile(MultipartFile imageFile) {
        this.imageFile = imageFile;
    }

    /**
     * Gets the user who created this meal, if applicable.
     *
     * @return The user who created the meal.
     */
    public UserDTO getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the user who created this meal.
     * Note: This field is typically managed by the system.
     *
     * @param createdBy The user who created the meal.
     */
    public void setCreatedBy(UserDTO createdBy) {
        this.createdBy = createdBy;
    }

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
     * Sets the type of meal (e.g., breakfast, lunch, dinner, or snack).
     * This allows categorization of meals based on their intended meal time.
     *
     * @param mealType the meal type to set for the meal.
     */
    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }


    /**
     * Gets the cuisine type of the meal.
     * Represents the cultural or regional origin of the meal (e.g., Italian, French, Japanese).
     *
     * @return The cuisine type of the meal.
     */
    public Cuisine getCuisine() {
        return cuisine;
    }

    /**
     * Sets the cuisine type of the meal.
     * This defines the cultural or regional origin of the meal.
     *
     * @param cuisine The cuisine type to set for the meal.
     */
    public void setCuisine(Cuisine cuisine) {
        this.cuisine = cuisine;
    }

    /**
     * Gets the dietary category of the meal.
     * Used for filtering meals based on dietary restrictions or preferences (e.g., vegetarian, vegan, gluten-free).
     *
     * @return The diet type of the meal.
     */
    public Diet getDiet() {
        return diet;
    }

    /**
     * Sets the dietary category of the meal.
     * This allows meals to be categorized based on dietary restrictions or preferences.
     *
     * @param diet The diet type to set for the meal.
     */
    public void setDiet(Diet diet) {
        this.diet = diet;
    }
}

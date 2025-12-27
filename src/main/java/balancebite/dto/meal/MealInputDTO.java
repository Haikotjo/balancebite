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
import org.hibernate.validator.constraints.URL;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

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
     * MultipartFiles for handling direct file uploads of the meal's images.
     * This field is optional.
     */
    private List<MultipartFile> imageFiles;

    /**
     * Index of the primary image in imageFiles (0-based).
     * Optional. If null, the first image will be primary.
     */
    private Integer primaryIndex;

    /**
     * IDs of existing MealImages that should be kept during update.
     * Any existing image NOT in this list will be deleted.
     * Optional. Used only for update.
     */
    private List<Long> keepImageIds;

    /**
     * For each uploaded imageFile: the orderIndex (slot 0–4) it should replace.
     * Must have same size/order as imageFiles.
     * Optional. Used only for update.
     */
    private List<Integer> replaceOrderIndexes;

    private Long primaryImageId;

    /**
     * The user who created this meal (if applicable).
     * This field is managed by the system and should not be set by the client directly.
     */
    @Valid
    private UserDTO createdBy;

    private boolean isPrivate = false;

    private boolean isRestricted;

    /**
     * The types of the meal (e.g., breakfast, lunch, dinner, or snack).
     * This field is optional and allows selecting multiple meal types.
     */
    private Set<MealType> mealTypes;

    /**
     * The cuisine types of the meal (e.g., Italian, French, Japanese).
     * This field is optional and allows selecting multiple cuisine types.
     */
    private Set<Cuisine> cuisines;

    /**
     * The dietary categories of the meal (e.g., vegetarian, vegan, gluten-free).
     * This field is optional and allows selecting multiple diet categories.
     */
    private Set<Diet> diets;

    /**
     * Estimated preparation time as a duration string (e.g., "PT20M", "PT1H30M", or "00:45:00").
     * Optional field. Expected format: ISO-8601 or "HH:mm:ss" for user input.
     */
    private String preparationTime;

    @Size(max = 2048, message = "Video URL must not exceed 2048 characters.")

    @URL(regexp = "https?://.*", message = "Must be a valid HTTP/HTTPS URL")
    private String videoUrl;

    @Size(max = 2048, message = "Source URL must not exceed 2048 characters.")
    @URL(regexp = "https?://.*", message = "Must be a valid HTTP/HTTPS URL")
    private String sourceUrl;

    @Size(max = 2048, message = "Preparation video URL must not exceed 2048 characters.")
    @URL(regexp = "https?://.*", message = "Must be a valid HTTP/HTTPS URL")
    private String preparationVideoUrl;

    /** Optional long preparation instructions (free text). */
    @Size(max = 20000, message = "Preparation text is too long.")
    private String mealPreparation;


    // Constructor, getters, and setters

    /**
     * Default constructor for frameworks that require a no-argument constructor.
     */
    public MealInputDTO() {}

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

    public List<MultipartFile> getImageFiles() { return imageFiles; }
    public void setImageFiles(List<MultipartFile> imageFiles) { this.imageFiles = imageFiles; }

    public Integer getPrimaryIndex() { return primaryIndex; }
    public void setPrimaryIndex(Integer primaryIndex) { this.primaryIndex = primaryIndex; }

    public List<Long> getKeepImageIds() {
        return keepImageIds;
    }

    public void setKeepImageIds(List<Long> keepImageIds) {
        this.keepImageIds = keepImageIds;
    }

    public List<Integer> getReplaceOrderIndexes() {
        return replaceOrderIndexes;
    }

    public void setReplaceOrderIndexes(List<Integer> replaceOrderIndexes) {
        this.replaceOrderIndexes = replaceOrderIndexes;
    }

    public Long getPrimaryImageId() {
        return primaryImageId;
    }

    public void setPrimaryImageId(Long primaryImageId) {
        this.primaryImageId = primaryImageId;
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

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isRestricted() {
        return isRestricted;
    }

    public void setRestricted(boolean restricted) {
        isRestricted = restricted;
    }

    /**
     * Gets the types of meal (e.g., breakfast, lunch, dinner, or snack).
     * Allows categorization of meals based on multiple intended meal times.
     *
     * @return the set of meal types.
     */
    public Set<MealType> getMealTypes() {
        return mealTypes;
    }

    /**
     * Sets the types of meal (e.g., breakfast, lunch, dinner, or snack).
     * Allows categorization of meals based on multiple intended meal times.
     *
     * @param mealTypes the set of meal types to set.
     */
    public void setMealTypes(Set<MealType> mealTypes) {
        this.mealTypes = mealTypes;
    }

    /**
     * Gets the cuisine types of the meal.
     * Represents the cultural or regional origins of the meal (e.g., Italian, French, Japanese).
     *
     * @return the set of cuisine types.
     */
    public Set<Cuisine> getCuisines() {
        return cuisines;
    }

    /**
     * Sets the cuisine types of the meal.
     * Defines the cultural or regional origins of the meal.
     *
     * @param cuisines the set of cuisine types to set.
     */
    public void setCuisines(Set<Cuisine> cuisines) {
        this.cuisines = cuisines;
    }

    /**
     * Gets the dietary categories of the meal.
     * Used for filtering meals based on dietary restrictions or preferences (e.g., vegetarian, vegan, gluten-free).
     *
     * @return the set of diet types.
     */
    public Set<Diet> getDiets() {
        return diets;
    }

    /**
     * Sets the dietary categories of the meal.
     * Allows categorization based on dietary restrictions or preferences.
     *
     * @param diets the set of diet types to set.
     */
    public void setDiets(Set<Diet> diets) {
        this.diets = diets;
    }

    public String getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(String preparationTime) {
        this.preparationTime = preparationTime;
    }

    public @Size(max = 2048, message = "Video URL must not exceed 2048 characters.") @URL(regexp = "https?://.*", message = "Must be a valid HTTP/HTTPS URL") String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(@Size(max = 2048, message = "Video URL must not exceed 2048 characters.") @URL(regexp = "https?://.*", message = "Must be a valid HTTP/HTTPS URL") String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public @Size(max = 2048, message = "Source URL must not exceed 2048 characters.") @URL(regexp = "https?://.*", message = "Must be a valid HTTP/HTTPS URL") String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(@Size(max = 2048, message = "Source URL must not exceed 2048 characters.") @URL(regexp = "https?://.*", message = "Must be a valid HTTP/HTTPS URL") String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public @Size(max = 2048, message = "Preparation video URL must not exceed 2048 characters.") @URL(regexp = "https?://.*", message = "Must be a valid HTTP/HTTPS URL") String getPreparationVideoUrl() {
        return preparationVideoUrl;
    }

    public void setPreparationVideoUrl(@Size(max = 2048, message = "Preparation video URL must not exceed 2048 characters.") @URL(regexp = "https?://.*", message = "Must be a valid HTTP/HTTPS URL") String preparationVideoUrl) {
        this.preparationVideoUrl = preparationVideoUrl;
    }

    public @Size(max = 20000, message = "Preparation text is too long.") String getMealPreparation() {
        return mealPreparation;
    }

    public void setMealPreparation(@Size(max = 20000, message = "Preparation text is too long.") String mealPreparation) {
        this.mealPreparation = mealPreparation;
    }
}

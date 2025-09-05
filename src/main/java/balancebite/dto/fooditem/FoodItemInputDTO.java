package balancebite.dto.fooditem;

import balancebite.dto.NutrientInfoDTO;
import balancebite.model.foodItem.FoodCategory;
import jakarta.validation.constraints.*;
import balancebite.model.foodItem.FoodSource;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;


import java.util.List;

/**
 * Input DTO class for creating or updating a FoodItem.
 * This DTO is used to transfer input data from client to server.
 */
public class FoodItemInputDTO {

    /**
     * Name of the food item.
     * Cannot be null or empty.
     */
    @NotBlank(message = "The name of the food item cannot be blank.")
    private String name;

    /**
     * The FDC (FoodData Central) ID associated with the food item.
     * Must be greater than zero.
     */
    @NotNull(message = "The FDC ID must be provided.")
    @Positive(message = "The FDC ID must be a positive integer.")
    private int fdcId;

    /**
     * List of nutrients associated with the food item.
     * Cannot be null.
     */
    @NotEmpty(message = "Nutrients list must contain at least one nutrient.")
    private List<NutrientInfoDTO> nutrients;

    /**
     * Description of the portion, such as "1 medium banana".
     */
    private String portionDescription;

    /**
     * The gram weight of the portion.
     * Must be greater than zero.
     */
    @NotNull(message = "The gram weight must be provided.")
    @PositiveOrZero(message = "The gram weight must be greater than or equal to zero.")
    private Double gramWeight;

    /**
     * Optional source indicating where the food item was purchased (e.g., supermarket name).
     */
    private String source;

    /**
     * Optional structured source of the food item, selected from predefined values (e.g., supermarkets).
     * This field complements the free-text 'source' field and provides consistent reference.
     */
    private FoodSource foodSource;

    private FoodCategory foodCategory;

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
    @Size(max = 2048, message = "Image URL must not exceed 2048 characters.")
    private String imageUrl;

    /**
     * MultipartFile for handling direct file uploads of the meal's image.
     * This field is optional.
     */
    private MultipartFile imageFile;

    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be ≥ 0.")
    private BigDecimal price;


    /** Net weight in grams for pricing context (optional). */
    @DecimalMin(value = "0.0", inclusive = true, message = "Grams must be ≥ 0.")
    private BigDecimal grams;

    private Boolean storeBrand;

    // Constructor, getters, and setters

    public FoodItemInputDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFdcId() {
        return fdcId;
    }

    public void setFdcId(int fdcId) {
        this.fdcId = fdcId;
    }

    public List<NutrientInfoDTO> getNutrients() {
        return nutrients;
    }

    public void setNutrients(List<NutrientInfoDTO> nutrients) {
        this.nutrients = nutrients;
    }

    public String getPortionDescription() {
        return portionDescription;
    }

    public void setPortionDescription(String portionDescription) {
        this.portionDescription = portionDescription;
    }

    public Double getGramWeight() {
        return gramWeight;
    }

    public void setGramWeight(Double gramWeight) {
        this.gramWeight = gramWeight;
    }

    public String getSource() {
        return source;
    }

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

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getGrams() { return grams; }

    public void setGrams(BigDecimal grams) { this.grams = grams; }

    public Boolean getStoreBrand() { return storeBrand; }
    public void setStoreBrand(Boolean storeBrand) { this.storeBrand = storeBrand; }
}

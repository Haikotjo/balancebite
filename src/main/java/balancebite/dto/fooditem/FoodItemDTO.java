package balancebite.dto.fooditem;

import balancebite.dto.NutrientInfoDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import balancebite.model.foodItem.FoodCategory;
import balancebite.model.foodItem.FoodSource;


/**
 * Data Transfer Object (DTO) representing a FoodItem.
 * This DTO is used to transfer food item data between layers of the application.
 * It calculates combined fat values for easier data handling.
 */
public class FoodItemDTO {

    /**
     * Unique identifier for the food item.
     */
    private final Long id;

    /**
     * Name of the food item.
     */
    private final String name;

    /**
     * The FDC (FoodData Central) ID associated with the food item.
     */
    private final int fdcId;

    /**
     * List of nutrients associated with the food item.
     */
    private final List<NutrientInfoDTO> nutrients;

    /**
     * Description of the portion, such as "1 medium banana".
     */
    private final String portionDescription;

    /**
     * The gram weight of the portion.
     */
    private final double gramWeight;

    /**
     * Optional source indicating where the food item was purchased (e.g., supermarket name).
     */
    private final String source;

    /**
     * Optional structured source of the food item (e.g., ALBERT_HEIJN, JUMBO).
     */
    private final FoodSource foodSource;

    private final boolean promoted;
    private final LocalDateTime promotionStartDate;
    private final LocalDateTime promotionEndDate;

    private final FoodCategory foodCategory;

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

    /** Base price (regular). */
    private final BigDecimal price;

    /** Net weight in grams for pricing context (optional). */
    private final BigDecimal grams;

    private final BigDecimal pricePer100g;

    private final Boolean storeBrand;

    /**
     * Parameterized constructor to create a FoodItemDTO.
     * Calculates the combined values for healthy and unhealthy fats.
     *
     * @param id The unique identifier of the food item.
     * @param name The name of the food item.
     * @param fdcId The FDC ID of the food item.
     * @param nutrients The list of nutrients associated with the food item.
     * @param portionDescription The description of the portion.
     * @param gramWeight The gram weight of the portion.
     *                     * @param source The source where the food item can be purchased (optional).
     * @param foodSource The source where the food item was purchased (optional).
     * @param image            the Base64-encoded image of the meal (optional).
     * @param imageUrl         the URL of the meal image (optional).
     */
    public FoodItemDTO(Long id, String name, int fdcId, List<NutrientInfoDTO> nutrients, String portionDescription, double gramWeight, String source, FoodSource foodSource, boolean promoted, LocalDateTime promotionStartDate, LocalDateTime promotionEndDate, FoodCategory foodCategory, String image, String imageUrl, BigDecimal price, BigDecimal grams, BigDecimal pricePer100g, Boolean storeBrand) {
        this.id = id;
        this.name = name;
        this.fdcId = fdcId;
        this.nutrients = processNutrients(nutrients);
        this.portionDescription = portionDescription;
        this.gramWeight = gramWeight;
        this.source = source;
        this.foodSource = foodSource;
        this.promoted = promoted;
        this.promotionStartDate = promotionStartDate;
        this.promotionEndDate = promotionEndDate;
        this.foodCategory = foodCategory;
        this.image = image;
        this.imageUrl = imageUrl;
        this.price = price;
        this.grams = grams;
        this.pricePer100g = pricePer100g;
        this.storeBrand = storeBrand;
    }

    /**
     * Processes the list of nutrients to combine values for saturated and unsaturated fats.
     * - Monounsaturated and polyunsaturated fats are grouped as "Unsaturated Fat".
     * - Saturated and trans fats are grouped as "Saturated Fat".
     * This simplifies the representation to just two fat types: healthy (unsaturated) and unhealthy (saturated).
     *
     * @param nutrients The original list of individual nutrient entries.
     * @return A new list of nutrients where individual fats are replaced with grouped totals.
     */
    private List<NutrientInfoDTO> processNutrients(List<NutrientInfoDTO> nutrients) {
        double unsaturatedFat = 0.0;
        double saturatedFat = 0.0;

        // Create a modifiable copy of the original list
        List<NutrientInfoDTO> updatedNutrients = new ArrayList<>(nutrients);

        // Calculate totals for saturated and unsaturated fats
        for (NutrientInfoDTO nutrient : nutrients) {
            switch (nutrient.getNutrientName()) {
                case "Fatty acids, total monounsaturated":
                case "Fatty acids, total polyunsaturated":
                    unsaturatedFat += nutrient.getValue();
                    break;
                case "Fatty acids, total unsaturated":
                    unsaturatedFat += nutrient.getValue();
                    break;
                case "Fatty acids, total saturated":
                case "Fatty acids, total trans":
                    saturatedFat += nutrient.getValue();
                    break;
                default:
                    // Other nutrients are left unchanged
                    break;
            }
        }

        // Remove original individual fat entries from the list
        updatedNutrients.removeIf(nutrient ->
                nutrient.getNutrientName().equals("Fatty acids, total monounsaturated") ||
                        nutrient.getNutrientName().equals("Fatty acids, total polyunsaturated") ||
                        nutrient.getNutrientName().equals("Fatty acids, total saturated") ||
                        nutrient.getNutrientName().equals("Fatty acids, total trans") ||
                        nutrient.getNutrientName().equals("Fatty acids, total unsaturated")
        );

        // Add combined fat values under clear display names
        if (unsaturatedFat > 0) {
            updatedNutrients.add(new NutrientInfoDTO("Unsaturated Fat", unsaturatedFat, "g"));
        }
        if (saturatedFat > 0) {
            updatedNutrients.add(new NutrientInfoDTO("Saturated Fat", saturatedFat, "g"));
        }

        // Return an unmodifiable copy of the updated list
        return List.copyOf(updatedNutrients);
    }

    // Getters only (no setters)

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
     * Gets the FDC ID of the food item.
     *
     * @return The FDC ID of the food item.
     */
    public int getFdcId() {
        return fdcId;
    }

    /**
     * Gets the list of nutrients associated with the food item.
     *
     * @return An unmodifiable list of nutrients.
     */
    public List<NutrientInfoDTO> getNutrients() {
        return List.copyOf(nutrients);  // Ensure the list cannot be mutated outside this DTO
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
     * Gets the gram weight of the portion.
     *
     * @return The gram weight of the portion.
     */
    public double getGramWeight() {
        return gramWeight;
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
     * Gets the structured food source (enum).
     *
     * @return The FoodSource enum value or null if not specified.
     */
    public FoodSource getFoodSource() {
        return foodSource;
    }

    public boolean isPromoted() {
        return promoted;
    }

    public LocalDateTime getPromotionStartDate() {
        return promotionStartDate;
    }

    public LocalDateTime getPromotionEndDate() {
        return promotionEndDate;
    }

    public FoodCategory getFoodCategory() {
        return foodCategory;
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

    /** Regular price. */
    public BigDecimal getPrice() { return price; }

    /** Weight in grams used for pricing. */
    public BigDecimal getGrams() { return grams; }

    public BigDecimal getPricePer100g() { return pricePer100g; }
    public Boolean getStoreBrand() { return storeBrand; }
}

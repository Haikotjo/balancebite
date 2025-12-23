package balancebite.model.meal.mealImage;

import balancebite.model.meal.Meal;
import jakarta.persistence.*;

/**
 * Entity class representing a single image that belongs to a Meal.
 * This class maps to the "meal_images" table in the database.
 *
 * Notes:
 * - Stores only the external URL (e.g., Cloudinary).
 * - Supports ordering and a primary image flag for future use.
 */
@Entity
@Table(name = "meal_images")
public class MealImage {

    /**
     * Unique identifier for the meal image.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The meal this image belongs to.
     * Many images can belong to one meal.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meal_id", nullable = false)
    private Meal meal;

    /**
     * External URL of the image (e.g., Cloudinary URL).
     */
    @Column(name = "image_url", length = 2048, nullable = false)
    private String imageUrl;

    /**
     * Indicates whether this image is the primary image for the meal.
     * Optional for now; useful when multiple images exist.
     */
    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary = false;

    /**
     * Order index for display sorting (0 = first).
     * Optional for now; useful when multiple images exist.
     */
    @Column(name = "order_index", nullable = false)
    private int orderIndex = 0;

    /**
     * Cloudinary public ID used for reliable deletion.
     */
    @Column(name = "public_id", length = 512, nullable = false)
    private String publicId;


    /**
     * No-argument constructor required by JPA.
     */
    public MealImage() {}

    /**
     * Constructor to initialize a MealImage with its required fields.
     *
     * @param meal the meal this image belongs to.
     * @param imageUrl the external image URL.
     */
    public MealImage(Meal meal, String imageUrl, String publicId) {
        this.meal = meal;
        this.imageUrl = imageUrl;
        this.publicId = publicId;
    }

    /**
     * Gets the unique identifier of the meal image.
     *
     * @return the ID of the meal image.
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the meal this image belongs to.
     *
     * @return the meal.
     */
    public Meal getMeal() {
        return meal;
    }

    /**
     * Sets the meal this image belongs to.
     *
     * @param meal the meal.
     */
    public void setMeal(Meal meal) {
        this.meal = meal;
    }

    /**
     * Gets the external image URL.
     *
     * @return the image URL.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the external image URL.
     *
     * @param imageUrl the image URL.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Returns whether this image is marked as primary.
     *
     * @return true if primary, otherwise false.
     */
    public boolean isPrimary() {
        return isPrimary;
    }

    /**
     * Sets whether this image is marked as primary.
     *
     * @param primary true if primary, otherwise false.
     */
    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    /**
     * Gets the order index used for sorting.
     *
     * @return the order index.
     */
    public int getOrderIndex() {
        return orderIndex;
    }

    /**
     * Sets the order index used for sorting.
     *
     * @param orderIndex the order index.
     */
    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    // equals/hashCode on id so Set.remove() works with proxies
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MealImage mi)) return false;
        return id != null && id.equals(mi.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

}

package balancebite.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    private String mealDescription;

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
    @JoinColumn(name = CREATED_BY_USER_ID_COLUMN, updatable = false)
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
     * No-argument constructor required by JPA.
     */
    public Meal() {}

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
}

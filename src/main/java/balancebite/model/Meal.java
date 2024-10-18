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

    private static final String USER_MEALS_TABLE = "user_meals";
    private static final String MEAL_ID_COLUMN = "meal_id";
    private static final String USER_ID_COLUMN = "user_id";
    private static final String CREATED_BY_USER_ID_COLUMN = "created_by_user_id";

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
     * List of meal ingredients associated with the meal.
     * Each ingredient corresponds to a food item with a specified quantity.
     */
    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MealIngredient> mealIngredients = new ArrayList<>();

    /**
     * Many-to-Many relationship with User.
     * A meal can be associated with multiple users, and a user can have multiple meals.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = USER_MEALS_TABLE,  // Join table to link users and meals
            joinColumns = @JoinColumn(name = MEAL_ID_COLUMN),  // Foreign key column in user_meals for meal
            inverseJoinColumns = @JoinColumn(name = USER_ID_COLUMN)  // Foreign key column in user_meals for user
    )
    private List<User> users = new ArrayList<>();

    /**
     * The user who created this meal.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = CREATED_BY_USER_ID_COLUMN)
    private User createdBy;

    /**
     * No-argument constructor required by JPA.
     */
    public Meal() {}

    /**
     * Constructor to initialize a Meal with a name.
     *
     * @param name the name of the meal.
     */
    public Meal(String name) {
        this.name = name;
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
     * Gets the list of users associated with the meal.
     *
     * @return the list of users associated with the meal.
     */
    public List<User> getUsers() {
        return users;
    }

    /**
     * Sets the list of users associated with the meal.
     *
     * @param users the list of users to associate with the meal.
     */
    public void setUsers(List<User> users) {
        this.users = users;
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
}

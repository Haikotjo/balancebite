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
    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealIngredient> mealIngredients = new ArrayList<>();

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
     *
     * @param mealIngredient the meal ingredient to add.
     */
    public void addMealIngredient(MealIngredient mealIngredient) {
        mealIngredients.add(mealIngredient);
        mealIngredient.setMeal(this);
    }

    /**
     * Adds a list of meal ingredients to the meal and sets the relationship.
     *
     * @param mealIngredients the list of meal ingredients to add.
     */
    public void addMealIngredients(List<MealIngredient> mealIngredients) {
        for (MealIngredient mealIngredient : mealIngredients) {
            addMealIngredient(mealIngredient);
        }
    }
}

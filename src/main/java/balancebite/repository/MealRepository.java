package balancebite.repository;

import balancebite.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing and managing Meal entities in the database.
 * Provides methods for saving, finding, and querying meals by specific criteria.
 */
public interface MealRepository extends JpaRepository<Meal, Long> {

    /**
     * Finds a meal by its name.
     * This method allows searching for meals with an exact match on the name.
     *
     * @param name the name of the meal to search for
     * @return an Optional containing the meal if found, or an empty Optional if no meal with the given name exists
     */
    Optional<Meal> findByName(String name);

    /**
     * Finds meals containing the specified partial name.
     * This method allows searching for meals with a name that includes the specified text, making it useful for partial matches.
     *
     * @param partialName the partial name of the meal to search for
     * @return a list of meals that contain the partial name in their name
     */
    List<Meal> findByNameContaining(String partialName);

    /**
     * Finds all meals with the same ingredients as the provided meal, ignoring quantities.
     * This custom query checks for meals with identical food items, regardless of meal name and quantity.
     * Ensures that only meals with the exact same set of ingredients are returned.
     *
     * @param mealId the ID of the meal to compare with others
     * @return a list of meals that have the same ingredients as the specified meal, ignoring quantities
     */
    @Query("SELECT m FROM Meal m WHERE m.id <> :mealId AND " +
            "NOT EXISTS (SELECT mi FROM MealIngredient mi WHERE mi.meal.id = :mealId AND mi.foodItem NOT IN " +
            "(SELECT mi2.foodItem FROM MealIngredient mi2 WHERE mi2.meal = m)) " +
            "AND NOT EXISTS (SELECT mi2 FROM MealIngredient mi2 WHERE mi2.meal = m AND mi2.foodItem NOT IN " +
            "(SELECT mi.foodItem FROM MealIngredient mi WHERE mi.meal.id = :mealId))")
    List<Meal> findMealsWithSameIngredients(@Param("mealId") Long mealId);


    /**
     * Retrieves all meals marked as templates.
     * This query returns only the meals where isTemplate is set to true,
     * filtering out any user-specific copies or adjusted meals.
     *
     * @return a list of meals that are marked as templates (isTemplate = true)
     */
    @Query("SELECT m FROM Meal m WHERE m.isTemplate = true")
    List<Meal> findAllTemplateMeals();
}

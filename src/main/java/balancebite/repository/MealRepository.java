package balancebite.repository;

import balancebite.model.Meal;
import balancebite.model.user.User;
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
     * Finds meals in a user's meal list that have the exact same ingredients and quantities as the provided meal.
     * This ensures the user does not have duplicate meals with identical ingredients and quantities.
     *
     * @param mealId the ID of the meal to compare ingredients with
     * @param userId the ID of the user to check within their meal list
     * @return a list of meals with identical ingredients and quantities as the specified meal in the user's list
     */
    @Query("SELECT m FROM User u JOIN u.meals m WHERE u.id = :userId AND m.id <> :mealId " +
            "AND NOT EXISTS (SELECT mi FROM MealIngredient mi WHERE mi.meal.id = :mealId " +
            "AND NOT EXISTS (SELECT mi2 FROM MealIngredient mi2 WHERE mi2.meal = m " +
            "AND mi2.foodItem = mi.foodItem AND mi2.quantity = mi.quantity)) " +
            "AND NOT EXISTS (SELECT mi2 FROM MealIngredient mi2 WHERE mi2.meal = m " +
            "AND NOT EXISTS (SELECT mi FROM MealIngredient mi WHERE mi.meal.id = :mealId " +
            "AND mi.foodItem = mi2.foodItem AND mi.quantity = mi2.quantity))")
    List<Meal> findUserMealsWithExactIngredients(@Param("mealId") Long mealId, @Param("userId") Long userId);

    /**
     * Finds template meals with the exact same ingredients as a provided meal.
     * This ensures that there are no duplicate template meals with identical ingredients in the app,
     * allowing multiple meals with the same name but only if the ingredients differ.
     *
     * @param foodItemIds the list of food item IDs to check
     * @param size the expected size of the ingredient list
     * @return a list of template meals with identical ingredients as the specified meal
     */
    @Query("SELECT m FROM Meal m " +
            "JOIN m.mealIngredients mi " +
            "WHERE m.isTemplate = true " +
            "AND mi.foodItem.id IN :foodItemIds " +
            "GROUP BY m.id " +
            "HAVING COUNT(mi) = :size")
    List<Meal> findTemplateMealsWithExactIngredients(@Param("foodItemIds") List<Long> foodItemIds, @Param("size") long size);

    /**
     * Retrieves all meals marked as templates.
     * This query returns only the meals where isTemplate is set to true,
     * filtering out any user-specific copies or adjusted meals.
     *
     * @return a list of meals that are marked as templates (isTemplate = true)
     */
    @Query("SELECT m FROM Meal m WHERE m.isTemplate = true")
    List<Meal> findAllTemplateMeals();

    /**
     * Finds all meals where the createdBy user ID matches the given userId.
     *
     * @param userId The ID of the user who created the meals.
     * @return A list of meals created by the specified user.
     */
    List<Meal> findByCreatedBy_Id(Long userId);

    /**
     * Finds all meals where the user is referenced as createdBy or adjustedBy.
     *
     * @param user The user to search for.
     * @return A list of meals where the user is referenced.
     */
    @Query("SELECT m FROM Meal m WHERE m.createdBy = :user OR m.adjustedBy = :user")
    List<Meal> findByCreatedByOrAdjustedBy(@Param("user") User user);
}

package balancebite.repository;

import balancebite.dto.NutrientInfoDTO;
import balancebite.dto.fooditem.FoodItemNameDTO;
import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealNameDTO;
import balancebite.model.meal.Meal;
import balancebite.model.meal.references.Cuisine;
import balancebite.model.meal.references.Diet;
import balancebite.model.meal.references.MealType;
import balancebite.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing and managing Meal entities in the database.
 * Provides methods for saving, finding, and querying meals by specific criteria.
 */
public interface MealRepository extends JpaRepository<Meal, Long>, JpaSpecificationExecutor<Meal> {

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
    @Query("""
    SELECT m FROM Meal m
    WHERE m.isTemplate = true
    AND SIZE(m.mealIngredients) = :size
    AND NOT EXISTS (
        SELECT mi FROM MealIngredient mi
        WHERE mi.meal = m
        AND mi.foodItem.id NOT IN :foodItemIds
    )
    AND NOT EXISTS (
        SELECT id FROM FoodItem fi
        WHERE fi.id IN :foodItemIds
        AND fi.id NOT IN (
            SELECT mi.foodItem.id FROM MealIngredient mi WHERE mi.meal = m
        )
    )
""")
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

    /**
     * Retrieves all meals that match the given filters, including filtering by food items, and applies sorting and pagination.
     *
     * @param createdByUserId Optional user ID to filter meals by creator.
     * @param cuisines Optional cuisine type to filter meals.
     * @param diets Optional diet type to filter meals.
     * @param mealTypes Optional meal type to filter meals (e.g., "Breakfast", "Lunch").
     * @param foodItems Optional list of food items to filter meals by (must contain at least one).
     * @param pageable Pageable object for sorting and paginating results.
     * @return A paginated and sorted list of Meal objects.
     */
    @Query("""
    SELECT DISTINCT m FROM Meal m
    LEFT JOIN m.mealIngredients mi
    LEFT JOIN m.cuisines c
    LEFT JOIN m.diets d
    LEFT JOIN m.mealTypes mt
    WHERE (:createdByUserId IS NULL OR m.createdBy.id = :createdByUserId)
      AND (:cuisines IS NULL OR c IN (:cuisines))
      AND (:diets IS NULL OR d IN (:diets))
      AND (:mealTypes IS NULL OR mt IN (:mealTypes))
      AND (:foodItems IS NULL OR mi.foodItem.name IN :foodItems)
""")
    Page<Meal> findMealsWithFilters(
            @Param("createdByUserId") Long createdByUserId,
            @Param("cuisines") List<Cuisine> cuisines,
            @Param("diets") List<Diet> diets,
            @Param("mealTypes") List<MealType> mealTypes,
            @Param("foodItems") List<String> foodItems,
            Pageable pageable
    );


    /**
     * Retrieves all meals with only ID and name.
     *
     * @return A list of MealNameDTOs containing only ID and name.
     */
    @Query("SELECT DISTINCT new balancebite.dto.meal.MealNameDTO(m.id, m.name) FROM Meal m WHERE m.isTemplate = true")
    List<MealNameDTO> findAllMealNames();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM saved_meal WHERE meal_id = :mealId", nativeQuery = true)
    void deleteFromSavedMeal(long mealId);

    @Query("SELECT m FROM Meal m WHERE m.adjustedBy.id = :userId AND m.originalMealId IN :originalMealIds")
    List<Meal> findByAdjustedBy_IdAndOriginalMealIdIn(@Param("userId") Long userId, @Param("originalMealIds") List<Long> originalMealIds);

    @Query("""
    SELECT m
    FROM Meal m
    WHERE (m.createdBy.id = :userId OR m.adjustedBy.id = :userId)
      AND m.originalMealId IN :originalMealIds
""")
    List<Meal> findUserCopiesForTemplates(
            @Param("userId") Long userId,
            @Param("originalMealIds") List<Long> originalMealIds
    );

}




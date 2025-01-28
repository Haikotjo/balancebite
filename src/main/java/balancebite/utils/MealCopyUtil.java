package balancebite.util;

import balancebite.model.meal.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class responsible for creating deep copies of Meal entities.
 * Preserves the original creator while associating the copy with a new user.
 */
public class MealCopyUtil {

    private static final Logger log = LoggerFactory.getLogger(MealCopyUtil.class);

    /**
     * Creates a deep copy of a meal for a specific user.
     * Preserves the original creator while associating the copy with the new user.
     *
     * @param originalMeal The original Meal entity to copy.
     * @param user         The User entity to associate with the copied Meal.
     * @return A new Meal entity that is a copy of the original Meal.
     */
    public static Meal createMealCopy(Meal originalMeal, User user) {
        log.info("Creating a copy of meal ID: {} for user ID: {}", originalMeal.getId(), user.getId());

        // Create a new meal instance
        Meal mealCopy = new Meal();
        mealCopy.setName(originalMeal.getName());
        mealCopy.setMealDescription(originalMeal.getMealDescription());
        mealCopy.setCreatedBy(originalMeal.getCreatedBy());
        mealCopy.setAdjustedBy(user);
        mealCopy.setIsTemplate(false);

        // Copy ingredients
        originalMeal.getMealIngredients().forEach(ingredient -> {
            MealIngredient copiedIngredient = new MealIngredient();
            copiedIngredient.setFoodItem(ingredient.getFoodItem());
            copiedIngredient.setQuantity(ingredient.getQuantity());
            mealCopy.addMealIngredient(copiedIngredient);
        });

        log.debug("Meal copy created: {}", mealCopy);
        return mealCopy;
    }
}

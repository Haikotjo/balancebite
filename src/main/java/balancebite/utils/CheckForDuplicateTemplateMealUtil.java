package balancebite.utils;

import balancebite.errorHandling.DuplicateMealException;
import balancebite.model.Meal;
import balancebite.repository.MealRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Utility class for checking duplicate template meals.
 * This class provides a method to verify if a meal with identical ingredients already exists in the database.
 * If a duplicate template meal is found, a DuplicateMealException is thrown.
 */
@Component
public class CheckForDuplicateTemplateMealUtil {

    private static final Logger log = LoggerFactory.getLogger(CheckForDuplicateTemplateMealUtil.class);

    private final MealRepository mealRepository;

    /**
     * Constructor for CheckForDuplicateTemplateMealUtil.
     *
     * @param mealRepository the repository for accessing Meal entities.
     */
    public CheckForDuplicateTemplateMealUtil(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    /**
     * Checks for duplicate template meals with the exact same ingredients.
     * This method queries the database for any template meal that has the same ingredients as specified
     * by the provided food item IDs. If a duplicate is found, it throws a DuplicateMealException.
     *
     * @param foodItemIds    List of food item IDs to be checked for duplication.
     * @param currentMealId  The ID of the current meal (for updates, to exclude itself).
     *                       This can be null for new meals.
     * @throws DuplicateMealException if a duplicate template meal is found.
     */
    public void checkForDuplicateTemplateMeal(List<Long> foodItemIds, Long currentMealId) {
        log.info("Checking for duplicate template meals with food item IDs: {}", foodItemIds);

        // Query the repository for template meals with the exact same ingredients
        List<Meal> duplicateMeals = mealRepository.findTemplateMealsWithExactIngredients(foodItemIds, foodItemIds.size());

        // Exclude the current meal from the check if an ID is provided, to avoid self-duplication
        if (currentMealId != null) {
            duplicateMeals = duplicateMeals.stream()
                    .filter(dupMeal -> !dupMeal.getId().equals(currentMealId))
                    .toList();
        }

        // Check if any duplicate meal exists
        if (!duplicateMeals.isEmpty()) {
            Meal duplicateMeal = duplicateMeals.get(0); // Log only the first duplicate for simplicity
            String duplicateInfo = String.format("Meal Name: %s, Meal ID: %d", duplicateMeal.getName(), duplicateMeal.getId());
            log.warn("Duplicate template meal detected: {}", duplicateInfo);
            throw new DuplicateMealException("A template meal with the same ingredients already exists. " + duplicateInfo);
        }

        log.info("No duplicate template meals found for the provided ingredients.");
    }
}

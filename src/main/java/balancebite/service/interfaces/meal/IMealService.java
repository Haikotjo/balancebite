package balancebite.service.interfaces.meal;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.dto.meal.MealNameDTO;
import balancebite.errorHandling.InvalidFoodItemException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Interface for managing Meal-related operations.
 * Defines methods for creating, updating, retrieving, and deleting Meal entities,
 * as well as calculating nutrient information.
 */
public interface IMealService {

    /**
     * Retrieves paginated and sorted template meals with optional filtering.
     *
     * Users can filter meals by cuisine, diet, meal type, and food items.
     * Additional filters are available for total calories, protein, fat, and carbs (min/max values).
     * Meals can be sorted by name, total calories, protein, fat, or carbs.
     * Results are paginated.
     *
     * @param cuisines    Optional filter for meal cuisines.
     * @param diets       Optional filter for meal diets.
     * @param mealTypes   Optional filter for meal types (e.g., BREAKFAST, LUNCH).
     * @param foodItems   Optional list of food items to match against meals.
     * @param sortBy      Field to sort by (e.g., "calories", "protein", "fat", "carbs", "name").
     * @param sortOrder   Sort direction ("asc" or "desc").
     * @param pageable    Pagination settings.
     * @param creatorId   Optional filter for meals created by a specific user.
     * @param minCalories Optional minimum total calories.
     * @param maxCalories Optional maximum total calories.
     * @param minProtein  Optional minimum total protein.
     * @param maxProtein  Optional maximum total protein.
     * @param minCarbs    Optional minimum total carbohydrates.
     * @param maxCarbs    Optional maximum total carbohydrates.
     * @param minFat      Optional minimum total fat.
     * @param maxFat      Optional maximum total fat.
     * @return A paginated and sorted list of MealDTOs that match the filters.
     */
    Page<MealDTO> getAllMeals(
            List<String> cuisines,
            List<String> diets,
            List<String> mealTypes,
            List<String> foodItems,
            String sortBy,
            String sortOrder,
            Pageable pageable,
            Long creatorId,
            Double minCalories,
            Double maxCalories,
            Double minProtein,
            Double maxProtein,
            Double minCarbs,
            Double maxCarbs,
            Double minFat,
            Double maxFat,
            String foodSource
    );

    /**
     * Retrieves a Meal by its ID, only if it is a template.
     *
     * @param id The ID of the Meal.
     * @return The MealDTO.
     * @throws EntityNotFoundException If the meal with the given ID is not found,
     *                                 or if the meal is not a template.
     */
    MealDTO getMealById(Long id) throws EntityNotFoundException;

    /**
     * Retrieves the total nutrients for a given Meal by its ID.
     *
     * @param mealId The ID of the Meal.
     * @return A map of nutrient names and their corresponding total values for the meal.
     * @throws EntityNotFoundException If the meal with the given ID is not found.
     */
    Map<String, NutrientInfoDTO> calculateNutrients(Long mealId) throws EntityNotFoundException;

    /**
     * Retrieves the nutrients per food item for a given Meal by its ID.
     *
     * @param mealId The ID of the Meal.
     * @return A map of food item IDs to nutrient maps, where each map contains nutrient names and their values.
     * @throws EntityNotFoundException If the meal with the given ID is not found.
     */
    Map<Long, Map<String, NutrientInfoDTO>> calculateNutrientsPerFoodItem(Long mealId) throws EntityNotFoundException;

    /**
     * Retrieves a list of all Meal, returning only their ID and name.
     *
     * @return A list of MealNameDTOs containing only ID and name.
     */
    List<MealNameDTO> getAllMealNames();
}

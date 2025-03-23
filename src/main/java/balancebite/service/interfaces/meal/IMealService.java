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
     * Meals can be sorted by name, total calories, protein, fat, or carbs.
     * Results are paginated.
     *
     * @param cuisine Optional filter for meal cuisine.
     * @param diet Optional filter for meal diet.
     * @param mealType Optional filter for meal type (BREAKFAST, LUNCH, etc.).
     * @param foodItems List of food items to filter meals by (e.g., "Banana", "Peas").
     * @param sortBy Sorting field (calories, protein, fat, carbs, name).
     * @param sortOrder Sorting order ("asc" for ascending, "desc" for descending).
     * @param pageable Pageable object for pagination and sorting.
     * @return A paginated and sorted list of MealDTOs that match the filters.
     */
    Page<MealDTO> getAllMeals(
            List<String> cuisines,
            List<String> diets,
            List<String> mealTypes,
            List<String> foodItems,
            String sortBy,
            String sortOrder,
            Pageable pageable
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

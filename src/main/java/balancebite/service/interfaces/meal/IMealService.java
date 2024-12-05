package balancebite.service.interfaces.meal;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.errorHandling.InvalidFoodItemException;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Map;

/**
 * Interface for managing Meal-related operations.
 * Defines methods for creating, updating, retrieving, and deleting Meal entities,
 * as well as calculating nutrient information.
 */
public interface IMealService {

//    /**
//     * Creates a new Meal entity based on the provided MealInputDTO.
//     *
//     * @param mealInputDTO The DTO containing the input data for creating a Meal.
//     * @return The created MealDTO with the persisted meal information.
//     * @throws InvalidFoodItemException If any food item in the input is invalid.
//     */
//    MealDTO createMealNoUser(MealInputDTO mealInputDTO) throws InvalidFoodItemException;

//    /**
//     * Updates an existing Meal entity with new information.
//     *
//     * @param id           The ID of the meal to be updated.
//     * @param mealInputDTO The DTO containing the updated meal information.
//     * @return The updated MealDTO containing the new meal data.
//     * @throws EntityNotFoundException  If the meal with the given ID is not found.
//     * @throws InvalidFoodItemException If any food item ID in the ingredients is invalid.
//     */
//    MealDTO updateMeal(Long id, MealInputDTO mealInputDTO) throws EntityNotFoundException, InvalidFoodItemException;

    /**
     * Retrieves all Meals from the repository.
     *
     * @return A list of MealDTOs, or an empty list if no meals are found.
     */
    List<MealDTO> getAllMeals();

    /**
     * Retrieves a Meal by its ID, only if it is a template.
     *
     * @param id The ID of the Meal.
     * @return The MealDTO.
     * @throws EntityNotFoundException If the meal with the given ID is not found,
     *                                 or if the meal is not a template.
     */
    MealDTO getMealById(Long id) throws EntityNotFoundException;

//    /**
//     * Deletes a specific meal from the repository.
//     *
//     * @param mealId The ID of the meal to be deleted.
//     * @throws EntityNotFoundException If the meal with the given ID is not found.
//     */
//    void deleteMeal(Long mealId) throws EntityNotFoundException;

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
}

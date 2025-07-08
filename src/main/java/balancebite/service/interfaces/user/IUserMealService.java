package balancebite.service.interfaces.user;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.DuplicateMealException;
import balancebite.errorHandling.InvalidFoodItemException;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import jakarta.persistence.EntityNotFoundException;

/**
 * Interface defining methods for managing the relationship between users and meals.
 * This includes adding and removing meals from a user's list.
 */
public interface IUserMealService {

    /**
     * Creates a new Meal entity for a specific user based on the provided MealInputDTO.
     *
     * @param mealInputDTO The DTO containing the input data for creating a Meal.
     * @param userId       The ID of the user to whom the meal will be associated.
     * @return The created MealDTO with the persisted meal information.
     * @throws InvalidFoodItemException If any food item in the input is invalid.
     * @throws EntityNotFoundException  If the user cannot be found.
     */
    MealDTO createMealForUser(MealInputDTO mealInputDTO, Long userId) throws InvalidFoodItemException, EntityNotFoundException;

    /**
     * Updates an existing Meal entity for a specific user.
     * Only meals in the user's list can be updated, with appropriate checks based on the template status.
     *
     * @param userId        The ID of the user whose meal is to be updated.
     * @param mealId        The ID of the meal to be updated.
     * @param mealInputDTO  The new details of the meal.
     * @return The updated MealDTO with the new meal data.
     * @throws EntityNotFoundException if the user or meal cannot be found.
     * @throws InvalidFoodItemException if any food item ID in the ingredients is invalid.
     * @throws DuplicateMealException if updating would create a duplicate template meal.
     */
    MealDTO updateUserMeal(Long userId, Long mealId, MealInputDTO mealInputDTO) throws EntityNotFoundException, InvalidFoodItemException, DuplicateMealException;

    /**
     * Updates the privacy setting of a meal by its ID.
     *
     * @param userId The ID of the user to whom the meal is to be added.
     * @param mealId    The ID of the meal to update.
     * @param isPrivate {@code true} to mark the meal as private, {@code false} to make it public.
     * @throws MealNotFoundException if the meal does not exist.
     */
    void updateMealPrivacy(Long userId, Long mealId, boolean isPrivate);

    void updateMealRestriction(Long userId, Long mealId, boolean isRestricted);

    /**
     * Adds an existing meal to the user's list of meals.
     *
     * @param userId The ID of the user to whom the meal is to be added.
     * @param mealId The ID of the meal to be added.
     * @return The updated UserDTO with the newly added meal.
     * @throws UserNotFoundException If the user with the specified ID is not found.
     * @throws MealNotFoundException If the meal with the specified ID is not found.
     */
    UserDTO addMealToUser(Long userId, Long mealId);

    /**
     * Retrieves a Meal by its ID, only if it belongs to the authenticated user.
     *
     * @param id     The ID of the Meal.
     * @param userId The ID of the authenticated user.
     * @return The MealDTO.
     * @throws EntityNotFoundException If the meal with the given ID is not found,
     *                                 or if the meal does not belong to the user.
     */
    MealDTO getUserMealById(Long id, Long userId) throws EntityNotFoundException;

    /**
     * Removes a meal from the user's list of meals.
     * This method checks if the user exists, and if the meal is associated with the user.
     *
     * @param userId The ID of the user.
     * @param mealId The ID of the meal to be removed.
     * @return The updated UserDTO without the removed meal.
     * @throws UserNotFoundException If the user with the specified ID is not found.
     * @throws MealNotFoundException If the meal with the specified ID is not associated with the user's meal list.
     */
    UserDTO removeMealFromUser(Long userId, Long mealId);

    void forceRemoveMealFromUser(Long userId, Long mealId);

}

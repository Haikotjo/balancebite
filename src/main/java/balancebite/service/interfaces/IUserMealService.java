package balancebite.service.interfaces;

import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;

/**
 * Interface defining methods for managing the relationship between users and meals.
 * This includes adding and removing meals from a user's list.
 */
public interface IUserMealService {

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
}

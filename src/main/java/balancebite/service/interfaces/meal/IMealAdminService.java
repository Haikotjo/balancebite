package balancebite.service.interfaces.meal;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.DuplicateMealException;
import balancebite.errorHandling.InvalidFoodItemException;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;


public interface IMealAdminService {
    /**
     * Creates a new Meal entity based on the provided MealInputDTO and associates it with a specific user if provided.
     * If no userId is provided, the meal is associated with the admin or chef creating it.
     *
     * @param mealInputDTO        The DTO containing the input data for creating a Meal.
     * @param authenticatedUserId The ID of the authenticated admin or chef creating the meal.
     * @param userId              Optional: The ID of the user to associate the meal with.
     * @return The created MealDTO with the persisted meal information.
     * @throws InvalidFoodItemException If any food item in the input is invalid.
     * @throws DuplicateMealException   If a template meal with the same ingredients already exists.
     * @throws EntityNotFoundException  If the user with the provided ID is not found.
     */
    MealDTO createMealForAdmin(MealInputDTO mealInputDTO, Long authenticatedUserId, Long userId)
            throws InvalidFoodItemException, DuplicateMealException, EntityNotFoundException;


    /**
     * Updates an existing Meal entity with new information.
     *
     * @param id           The ID of the meal to be updated.
     * @param mealInputDTO The DTO containing the updated meal information.
     * @return The updated MealDTO containing the new meal data.
     * @throws EntityNotFoundException  If the meal with the given ID is not found.
     * @throws InvalidFoodItemException If any food item ID in the ingredients is invalid.
     */
    MealDTO updateMeal(Long id, MealInputDTO mealInputDTO) throws EntityNotFoundException, InvalidFoodItemException;

    /**
     * Assigns a copy of an existing meal to a user's meal list.
     *
     * @param userId The ID of the user to assign the meal to.
     * @param mealId The ID of the meal to copy and assign.
     * @return UserDTO with the updated user data.
     * @throws UserNotFoundException  if the user is not found.
     * @throws MealNotFoundException  if the meal is not found.
     * @throws DuplicateMealException if the user already has a meal with the same content.
     */
    UserDTO addMealToUser(Long userId, Long mealId)
            throws UserNotFoundException, MealNotFoundException, DuplicateMealException;


    /**
     * Retrieves all Meals from the repository.
     *
     * @return A list of MealDTOs, or an empty list if no meals are found.
     */
    List<MealDTO> getAllMeals();

    /**
     * Retrieves a Meal by its ID, regardless of ownership or template status.
     *
     * @param id The ID of the Meal.
     * @return The MealDTO.
     * @throws EntityNotFoundException If the meal with the given ID is not found.
     */
    MealDTO getMealById(Long id) throws EntityNotFoundException;



    /**
     * Deletes a specific meal from the repository.
     *
     * @param mealId The ID of the meal to be deleted.
     * @throws EntityNotFoundException If the meal with the given ID is not found.
     */
    void deleteMeal(Long mealId) throws EntityNotFoundException;
}

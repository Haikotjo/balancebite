package balancebite.service;

import balancebite.dto.NutrientInfoDTO;
import balancebite.dto.user.UserBasicInfoInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserDetailsInputDTO;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;

import java.util.List;

/**
 * Interface defining the methods for managing User operations.
 * This includes methods for creating, updating, retrieving, and deleting users, as well as adding and removing meals from a user's list.
 */
public interface IUserService {

    /**
     * Creates a new user in the system based on the provided UserBasicInfoInputDTO.
     * Meals are not added at the time of creation.
     *
     * @param userBasicInfoInputDTO The input data for creating the user.
     * @return The created UserDTO.
     * @throws EntityAlreadyExistsException If a user with the provided email already exists.
     */
    UserDTO createUser(UserBasicInfoInputDTO userBasicInfoInputDTO);

    /**
     * Updates the basic information of an existing user in the system based on the provided UserBasicInfoInputDTO.
     *
     * @param id The ID of the user to update.
     * @param userBasicInfoInputDTO The input data for updating the user.
     * @return The updated UserDTO.
     * @throws UserNotFoundException If the user with the specified ID is not found.
     */
    UserDTO updateUserBasicInfo(Long id, UserBasicInfoInputDTO userBasicInfoInputDTO);

    /**
     * Updates the detailed information of an existing user in the system based on the provided UserDetailsInputDTO.
     *
     * @param id The ID of the user to update.
     * @param userDetailsInputDTO The input data for updating the user's detailed information.
     * @return The updated UserDTO.
     * @throws UserNotFoundException If the user with the specified ID is not found.
     */
    UserDTO updateUserDetails(Long id, UserDetailsInputDTO userDetailsInputDTO);

    /**
     * Retrieves all users in the system.
     *
     * @return A list of UserDTOs representing all users.
     */
    List<UserDTO> getAllUsers();

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The UserDTO representing the user.
     * @throws UserNotFoundException If the user with the specified ID is not found.
     */
    UserDTO getUserById(Long id);

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     * @throws UserNotFoundException If the user with the specified ID is not found.
     */
    void deleteUser(Long id);

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

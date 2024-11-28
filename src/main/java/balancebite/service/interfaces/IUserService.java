package balancebite.service.interfaces;

import balancebite.dto.user.UserRegistrationInputDTO;
import balancebite.dto.user.UserDetailsInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.errorHandling.UserNotFoundException;

import java.util.List;

/**
 * Interface defining the methods for managing User operations.
 * This includes methods for updating, retrieving, and deleting users.
 */
public interface IUserService {

    /**
     * Updates the basic information of the currently logged-in user.
     * Uses the user ID extracted from the JWT token.
     *
     * @param userId The ID of the currently logged-in user.
     * @param userRegistrationInputDTO The input data for updating the user.
     * @return The updated UserDTO.
     * @throws UserNotFoundException If the user with the specified ID is not found.
     * @throws EntityAlreadyExistsException If the provided email already exists for another user.
     */
    UserDTO updateUserBasicInfo(Long userId, UserRegistrationInputDTO userRegistrationInputDTO);

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
}

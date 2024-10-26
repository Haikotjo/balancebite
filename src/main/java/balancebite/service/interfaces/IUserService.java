package balancebite.service.interfaces;

import balancebite.dto.user.UserBasicInfoInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserDetailsInputDTO;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.errorHandling.UserNotFoundException;

import java.util.List;

/**
 * Interface defining the methods for managing User operations.
 * This includes methods for creating, updating, retrieving, and deleting users.
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
}

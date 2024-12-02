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
public interface IUserAdminService {

    /**
     * Updates the basic information of an existing user in the system based on the provided UserRegistrationInputDTO.
     * Only admins are allowed to perform this action.
     *
     * @param userRegistrationInputDTO The input DTO containing the user ID and updated user information.
     * @return The updated UserDTO.
     */
    UserDTO updateUserBasicInfoForAdmin(UserRegistrationInputDTO userRegistrationInputDTO);

    /**
     * Retrieves all users in the system.
     *
     * @return A list of UserDTOs representing all users.
     */
    List<UserDTO> getAllUsers();

//    /**
//     * Retrieves a user by their unique ID.
//     *
//     * @param id The ID of the user to retrieve.
//     * @return The UserDTO representing the user.
//     * @throws UserNotFoundException If the user with the specified ID is not found.
//     */
//    UserDTO getUserById(Long id);

//    /**
//     * Deletes a user by their ID.
//     *
//     * @param id The ID of the user to delete.
//     * @throws UserNotFoundException If the user with the specified ID is not found.
//     */
//    void deleteUser(Long id);
}

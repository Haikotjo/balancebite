package balancebite.service.interfaces.user;

import balancebite.dto.user.UserRegistrationInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.errorHandling.EntityAlreadyExistsException;

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

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     * @throws UserNotFoundException If the user with the specified ID is not found.
     */
    void deleteUser(Long id);


    /**
     * Updates the roles and optionally the food source of a user based on their email address.
     *
     * @param email the email address of the user whose roles should be updated
     * @param roleNames a list of role names to assign (e.g., ["USER", "ADMIN"])
     * @param foodSource the supermarket source (e.g., "DIRK"), can be null if not applicable
     * @throws UserNotFoundException if no user is found with the provided email
     * @throws RuntimeException if one or more role names are invalid
     */
    void updateUserRolesByEmail(String email, List<String> roleNames, String foodSource);

    /**
     * Registers a new user with the specified roles and optionally a food source.
     * This method is intended for admin use only.
     *
     * @param registrationDTO The input DTO containing user registration details.
     * @throws EntityAlreadyExistsException If a user with the given email already exists.
     */
    void registerUserAsAdmin(UserRegistrationInputDTO registrationDTO);
}

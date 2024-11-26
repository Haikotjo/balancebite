package balancebite.service.interfaces;

import balancebite.dto.user.UserRegistrationInputDTO;

/**
 * Interface for user registration services.
 * Defines methods for user registration and related operations.
 */
public interface IRegistrationService {

    /**
     * Registers a new user using the provided registration details.
     *
     * @param registrationDTO The DTO containing user registration details.
     */
    void registerUser(UserRegistrationInputDTO registrationDTO);
}

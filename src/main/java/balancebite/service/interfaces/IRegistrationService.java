package balancebite.service.interfaces;

import balancebite.dto.user.UserRegistrationInputDTO;

import java.util.Map;

/**
 * Interface for user registration services.
 * Defines methods for user registration and related operations.
 */
public interface IRegistrationService {

    /**
     * Registers a new user using the provided registration details and returns generated tokens.
     *
     * @param registrationDTO The DTO containing user registration details.
     * @return A map containing access and refresh tokens.
     */
    Map<String, String> registerUser(UserRegistrationInputDTO registrationDTO);
}

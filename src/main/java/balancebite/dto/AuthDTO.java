package balancebite.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * AuthDTO is a Data Transfer Object for authentication requests.
 * It contains the necessary information for user authentication.
 */
public class AuthDTO {

    /**
     * The email of the user.
     * This field is mandatory and cannot be blank.
     */
    @NotBlank(message = "Email is mandatory")
    public String email;

    /**
     * The password of the user.
     * This field is mandatory and cannot be blank.
     */
    @NotBlank(message = "Password is mandatory")
    public String password;
}

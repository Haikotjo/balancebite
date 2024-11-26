package balancebite.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for handling user login input.
 *
 * This DTO is specifically designed for login requests, collecting and validating
 * only the necessary fields for user authentication.
 */
public class UserLoginInputDTO {

    /**
     * The user's email address.
     * <p>
     * This field is mandatory and must follow a valid email format.
     */
    @NotBlank(message = "The email cannot be blank. Please provide a valid email address.")
    @Email(message = "Please provide a valid email address.")
    private String email;

    /**
     * The user's password.
     * <p>
     * This field is mandatory and must be at least 4 characters long.
     */
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])[A-Za-z\\d@#$%^&+=]{5,}$",
            message = "Password must be at least 5 characters long, include an uppercase letter, a lowercase letter, and a special character.")
    private String password;

    /**
     * Default no-argument constructor for frameworks that require it.
     */
    public UserLoginInputDTO() {}

    /**
     * Constructor for creating a UserLoginInputDTO with all fields.
     *
     * @param email    The user's email address. Must not be blank and must follow a valid email format.
     * @param password The user's password. Must not be blank and must be at least 4 characters long.
     */
    public UserLoginInputDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    /**
     * Gets the user's email address.
     *
     * @return The user's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email The user's email address. Must not be blank and must follow a valid email format.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's password.
     *
     * @return The user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password The user's password. Must not be blank and must be at least 4 characters long.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}

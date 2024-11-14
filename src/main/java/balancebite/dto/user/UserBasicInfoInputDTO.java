package balancebite.dto.user;

import balancebite.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for handling basic user information.
 *
 * This DTO is used to collect and validate basic user information during user registration or updates.
 * It includes fields for the user's name, email, password, and role, with appropriate validation constraints.
 */
public class UserBasicInfoInputDTO {

    /**
     * The user's name. This field is mandatory and must be between 2 and 50 characters long.
     */
    @NotBlank(message = "The user name cannot be blank. Please provide a valid name.")
    @Size(min = 2, max = 50, message = "User name must be between 2 and 50 characters.")
    private String userName;

    /**
     * The user's email address. This field is mandatory and must follow a valid email format.
     */
    @NotBlank(message = "The email cannot be blank. Please provide a valid email address.")
    @Email(message = "Please provide a valid email address.")
    private String email;

    /**
     * The user's password. This field is mandatory and must be at least 4 characters long.
     */
    @NotBlank(message = "The password cannot be blank. Please provide a valid password.")
    @Size(min = 4, message = "Password must be at least 4 characters long.")
    private String password;

    /**
     * The role assigned to the user. This field is mandatory and must match a valid Role enum value.
     */
    @NotNull(message = "Role must be provided. Please select a valid role for the user.")
    private Role role;

    /**
     * Default no-argument constructor for frameworks that require it.
     */
    public UserBasicInfoInputDTO() {}

    /**
     * Constructor for creating a UserBasicInfoInputDTO with all fields.
     *
     * @param userName The user's name. Must not be blank and must have 2-50 characters.
     * @param email    The user's email address. Must not be blank and must be a valid email format.
     * @param password The user's password. Must not be blank and must be at least 4 characters long.
     * @param role     The user's role. Must not be null and must match a valid Role enum value.
     */
    public UserBasicInfoInputDTO(
            @NotBlank(message = "The user name cannot be blank.") @Size(min = 2, max = 50) String userName,
            @NotBlank(message = "The email cannot be blank.") @Email(message = "Please provide a valid email address.") String email,
            @NotBlank(message = "The password cannot be blank.") @Size(min = 4, message = "Password must be at least 4 characters long.") String password,
            @NotNull(message = "Role must be provided.") Role role) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and setters

    /**
     * Gets the user's name.
     *
     * @return The user's name.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the user's name.
     *
     * @param userName The user's name. Must not be blank and must have 2-50 characters.
     */
    public void setUserName(String userName) {
        this.userName = userName;
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

    /**
     * Gets the user's role.
     *
     * @return The user's role.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the user's role.
     *
     * @param role The user's role. Must not be null and must match a valid Role enum value.
     */
    public void setRole(Role role) {
        this.role = role;
    }
}

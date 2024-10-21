package balancebite.dto.user;

import balancebite.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for handling basic user information.
 *
 * This DTO is used to receive basic user information, such as username, email, password, and role,
 * during user creation or updates. It includes validation constraints to ensure the provided data is correct.
 */
public class UserBasicInfoInputDTO {

    /**
     * The user's name. Must be between 2 and 50 characters long and cannot be blank.
     */
    @NotBlank(message = "The user name cannot be blank. Please provide a valid name.")
    @Size(min = 2, max = 50, message = "User name must be between 2 and 50 characters.")
    private String userName;

    /**
     * The user's email address. Must be a valid email format and cannot be blank.
     */
    @NotBlank(message = "The email cannot be blank. Please provide a valid email address.")
    @Email(message = "Please provide a valid email address.")
    private String email;

    /**
     * The user's password. Must be at least 4 characters long and cannot be blank.
     */
    @NotBlank(message = "The password cannot be blank. Please provide a valid password.")
    @Size(min = 4, message = "Password must be at least 4 characters long.")
    private String password;

    /**
     * The role assigned to the user. This field cannot be null.
     */
    @NotNull(message = "Role must be provided.")
    private Role role;

    /**
     * Default constructor for UserBasicInfoInputDTO.
     */
    public UserBasicInfoInputDTO() {}

    /**
     * Constructor for creating a user with basic information.
     *
     * @param userName The user's name.
     * @param email The user's email address.
     * @param password The user's password.
     * @param role The user's role.
     */
    public UserBasicInfoInputDTO(String userName, String email, String password, Role role) {
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
     * @param userName The user's name.
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
     * @param email The user's email address.
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
     * @param password The user's password.
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
     * @param role The user's role.
     */
    public void setRole(Role role) {
        this.role = role;
    }
}

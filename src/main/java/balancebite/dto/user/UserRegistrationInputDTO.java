package balancebite.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Collection;

/**
 * Data Transfer Object (DTO) for handling user registration input.
 *
 * This DTO is specifically designed for user registration requests, collecting
 * and validating the necessary fields for creating a new user, including optional
 * elevated role assignments.
 */
public class UserRegistrationInputDTO {

    /**
     * The user's name.
     * <p>
     * This field is optional. If not provided, it defaults to the email address.
     */
    @Size(min = 2, max = 50, message = "User name must be between 2 and 50 characters.")
    private String userName;

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
     * The roles assigned to the user.
     * <p>
     * This field can contain one or more roles, such as "USER", "ADMIN", or "CHEF".
     * If no roles are provided, the default role is "USER".
     */
    private Collection<String> roles;

    /**
     * Optional token for assigning elevated roles (e.g., ADMIN, CHEF).
     * <p>
     * This token is used during registration to verify if the user is eligible
     * for elevated roles. If not provided, the default role is USER.
     */
    private String verificationToken;

    /**
     * Default no-argument constructor for frameworks that require it.
     */
    public UserRegistrationInputDTO() {}

    /**
     * Constructor for creating a UserRegistrationInputDTO with all fields.
     *
     * @param userName           The user's name. If null or blank, it will default to the email.
     * @param email              The user's email address. Must not be blank and must be a valid email format.
     * @param password           The user's password. Must not be blank and must be at least 4 characters long.
     * @param roles              The roles assigned to the user as strings. This field is optional.
     * @param verificationToken  Optional token used to validate elevated roles (e.g., ADMIN, CHEF).
     */
    public UserRegistrationInputDTO(String userName, String email, String password, Collection<String> roles, String verificationToken) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.verificationToken = verificationToken;
    }

    /**
     * Gets the user's name.
     * If the userName is null or blank, it defaults to the email.
     *
     * @return The user's name, or email if userName is not provided.
     */
    public String getUserName() {
        return (userName == null || userName.isBlank()) ? email : userName;
    }

    /**
     * Sets the user's name.
     *
     * @param userName The user's name. Must have 2-50 characters if provided.
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
     * Gets the collection of roles assigned to the user as strings.
     *
     * @return A collection of roles assigned to the user as strings.
     */
    public Collection<String> getRoles() {
        return roles;
    }

    /**
     * Sets the collection of roles assigned to the user as strings.
     * This method replaces all existing roles with the provided collection of roles.
     *
     * @param roles A collection of roles to assign to the user as strings. This field is optional.
     */
    public void setRoles(Collection<String> roles) {
        this.roles = roles;
    }

    /**
     * Gets the verification token provided during user creation.
     *
     * @return The verification token, or null if not provided.
     */
    public String getVerificationToken() {
        return verificationToken;
    }

    /**
     * Sets the verification token for user creation.
     *
     * @param verificationToken The verification token.
     */
    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }
}

package balancebite.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO for updating user roles based on email.
 * Intended for use by admins.
 */
public class UserRoleUpdateDTO {

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Roles cannot be null")
    @NotEmpty(message = "At least one role must be specified")
    private List<String> roles;

    // Constructors
    public UserRoleUpdateDTO() {}

    public UserRoleUpdateDTO(String email, List<String> roles) {
        this.email = email;
        this.roles = roles;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}

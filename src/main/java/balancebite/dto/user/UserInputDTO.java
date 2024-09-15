package balancebite.dto.user;

import balancebite.model.Role;

/**
 * Data Transfer Object (DTO) for creating or updating a user.
 * This DTO is used to receive user data from the client when creating or updating a user.
 */
public class UserInputDTO {

    /**
     * The name of the user.
     */
    private String name;

    /**
     * The email of the user.
     */
    private String email;

    /**
     * The password of the user. This should be hashed before storing in the database.
     */
    private String password;

    /**
     * The role of the user.
     */
    private Role role;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

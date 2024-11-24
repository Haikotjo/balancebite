package balancebite.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Collection;

/**
 * Represents a role entity in the system.
 */
@Entity
@Table(name = "roles")
public class Role {

    /**
     * The unique identifier for the role.
     * The role name is an enumerated type.
     */
    @Id
    @Enumerated(EnumType.STRING)
    private UserRole rolename;

    /**
     * The collection of users that have this role.
     * This establishes a many-to-many relationship with the User entity.
     */
    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;

    // Constructor

    /**
     * Default constructor.
     */
    public Role() {
    }

    /**
     * Constructor used for creating a role with a specified name.
     *
     * @param rolename the name of the role
     */
    public Role(UserRole rolename) {
        this.rolename = rolename;
    }

    // Getters and Setters

    /**
     * Gets the name of the role.
     *
     * @return the name of the role
     */
    public UserRole getRolename() {
        return rolename;
    }

    /**
     * Sets the name of the role.
     *
     * @param rolename the name of the role
     */
    public void setRolename(UserRole rolename) {
        this.rolename = rolename;
    }

    /**
     * Gets the name of the role as a string.
     * This method is annotated with @JsonIgnore to prevent it from being serialized.
     *
     * @return the name of the role as a string
     */
    @JsonIgnore
    public String getRoleName() {
        return rolename.name();
    }
}

package balancebite.model;

import jakarta.persistence.*;
import java.util.Set;

/**
 * Represents a user entity in the system.
 * Each user has a unique ID, name, email, and password, and can be associated with multiple meals.
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * The unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the user.
     */
    private String name;

    /**
     * The email of the user.
     */
    private String email;

    /**
     * The password for the user account.
     */
    private String password;

    /**
     * Many-to-Many relationship between users and meals.
     * A user can be associated with multiple meals, and meals can belong to multiple users.
     */
    @ManyToMany
    @JoinTable(
            name = "user_meals",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "meal_id")
    )
    private Set<Meal> meals;

    /**
     * Default constructor for JPA.
     */
    public User() {
        // Default constructor for JPA
    }

    /**
     * Full constructor for creating a User entity.
     *
     * @param id       The unique identifier for the user.
     * @param name     The name of the user.
     * @param email    The email of the user.
     * @param password The password for the user account.
     * @param meals    The set of meals associated with the user.
     */
    public User(Long id, String name, String email, String password, Set<Meal> meals) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.meals = meals;
    }

    /**
     * Gets the unique identifier for the user.
     *
     * @return The ID of the user.
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the name of the user.
     *
     * @return The name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param name The name to set for the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the email of the user.
     *
     * @return The email of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     *
     * @param email The email to set for the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the password for the user account.
     *
     * @return The password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for the user account.
     *
     * @param password The password to set for the user.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the set of meals associated with the user.
     *
     * @return A set of meals.
     */
    public Set<Meal> getMeals() {
        return meals;
    }

    /**
     * Sets the meals associated with the user.
     *
     * @param meals The set of meals to associate with the user.
     */
    public void setMeals(Set<Meal> meals) {
        this.meals = meals;
    }
}

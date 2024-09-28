package balancebite.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user entity in the system.
 * Each user has a unique ID, name, email, and password, and can be associated with multiple meals and a role.
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
    private Set<Meal> meals = new HashSet<>(); // Initialize with an empty set

    /**
     * The role of the user, which defines their permissions and access levels.
     * This is represented as an enum with values like USER and ADMIN.
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * The recommended daily intake associated with the user.
     * This represents the daily nutrient intake goals or limits for the user,
     * including values for macronutrients, vitamins, minerals, and other nutritional components.
     * The relationship is one-to-one, meaning each user has their own personalized daily intake.
     * The daily intake is automatically persisted and managed when the user is saved or updated.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "recommended_daily_intake_id", referencedColumnName = "id")
    private RecommendedDailyIntake recommendedDailyIntake;

    /**
     * Default no-argument constructor for User entity.
     */
    public User() {
        // Default constructor for JPA
    }

    /**
     * Full constructor for creating a User entity without an ID.
     * The ID is automatically generated when the entity is persisted.
     *
     * @param name     The name of the user.
     * @param email    The email of the user.
     * @param password The password for the user account.
     * @param meals    The set of meals associated with the user.
     * @param role     The role of the user.
     * @param recommendedDailyIntake The recommended daily intake of the user.
     */
    public User(String name, String email, String password, Set<Meal> meals, Role role, RecommendedDailyIntake recommendedDailyIntake) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.meals = meals != null ? meals : new HashSet<>();
        this.role = role;
        this.recommendedDailyIntake = recommendedDailyIntake;
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

    public void setMeals(Set<Meal> meals) {
        this.meals = meals != null ? meals : new HashSet<>();
    }

    /**
     * Gets the role of the user.
     *
     * @return The role of the user.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the role of the user.
     *
     * @param role The role to set for the user.
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Gets the recommended daily intake associated with the user.
     *
     * @return The recommended daily intake of the user.
     */
    public RecommendedDailyIntake getRecommendedDailyIntake() {
        return recommendedDailyIntake;
    }

    /**
     * Sets the recommended daily intake for the user.
     *
     * @param recommendedDailyIntake The recommended daily intake to set for the user.
     */
    public void setRecommendedDailyIntake(RecommendedDailyIntake recommendedDailyIntake) {
        this.recommendedDailyIntake = recommendedDailyIntake;
    }

}

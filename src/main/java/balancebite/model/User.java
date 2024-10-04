package balancebite.model;

import balancebite.model.userenums.ActivityLevel;
import balancebite.model.userenums.Gender;
import balancebite.model.userenums.Goal;
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
    @Column(name = "user_name")
    private String userName;

    /**
     * The email of the user.
     */
    private String email;

    /**
     * The password for the user account.
     */
    private String password;

    /**
     * The weight of the user in kilograms.
     */
    private Double weight;

    /**
     * The age of the user in years.
     */
    private Integer age;

    /**
     * The height of the user in centimeters.
     */
    private Double height;

    /**
     * The gender of the user.
     */
    @Enumerated(EnumType.STRING)
    private Gender gender;

    /**
     * The activity level of the user, which defines how active they are.
     */
    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;

    /**
     * The goal of the user, defining whether they want to lose, gain, or maintain weight.
     */
    @Enumerated(EnumType.STRING)
    private Goal goal;

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
     * The relationship is one-to-one, meaning each user has their own personalized daily intake.
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
     * Full constructor for creating a User entity without mandatory fields for weight, age, height, gender, activityLevel, and goal.
     *
     * This allows for creating a user without requiring all these fields immediately.
     *
     * @param userName     The name of the user.
     * @param email    The email of the user.
     * @param password The password for the user account.
     * @param role     The role of the user.
     */
    public User(String userName, String email, String password, Role role) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
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
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the name of the user.
     *
     * @param userName  The name to set for the user.
     */
    public void setUserName(String userName) {
        this.userName = userName;
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
     * Gets the weight of the user in kilograms.
     *
     * @return The weight of the user.
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Sets the weight of the user in kilograms.
     *
     * @param weight The weight to set for the user.
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     * Gets the age of the user in years.
     *
     * @return The age of the user.
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Sets the age of the user in years.
     *
     * @param age The age to set for the user.
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * Gets the height of the user in centimeters.
     *
     * @return The height of the user.
     */
    public Double getHeight() {
        return height;
    }

    /**
     * Sets the height of the user in centimeters.
     *
     * @param height The height to set for the user.
     */
    public void setHeight(Double height) {
        this.height = height;
    }

    /**
     * Gets the gender of the user.
     *
     * @return The gender of the user.
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Sets the gender of the user.
     *
     * @param gender The gender to set for the user.
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * Gets the activity level of the user.
     *
     * @return The activity level of the user.
     */
    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    /**
     * Sets the activity level of the user.
     *
     * @param activityLevel The activity level to set for the user.
     */
    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    /**
     * Gets the goal of the user (weight loss, weight gain, or maintenance).
     *
     * @return The goal of the user.
     */
    public Goal getGoal() {
        return goal;
    }

    /**
     * Sets the goal of the user.
     *
     * @param goal The goal to set for the user.
     */
    public void setGoal(Goal goal) {
        this.goal = goal;
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

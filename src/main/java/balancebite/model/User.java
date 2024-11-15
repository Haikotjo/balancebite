package balancebite.model;

import balancebite.model.userenums.ActivityLevel;
import balancebite.model.userenums.Gender;
import balancebite.model.userenums.Goal;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user entity in the system.
 * Each user has a unique ID, name, email, and password, and can be associated with multiple meals and roles.
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the user.
     */
    @Column(name = "user_name", nullable = false)
    private String userName;

    /**
     * The unique email of the user.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * The password of the user.
     */
    @Column(nullable = false)
    private String password;

    /**
     * The weight of the user.
     */
    private Double weight;

    /**
     * The age of the user, stored as an integer.
     */
    private Integer age;

    /**
     * The height of the user in centimeters.
     */
    private Double height;

    /**
     * The gender of the user, stored as a string representation of the Gender enum.
     */
    @Enumerated(EnumType.STRING)
    private Gender gender;

    /**
     * The activity level of the user, stored as a string representation of the ActivityLevel enum.
     */
    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;

    /**
     * The goal of the user, stored as a string representation of the Goal enum.
     */
    @Enumerated(EnumType.STRING)
    private Goal goal;

    /**
     * The set of meals associated with the user.
     * This is a many-to-many relationship stored in the "user_meals" table.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_meals",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "meal_id")
    )
    private Set<Meal> meals = new HashSet<>();

    /**
     * The role of the user, stored as a string representation of the Role enum.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * The recommended daily intakes associated with the user.
     * This is a one-to-many relationship where the user is the parent.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<RecommendedDailyIntake> recommendedDailyIntakes = new HashSet<>();

    /**
     * Default constructor for JPA.
     */
    public User() {}

    /**
     * Constructor to initialize a User with basic attributes.
     *
     * @param userName the name of the user.
     * @param email    the email of the user.
     * @param password the password of the user.
     * @param role     the role of the user.
     */
    public User(String userName, String email, String password, Role role) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * Gets the unique identifier of the user.
     *
     * @return the ID of the user.
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the name of the user.
     *
     * @return the name of the user.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the name of the user.
     *
     * @param userName the name of the user.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Gets the email of the user.
     *
     * @return the email of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     *
     * @param email the email of the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the password of the user.
     *
     * @return the password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password the password of the user.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the weight of the user.
     *
     * @return the weight of the user.
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Sets the weight of the user.
     *
     * @param weight the weight of the user.
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     * Gets the age of the user.
     *
     * @return the age of the user.
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Sets the age of the user.
     *
     * @param age the age of the user.
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * Gets the height of the user in centimeters.
     *
     * @return the height of the user.
     */
    public Double getHeight() {
        return height;
    }

    /**
     * Sets the height of the user in centimeters.
     *
     * @param height the height of the user.
     */
    public void setHeight(Double height) {
        this.height = height;
    }

    /**
     * Gets the gender of the user.
     *
     * @return the gender of the user.
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Sets the gender of the user.
     *
     * @param gender the gender of the user.
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * Gets the activity level of the user.
     *
     * @return the activity level of the user.
     */
    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    /**
     * Sets the activity level of the user.
     *
     * @param activityLevel the activity level of the user.
     */
    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    /**
     * Gets the goal of the user.
     *
     * @return the goal of the user.
     */
    public Goal getGoal() {
        return goal;
    }

    /**
     * Sets the goal of the user.
     *
     * @param goal the goal of the user.
     */
    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    /**
     * Gets the set of meals associated with the user.
     *
     * @return the set of meals.
     */
    public Set<Meal> getMeals() {
        return meals;
    }

    /**
     * Sets the set of meals associated with the user.
     *
     * @param meals the set of meals.
     */
    public void setMeals(Set<Meal> meals) {
        this.meals = meals != null ? meals : new HashSet<>();
    }

    /**
     * Gets the role of the user.
     *
     * @return the role of the user.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the role of the user.
     *
     * @param role the role of the user.
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Gets the set of recommended daily intakes associated with the user.
     *
     * @return the set of recommended daily intakes.
     */
    public Set<RecommendedDailyIntake> getRecommendedDailyIntakes() {
        return recommendedDailyIntakes;
    }

    /**
     * Sets the set of recommended daily intakes associated with the user.
     *
     * @param recommendedDailyIntakes the set of recommended daily intakes.
     */
    public void setRecommendedDailyIntakes(Set<RecommendedDailyIntake> recommendedDailyIntakes) {
        this.recommendedDailyIntakes = recommendedDailyIntakes != null ? recommendedDailyIntakes : new HashSet<>();
    }

    /**
     * Adds a meal to the user's set of meals and increments the user count for the meal.
     *
     * @param meal the meal to be added.
     */
    public void addMeal(Meal meal) {
        this.meals.add(meal);
        meal.incrementUserCount();
    }

    /**
     * Removes a meal from the user's set of meals and decrements the user count for the meal.
     *
     * @param meal the meal to be removed.
     */
    public void removeMeal(Meal meal) {
        this.meals.remove(meal);
        meal.decrementUserCount();
    }

    /**
     * Adds a recommended daily intake to the user.
     * This method maintains bidirectional consistency.
     *
     * @param rdi the recommended daily intake to be added.
     */
    public void addRecommendedDailyIntake(RecommendedDailyIntake rdi) {
        this.recommendedDailyIntakes.add(rdi);
        rdi.setUser(this);
    }

    /**
     * Removes a recommended daily intake from the user.
     * This method ensures bidirectional consistency.
     *
     * @param rdi the recommended daily intake to be removed.
     */
    public void removeRecommendedDailyIntake(RecommendedDailyIntake rdi) {
        this.recommendedDailyIntakes.remove(rdi);
        rdi.setUser(null);
    }
}

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private Double weight;

    private Integer age;

    private Double height;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    private Goal goal;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_meals",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "meal_id")
    )
    private Set<Meal> meals = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<RecommendedDailyIntake> recommendedDailyIntakes = new HashSet<>();

    public User() {
        // Default constructor for JPA
    }

    public User(String userName, String email, String password, Role role) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public Set<Meal> getMeals() {
        return meals;
    }

    public void setMeals(Set<Meal> meals) {
        this.meals = meals != null ? meals : new HashSet<>();
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<RecommendedDailyIntake> getRecommendedDailyIntakes() {
        return recommendedDailyIntakes;
    }

    public void setRecommendedDailyIntakes(Set<RecommendedDailyIntake> recommendedDailyIntakes) {
        this.recommendedDailyIntakes = recommendedDailyIntakes != null ? recommendedDailyIntakes : new HashSet<>();
    }

    /**
     * Adds a meal to the user's set of meals.
     * This method ensures consistency in the association between User and Meal.
     *
     * @param meal the meal to be added
     */
    public void addMeal(Meal meal) {
        this.meals.add(meal);
        meal.getUsers().add(this);
    }

    /**
     * Removes a meal from the user's set of meals.
     * This method ensures consistency in the association between User and Meal.
     *
     * @param meal the meal to be removed
     */
    public void removeMeal(Meal meal) {
        this.meals.remove(meal);
        meal.getUsers().remove(this);
    }

    /**
     * Adds a recommended daily intake to the user.
     * This method maintains bidirectional consistency.
     *
     * @param rdi the recommended daily intake to be added
     */
    public void addRecommendedDailyIntake(RecommendedDailyIntake rdi) {
        this.recommendedDailyIntakes.add(rdi);
        rdi.setUser(this);
    }

    /**
     * Removes a recommended daily intake from the user.
     * This method ensures bidirectional consistency.
     *
     * @param rdi the recommended daily intake to be removed
     */
    public void removeRecommendedDailyIntake(RecommendedDailyIntake rdi) {
        this.recommendedDailyIntakes.remove(rdi);
        rdi.setUser(null);
    }
}

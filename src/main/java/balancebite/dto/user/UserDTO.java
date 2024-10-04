package balancebite.dto.user;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.model.Role;
import balancebite.model.userenums.ActivityLevel;
import balancebite.model.userenums.Gender;
import balancebite.model.userenums.Goal;

import java.util.List;

/**
 * Data Transfer Object (DTO) for User.
 * This DTO is used to send user data back to the client.
 */
public class UserDTO {

    /**
     * The unique identifier of the user.
     */
    private Long id;

    /**
     * The name of the user.
     */
    private String userName;

    /**
     * The email of the user.
     */
    private String email;

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
    private Gender gender;

    /**
     * The activity level of the user.
     */
    private ActivityLevel activityLevel;

    /**
     * The goal of the user (weight loss, weight gain, or maintenance).
     */
    private Goal goal;

    /**
     * A list of meals associated with the user.
     * This contains basic meal information (e.g., ID and name).
     */
    private List<MealDTO> meals;

    /**
     * The role of the user.
     */
    private Role role;

    /**
     * The recommended daily intake associated with the user.
     * This represents the user's daily nutrient intake goals or limits.
     */
    private RecommendedDailyIntakeDTO recommendedDailyIntake;

    // Constructors

    /**
     * Default no-argument constructor for UserDTO.
     * Used by frameworks like Hibernate or Jackson.
     */
    public UserDTO() {
        // Default constructor
    }

    /**
     * Constructor for creating a UserDTO with all fields.
     *
     * @param id                     the ID of the user.
     * @param userName                   the name of the user.
     * @param email                  the email of the user.
     * @param weight                 the weight of the user.
     * @param age                    the age of the user.
     * @param height                 the height of the user.
     * @param gender                 the gender of the user.
     * @param activityLevel          the activity level of the user.
     * @param goal                   the goal of the user.
     * @param meals                  the list of meals associated with the user.
     * @param role                   the role of the user.
     * @param recommendedDailyIntake the recommended daily intake of the user.
     */
    public UserDTO(Long id, String userName, String email, Double weight, Integer age, Double height, Gender gender,
                   ActivityLevel activityLevel, Goal goal, List<MealDTO> meals, Role role,
                   RecommendedDailyIntakeDTO recommendedDailyIntake) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.weight = weight;
        this.age = age;
        this.height = height;
        this.gender = gender;
        this.activityLevel = activityLevel;
        this.goal = goal;
        this.meals = meals;
        this.role = role;
        this.recommendedDailyIntake = recommendedDailyIntake;
    }

    /**
     * Constructor for creating a UserDTO with only basic user information (ID, name, and email).
     * This constructor is specifically used in the MealMapper to associate users with meals
     * without requiring the full user details (such as role or recommended daily intake).
     *
     * @param id    The unique identifier of the user.
     * @param userName  The name of the user.
     * @param email The email of the user.
     */
    public UserDTO(Long id, String userName, String email) {
        this.id = id;
        this.userName = userName;
        this.email = email;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<MealDTO> getMeals() {
        return meals;
    }

    public void setMeals(List<MealDTO> meals) {
        this.meals = meals;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public RecommendedDailyIntakeDTO getRecommendedDailyIntake() {
        return recommendedDailyIntake;
    }

    public void setRecommendedDailyIntake(RecommendedDailyIntakeDTO recommendedDailyIntake) {
        this.recommendedDailyIntake = recommendedDailyIntake;
    }
}

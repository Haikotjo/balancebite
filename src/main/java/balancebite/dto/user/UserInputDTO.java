package balancebite.dto.user;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.model.Role;
import balancebite.model.userenums.ActivityLevel;
import balancebite.model.userenums.Gender;
import balancebite.model.userenums.Goal;

/**
 * Data Transfer Object (DTO) for creating or updating a user.
 * This DTO is used to receive user data from the client during user creation or update.
 */
public class UserInputDTO {

    /**
     * The name of the user.
     */
    private String userName;

    /**
     * The email of the user.
     */
    private String email;

    /**
     * The password of the user.
     * This must be hashed before storing in the database.
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
     * The role of the user (e.g., USER or ADMIN).
     */
    private Role role;

    /**
     * The recommended daily intake of the user.
     * This represents the user's personalized nutritional goals, such as macronutrients and micronutrients.
     */
    private RecommendedDailyIntakeDTO recommendedDailyIntake;

    // Getters and setters

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

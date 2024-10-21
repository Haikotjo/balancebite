package balancebite.dto.user;

import balancebite.model.userenums.ActivityLevel;
import balancebite.model.userenums.Gender;
import balancebite.model.userenums.Goal;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for updating user details.
 * This DTO is used to receive user-specific data from the client during updates to an existing user.
 */
public class UserDetailsInputDTO {

    @NotNull(message = "Weight must be provided.")
    private Double weight;

    @NotNull(message = "Age must be provided.")
    private Integer age;

    @NotNull(message = "Height must be provided.")
    private Double height;

    @NotNull(message = "Gender must be provided.")
    private Gender gender;

    @NotNull(message = "Activity level must be provided.")
    private ActivityLevel activityLevel;

    @NotNull(message = "Goal must be provided.")
    private Goal goal;

    /**
     * Default constructor for UserDetailsInputDTO.
     * This constructor is necessary for frameworks that require a no-argument constructor.
     */
    public UserDetailsInputDTO() {}

    /**
     * Full constructor for creating a UserDetailsInputDTO with all fields.
     *
     * @param weight The user's weight.
     * @param age The user's age.
     * @param height The user's height.
     * @param gender The user's gender.
     * @param activityLevel The user's activity level.
     * @param goal The user's goal.
     */
    public UserDetailsInputDTO(Double weight, Integer age, Double height, Gender gender, ActivityLevel activityLevel, Goal goal) {
        this.weight = weight;
        this.age = age;
        this.height = height;
        this.gender = gender;
        this.activityLevel = activityLevel;
        this.goal = goal;
    }

    /**
     * Gets the user's weight.
     *
     * @return The user's weight.
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Sets the user's weight.
     *
     * @param weight The user's weight. Must not be null.
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     * Gets the user's age.
     *
     * @return The user's age.
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Sets the user's age.
     *
     * @param age The user's age. Must not be null.
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * Gets the user's height.
     *
     * @return The user's height.
     */
    public Double getHeight() {
        return height;
    }

    /**
     * Sets the user's height.
     *
     * @param height The user's height. Must not be null.
     */
    public void setHeight(Double height) {
        this.height = height;
    }

    /**
     * Gets the user's gender.
     *
     * @return The user's gender.
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Sets the user's gender.
     *
     * @param gender The user's gender. Must not be null.
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * Gets the user's activity level.
     *
     * @return The user's activity level.
     */
    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    /**
     * Sets the user's activity level.
     *
     * @param activityLevel The user's activity level. Must not be null.
     */
    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    /**
     * Gets the user's goal.
     *
     * @return The user's goal.
     */
    public Goal getGoal() {
        return goal;
    }

    /**
     * Sets the user's goal.
     *
     * @param goal The user's goal. Must not be null.
     */
    public void setGoal(Goal goal) {
        this.goal = goal;
    }
}

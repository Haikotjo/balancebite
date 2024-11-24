package balancebite.dto.user;

import balancebite.model.user.userenums.ActivityLevel;
import balancebite.model.user.userenums.Gender;
import balancebite.model.user.userenums.Goal;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) for updating user details.
 * This DTO is used to receive user-specific data from the client during updates to an existing user.
 */
public class UserDetailsInputDTO {

    /**
     * The user's weight, in kilograms. This field is mandatory and must have a maximum of 5 digits with 2 decimal places.
     */
    @NotNull(message = "Weight must be provided. Please specify your weight in kilograms.")
    @Digits(integer = 3, fraction = 2, message = "Weight must be a valid number with up to 3 digits and 2 decimal places.")
    private Double weight;

    /**
     * The user's age, in years. This field is mandatory and must be an integer.
     */
    @NotNull(message = "Age must be provided. Please specify your age in years.")
    @Max(value = 99, message = "Age cannot be more than 99 years.")
    private Integer age;

    /**
     * The user's height, in centimeters. This field is mandatory and must have a maximum of 5 digits with 1 decimal place.
     */
    @NotNull(message = "Height must be provided. Please specify your height in centimeters.")
    @Digits(integer = 3, fraction = 1, message = "Height must be a valid number with up to 3 digits and 1 decimal place.")
    private Double height;

    /**
     * The user's gender. This field is mandatory and must match a valid gender enum value.
     */
    @NotNull(message = "Gender must be provided. Please select a valid gender option.")
    private Gender gender;

    /**
     * The user's activity level. This field is mandatory and must match a valid activity level enum value.
     */
    @NotNull(message = "Activity level must be provided. Please select your activity level.")
    private ActivityLevel activityLevel;

    /**
     * The user's goal. This field is mandatory and must match a valid goal enum value.
     */
    @NotNull(message = "Goal must be provided. Please specify your health or fitness goal.")
    private Goal goal;

    /**
     * Default constructor for frameworks that require a no-argument constructor.
     */
    public UserDetailsInputDTO() {}

    /**
     * Full constructor for creating a UserDetailsInputDTO with all fields.
     *
     * @param weight         The user's weight in kilograms. Cannot be null.
     * @param age            The user's age in years. Cannot be null.
     * @param height         The user's height in centimeters. Cannot be null.
     * @param gender         The user's gender. Cannot be null.
     * @param activityLevel  The user's activity level. Cannot be null.
     * @param goal           The user's goal. Cannot be null.
     */
    public UserDetailsInputDTO(
            @NotNull(message = "Weight must be provided.") Double weight,
            @NotNull(message = "Age must be provided.") Integer age,
            @NotNull(message = "Height must be provided.") Double height,
            @NotNull(message = "Gender must be provided.") Gender gender,
            @NotNull(message = "Activity level must be provided.") ActivityLevel activityLevel,
            @NotNull(message = "Goal must be provided.") Goal goal
    ) {
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
     * @return The user's weight in kilograms.
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Sets the user's weight.
     *
     * @param weight The user's weight in kilograms. Must not be null.
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     * Gets the user's age.
     *
     * @return The user's age in years.
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Sets the user's age.
     *
     * @param age The user's age in years. Must not be null.
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * Gets the user's height.
     *
     * @return The user's height in centimeters.
     */
    public Double getHeight() {
        return height;
    }

    /**
     * Sets the user's height.
     *
     * @param height The user's height in centimeters. Must not be null.
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

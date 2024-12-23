package balancebite.dto.user;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.model.user.Role;
import balancebite.model.user.userenums.ActivityLevel;
import balancebite.model.user.userenums.Gender;
import balancebite.model.user.userenums.Goal;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Data Transfer Object (DTO) for User.
 * This DTO is used to transfer user data between the application layers and send it back to the client.
 * It encapsulates all the necessary information about a user, including their profile details, associated meals,
 * roles, and recommended daily intake.
 */
public class UserDTO {

    /**
     * The unique identifier of the user.
     */
    private final Long id;

    /**
     * The user's name.
     */
    private final String userName;

    /**
     * The user's email address.
     */
    private final String email;

    /**
     * The user's weight in kilograms.
     */
    private final Double weight;

    /**
     * The user's age in years.
     */
    private final Integer age;

    /**
     * The user's height in centimeters.
     */
    private final Double height;

    /**
     * The user's gender.
     */
    private final Gender gender;

    /**
     * The user's activity level.
     */
    private final ActivityLevel activityLevel;

    /**
     * The user's goal (e.g., weight loss, muscle gain).
     */
    private final Goal goal;

    /**
     * The list of meals associated with the user.
     */
    private final List<MealDTO> meals;

    /**
     * The roles assigned to the user (e.g., ADMIN, USER).
     */
    private final Collection<Role> roles;

    /**
     * The list of recommended daily intakes associated with the user.
     */
    private final List<RecommendedDailyIntakeDTO> recommendedDailyIntakes;

    /**
     * Constructor for creating a UserDTO with all fields.
     * This is typically used when retrieving a full user profile with detailed information.
     *
     * @param id                     The unique identifier of the user.
     * @param userName               The user's name.
     * @param email                  The user's email address.
     * @param weight                 The user's weight in kilograms.
     * @param age                    The user's age in years.
     * @param height                 The user's height in centimeters.
     * @param gender                 The user's gender.
     * @param activityLevel          The user's activity level.
     * @param goal                   The user's goal.
     * @param meals                  The list of meals associated with the user.
     * @param roles                  The roles assigned to the user.
     * @param recommendedDailyIntakes The list of recommended daily intakes for the user.
     */
    public UserDTO(Long id, String userName, String email, Double weight, Integer age, Double height, Gender gender,
                   ActivityLevel activityLevel, Goal goal, List<MealDTO> meals, Collection<Role> roles,
                   List<RecommendedDailyIntakeDTO> recommendedDailyIntakes) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.weight = weight;
        this.age = age;
        this.height = height;
        this.gender = gender;
        this.activityLevel = activityLevel;
        this.goal = goal;
        this.meals = (meals != null) ? List.copyOf(meals) : List.of(); // Ensure immutability
        this.roles = (roles != null) ? List.copyOf(roles) : List.of(); // Ensure immutability
        this.recommendedDailyIntakes = (recommendedDailyIntakes != null) ? List.copyOf(recommendedDailyIntakes) : List.of(); // Ensure immutability
    }

    /**
     * Constructor for creating a UserDTO with only basic user information (ID, name, and email).
     * This is specifically used in lightweight contexts such as associating users with meals
     * without requiring full user details.
     *
     * @param id       The unique identifier of the user.
     * @param userName The name of the user.
     * @param email    The email of the user.
     */
    public UserDTO(Long id, String userName, String email) {
        this(id, userName, email, null, null, null, null, null, null, List.of(), List.of(), List.of());
    }

    /**
     * Gets the unique identifier of the user.
     *
     * @return The user's ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the user's name.
     *
     * @return The user's name.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets the user's email address.
     *
     * @return The user's email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the user's weight in kilograms.
     *
     * @return The user's weight, or null if not specified.
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Gets the user's age in years.
     *
     * @return The user's age, or null if not specified.
     */
    public Integer getAge() {
        return age;
    }

    /**
     * Gets the user's height in centimeters.
     *
     * @return The user's height, or null if not specified.
     */
    public Double getHeight() {
        return height;
    }

    /**
     * Gets the user's gender.
     *
     * @return The user's gender, or null if not specified.
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * Gets the user's activity level.
     *
     * @return The user's activity level, or null if not specified.
     */
    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    /**
     * Gets the user's goal.
     *
     * @return The user's goal, or null if not specified.
     */
    public Goal getGoal() {
        return goal;
    }

    /**
     * Gets the list of meals associated with the user.
     *
     * @return An immutable list of the user's meals.
     */
    public List<MealDTO> getMeals() {
        return meals;
    }

    /**
     * Gets the roles assigned to the user.
     *
     * @return An immutable collection of the user's roles.
     */
    public Collection<Role> getRoles() {
        return roles;
    }

    /**
     * Gets the list of recommended daily intakes for the user.
     *
     * @return An immutable list of the user's recommended daily intakes.
     */
    public List<RecommendedDailyIntakeDTO> getRecommendedDailyIntakes() {
        return recommendedDailyIntakes;
    }
}

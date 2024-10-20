package balancebite.dto.user;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.model.Role;
import balancebite.model.userenums.ActivityLevel;
import balancebite.model.userenums.Gender;
import balancebite.model.userenums.Goal;

import java.util.List;
import java.util.Set;

/**
 * Data Transfer Object (DTO) for User.
 * This DTO is used to send user data back to the client.
 */
public class UserDTO {

    private final Long id;
    private final String userName;
    private final String email;
    private final Double weight;
    private final Integer age;
    private final Double height;
    private final Gender gender;
    private final ActivityLevel activityLevel;
    private final Goal goal;
    private final List<MealDTO> meals;
    private final Role role;
    private final List<RecommendedDailyIntakeDTO> recommendedDailyIntakes;

    /**
     * Constructor for creating a UserDTO with all fields.
     *
     * @param id                     the ID of the user.
     * @param userName               the name of the user.
     * @param email                  the email of the user.
     * @param weight                 the weight of the user.
     * @param age                    the age of the user.
     * @param height                 the height of the user.
     * @param gender                 the gender of the user.
     * @param activityLevel          the activity level of the user.
     * @param goal                   the goal of the user.
     * @param meals                  the list of meals associated with the user.
     * @param role                   the role of the user.
     * @param recommendedDailyIntakes the recommended daily intakes of the user.
     */
    public UserDTO(Long id, String userName, String email, Double weight, Integer age, Double height, Gender gender,
                   ActivityLevel activityLevel, Goal goal, List<MealDTO> meals, Role role,
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
        this.meals = (meals != null) ? List.copyOf(meals) : List.of(); // Gebruik een niet-wijzigbare lijst
        this.role = role;
        this.recommendedDailyIntakes = (recommendedDailyIntakes != null) ? List.copyOf(recommendedDailyIntakes) : List.of(); // Gebruik een niet-wijzigbare lijst
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
        this(id, userName, email, null, null, null, null, null, null, List.of(), null, List.of());
    }

    // Getters only (no setters)

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public Double getWeight() {
        return weight;
    }

    public Integer getAge() {
        return age;
    }

    public Double getHeight() {
        return height;
    }

    public Gender getGender() {
        return gender;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public Goal getGoal() {
        return goal;
    }

    public Set<MealDTO> getMeals() {
        return Set.copyOf(meals);  // Zorg ervoor dat de set niet gemuteerd kan worden buiten deze DTO
    }

    public Role getRole() {
        return role;
    }

    public Set<RecommendedDailyIntakeDTO> getRecommendedDailyIntakes() {
        return Set.copyOf(recommendedDailyIntakes);  // Zorg ervoor dat de set niet gemuteerd kan worden buiten deze DTO
    }
}

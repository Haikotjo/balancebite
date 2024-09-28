package balancebite.dto.user;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.model.Role;

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
    private String name;

    /**
     * The email of the user.
     */
    private String email;

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
     * Constructor for creating a UserDTO with meals and recommended daily intake.
     *
     * @param id the ID of the user.
     * @param name the name of the user.
     * @param email the email of the user.
     * @param meals the list of meals associated with the user.
     * @param role the role of the user.
     * @param recommendedDailyIntake the recommended daily intake of the user.
     */
    public UserDTO(Long id, String name, String email, List<MealDTO> meals, Role role, RecommendedDailyIntakeDTO recommendedDailyIntake) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.meals = meals;
        this.role = role;
        this.recommendedDailyIntake = recommendedDailyIntake;
    }

    /**
     * Constructor for creating a UserDTO without the meals.
     *
     * @param id the ID of the user.
     * @param name the name of the user.
     * @param email the email of the user.
     * @param role the role of the user.
     * @param recommendedDailyIntake the recommended daily intake of the user.
     */
    public UserDTO(Long id, String name, String email, Role role, RecommendedDailyIntakeDTO recommendedDailyIntake) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.recommendedDailyIntake = recommendedDailyIntake;
    }

    /**
     * Constructor for creating a UserDTO with only basic user information (ID, name, and email).
     * This constructor is specifically used in the MealMapper to associate users with meals
     * without requiring the full user details (such as role or recommended daily intake).
     *
     * @param id    The unique identifier of the user.
     * @param name  The name of the user.
     * @param email The email of the user.
     */
    public UserDTO(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }


    // Getters and setters

    /**
     * Gets the unique identifier of the user.
     *
     * @return the ID of the user.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the user.
     *
     * @param id the ID to set for the user.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the user.
     *
     * @return the name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     *
     * @param name the name to set for the user.
     */
    public void setName(String name) {
        this.name = name;
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
     * @param email the email to set for the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the list of meals associated with the user.
     *
     * @return a list of meal DTOs.
     */
    public List<MealDTO> getMeals() {
        return meals;
    }

    /**
     * Sets the list of meals associated with the user.
     *
     * @param meals the list of meals to set.
     */
    public void setMeals(List<MealDTO> meals) {
        this.meals = meals;
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
     * @param role the role to set for the user.
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Gets the recommended daily intake associated with the user.
     *
     * @return the recommended daily intake DTO.
     */
    public RecommendedDailyIntakeDTO getRecommendedDailyIntake() {
        return recommendedDailyIntake;
    }

    /**
     * Sets the recommended daily intake associated with the user.
     *
     * @param recommendedDailyIntake the recommended daily intake DTO to set.
     */
    public void setRecommendedDailyIntake(RecommendedDailyIntakeDTO recommendedDailyIntake) {
        this.recommendedDailyIntake = recommendedDailyIntake;
    }
}

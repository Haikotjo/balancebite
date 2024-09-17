package balancebite.dto.user;

import balancebite.dto.meal.MealDTO;
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

    // Constructors

    /**
     * Default no-argument constructor for UserDTO.
     * Used by frameworks like Hibernate or Jackson.
     */
    public UserDTO() {
        // Default constructor
    }

    /**
     * Constructor for creating a UserDTO with meals.
     *
     * @param id the ID of the user.
     * @param name the name of the user.
     * @param email the email of the user.
     * @param meals the list of meals associated with the user.
     * @param role the role of the user.
     */
    public UserDTO(Long id, String name, String email, List<MealDTO> meals, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.meals = meals;
        this.role = role;
    }

    /**
     * Constructor for creating a UserDTO without the meals.
     *
     * @param id the ID of the user.
     * @param name the name of the user.
     * @param email the email of the user.
     */
    public UserDTO(Long id, String name, String email, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}

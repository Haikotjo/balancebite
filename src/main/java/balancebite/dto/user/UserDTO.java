package balancebite.dto.user;

import balancebite.dto.meal.MealDTO;
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
}

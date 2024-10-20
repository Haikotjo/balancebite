package balancebite.dto.meal;

import balancebite.dto.mealingredient.MealIngredientInputDTO;
import balancebite.dto.user.UserDTO;  // Import UserDTO
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Data Transfer Object (DTO) for capturing input data related to a Meal.
 * This class is used for receiving data from the client-side when creating or updating
 * a Meal in the application.
 */
public class MealInputDTO {

    @NotBlank(message = "The name of the meal cannot be blank. Please provide a valid name.")
    private String name;

    @NotEmpty(message = "The meal must contain at least one ingredient. Please provide ingredients.")
    private List<MealIngredientInputDTO> mealIngredients;

    // List of users to associate with the meal (optional, depending on use case)
    private List<UserDTO> users;

    // The user who created this meal (optional)
    private UserDTO createdBy;

    /**
     * Default constructor for MealInputDTO.
     */
    public MealInputDTO() {}

    /**
     * Constructor for creating a MealInputDTO with basic meal information.
     *
     * @param name             the name of the meal.
     * @param mealIngredients  the list of ingredients that make up the meal.
     * @param users            the list of users associated with the meal (if applicable).
     * @param createdBy        the user who created the meal (if applicable).
     */
    public MealInputDTO(String name, List<MealIngredientInputDTO> mealIngredients, List<UserDTO> users, UserDTO createdBy) {
        this.name = name;
        this.mealIngredients = mealIngredients;
        this.users = users;
        this.createdBy = createdBy;
    }

    // Getters

    /**
     * Gets the name of the meal.
     *
     * @return the name of the meal.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of ingredients that make up the meal.
     *
     * @return the list of meal ingredients.
     */
    public List<MealIngredientInputDTO> getMealIngredients() {
        return mealIngredients;
    }

    /**
     * Gets the list of users associated with the meal.
     *
     * @return the list of users.
     */
    public List<UserDTO> getUsers() {
        return users;
    }

    /**
     * Gets the user who created this meal.
     *
     * @return the user who created the meal, or null if not specified.
     */
    public UserDTO getCreatedBy() {
        return createdBy;
    }

    // Setters

    /**
     * Sets the name of the meal.
     *
     * @param name the name of the meal to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the list of ingredients that make up the meal.
     *
     * @param mealIngredients the list of meal ingredients to set.
     */
    @NotEmpty(message = "The meal must contain at least one ingredient. Please provide ingredients.")
    @Valid
    public void setMealIngredients(List<MealIngredientInputDTO> mealIngredients) {
        this.mealIngredients = mealIngredients;
    }

    /**
     * Sets the list of users associated with the meal.
     *
     * @param users the list of users to set.
     */
    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }

    /**
     * Sets the user who created this meal.
     *
     * @param createdBy the user who created the meal (optional).
     */
    public void setCreatedBy(UserDTO createdBy) {
        this.createdBy = createdBy;
    }
}

package balancebite.dto.meal;

import balancebite.dto.mealingredient.MealIngredientInputDTO;
import balancebite.dto.user.UserDTO;  // Import UserDTO
import jakarta.persistence.Column;
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

    /**
     * The name of the meal. This field is mandatory and cannot be blank.
     */
    @NotBlank(message = "The name of the meal cannot be blank. Please provide a valid name.")
    private String name;

    /**
     * List of ingredients that make up the meal. This field is mandatory and
     * requires at least one ingredient.
     */
    @NotEmpty(message = "The meal must contain at least one ingredient. Please provide ingredients.")
    @Valid
    private List<MealIngredientInputDTO> mealIngredients;

    /**
     * Optional description of the meal. The description length is limited to 1000 characters.
     */
    @Column(length = 1000)
    private String mealDescription;

    /**
     * The user who created this meal (if applicable).
     * This field is managed by the system and should not be set by the client directly.
     */
    @Valid
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
     * @param mealDescription  the description of the meal (optional).
     * @param createdBy        the user who created the meal (if applicable).
     */
    public MealInputDTO(String name, List<MealIngredientInputDTO> mealIngredients, String mealDescription, UserDTO createdBy) {
        this.name = name;
        this.mealIngredients = mealIngredients;
        this.mealDescription = mealDescription;
        this.createdBy = createdBy;
    }

    /**
     * Gets the name of the meal.
     *
     * @return the name of the meal.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the meal.
     *
     * @param name the name of the meal.
     */
    public void setName(String name) {
        this.name = name;
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
     * Sets the list of ingredients that make up the meal.
     *
     * @param mealIngredients the list of ingredients to set.
     */
    public void setMealIngredients(List<MealIngredientInputDTO> mealIngredients) {
        this.mealIngredients = mealIngredients;
    }

    /**
     * Gets the description of the meal.
     *
     * @return the description of the meal.
     */
    public String getMealDescription() {
        return mealDescription;
    }

    /**
     * Sets the description of the meal.
     *
     * @param mealDescription the description of the meal.
     */
    public void setMealDescription(String mealDescription) {
        this.mealDescription = mealDescription;
    }

    /**
     * Gets the user who created this meal, if applicable.
     *
     * @return the user who created the meal.
     */
    public UserDTO getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the user who created this meal.
     * Note: This field is typically managed by the system.
     *
     * @param createdBy the user who created the meal.
     */
    public void setCreatedBy(UserDTO createdBy) {
        this.createdBy = createdBy;
    }
}

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

    @NotBlank(message = "The name of the meal cannot be blank. Please provide a valid name.")
    private String name;

    @NotEmpty(message = "The meal must contain at least one ingredient. Please provide ingredients.")
    @Valid
    private List<MealIngredientInputDTO> mealIngredients;

    // Optional description of the meal.
    @Column(length = 1000)
    private String mealDescription;

    // The user who created this meal (optional)
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

    // Getters

    public String getName() {
        return name;
    }

    public List<MealIngredientInputDTO> getMealIngredients() {
        return mealIngredients;
    }

    public String getMealDescription() {
        return mealDescription;
    }

    public UserDTO getCreatedBy() {
        return createdBy;
    }

    // Setters

    public void setName(String name) {
        this.name = name;
    }

    public void setMealIngredients(List<MealIngredientInputDTO> mealIngredients) {
        this.mealIngredients = mealIngredients;
    }

    public void setMealDescription(String mealDescription) {
        this.mealDescription = mealDescription;
    }

    public void setCreatedBy(UserDTO createdBy) {
        this.createdBy = createdBy;
    }
}

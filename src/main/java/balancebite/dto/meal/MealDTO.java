package balancebite.dto.meal;

import balancebite.dto.mealingredient.MealIngredientDTO;

import java.util.List;

/**
 * Data Transfer Object (DTO) for transferring Meal data between layers of the application.
 * This class contains the essential fields required for the response when a meal is created
 * or retrieved, along with a message field for success notification.
 */
public class MealDTO {

    private Long id;
    private String name;
    private List<MealIngredientDTO> mealIngredients;

    // Message for success or additional information
    private String message;

    /**
     * Default constructor for MealDTO.
     */
    public MealDTO() {}

    /**
     * Constructor for creating a MealDTO with basic meal information.
     *
     * @param id                the ID of the meal.
     * @param name              the name of the meal.
     * @param mealIngredients   the list of ingredients in the meal.
     * @param message           the message indicating the status of meal creation.
     */
    public MealDTO(Long id, String name, List<MealIngredientDTO> mealIngredients, String message) {
        this.id = id;
        this.name = name;
        this.mealIngredients = mealIngredients;
        this.message = message;
    }

    // Getters

    /**
     * Gets the ID of the meal.
     *
     * @return the ID of the meal.
     */
    public Long getId() {
        return id;
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
     * Gets the list of ingredients in the meal.
     *
     * @return the list of MealIngredientDTO.
     */
    public List<MealIngredientDTO> getMealIngredients() {
        return mealIngredients;
    }

    /**
     * Gets the message for the meal response.
     *
     * @return the message indicating the success or status of the meal creation.
     */
    public String getMessage() {
        return message;
    }

    // Setters

    /**
     * Sets the ID of the meal.
     *
     * @param id the ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the name of the meal.
     *
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the list of ingredients for the meal.
     *
     * @param mealIngredients the list of ingredients to set.
     */
    public void setMealIngredients(List<MealIngredientDTO> mealIngredients) {
        this.mealIngredients = mealIngredients;
    }

    /**
     * Sets the message for the meal response.
     *
     * @param message the message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}

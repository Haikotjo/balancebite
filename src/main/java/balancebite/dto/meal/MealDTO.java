package balancebite.dto.meal;

import balancebite.dto.mealingredient.MealIngredientDTO;
import balancebite.dto.user.UserDTO;  // Import UserDTO

import java.util.List;

/**
 * Data Transfer Object (DTO) for transferring Meal data between layers of the application.
 * This class contains the essential fields required for the response when a meal is created
 * or retrieved.
 */
public class MealDTO {

    private final Long id;
    private final String name;
    private final String mealDescription;
    private final List<MealIngredientDTO> mealIngredients;

    // List of users associated with the meal
    private final List<UserDTO> users;

    // Creator of the meal
    private final UserDTO createdBy;

    /**
     * Constructor for creating a MealDTO with basic meal information.
     *
     * @param id                the ID of the meal.
     * @param name              the name of the meal.
     * @param mealDescription   the description of the meal.
     * @param mealIngredients   the list of ingredients in the meal.
     * @param users             the list of users associated with the meal.
     * @param createdBy         the user that created the meal.
     */
    public MealDTO(Long id, String name, String mealDescription, List<MealIngredientDTO> mealIngredients, List<UserDTO> users, UserDTO createdBy) {
        this.id = id;
        this.name = name;
        this.mealDescription = mealDescription;
        this.mealIngredients = (mealIngredients != null) ? List.copyOf(mealIngredients) : List.of();  // Use an unmodifiable list
        this.users = (users != null) ? List.copyOf(users) : List.of();  // Use an unmodifiable list
        this.createdBy = createdBy;
    }

    // Getters only (no setters)

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
     * Gets the description of the meal.
     *
     * @return the description of the meal.
     */
    public String getMealDescription() {
        return mealDescription;
    }

    /**
     * Gets the list of ingredients in the meal.
     *
     * @return an unmodifiable list of MealIngredientDTO.
     */
    public List<MealIngredientDTO> getMealIngredients() {
        return List.copyOf(mealIngredients);  // Ensure the list cannot be mutated outside this DTO
    }

    /**
     * Gets the list of users associated with the meal.
     *
     * @return an unmodifiable list of users.
     */
    public List<UserDTO> getUsers() {
        return List.copyOf(users);  // Ensure the list cannot be mutated outside this DTO
    }

    /**
     * Gets the user who created the meal.
     *
     * @return the user who created the meal.
     */
    public UserDTO getCreatedBy() {
        return createdBy;
    }
}

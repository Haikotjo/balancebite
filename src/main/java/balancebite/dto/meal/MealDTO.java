package balancebite.dto.meal;

import balancebite.dto.mealingredient.MealIngredientDTO;
import balancebite.dto.user.UserDTO;

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

    // Count of users associated with the meal
    private final int userCount;

    // Creator of the meal
    private final UserDTO createdBy;

    /**
     * Constructor for creating a MealDTO with basic meal information.
     *
     * @param id                the ID of the meal.
     * @param name              the name of the meal.
     * @param mealDescription   the description of the meal.
     * @param mealIngredients   the list of ingredients in the meal.
     * @param userCount         the count of users who have added the meal.
     * @param createdBy         the user that created the meal.
     */
    public MealDTO(Long id, String name, String mealDescription, List<MealIngredientDTO> mealIngredients, int userCount, UserDTO createdBy) {
        this.id = id;
        this.name = name;
        this.mealDescription = mealDescription;
        this.mealIngredients = (mealIngredients != null) ? List.copyOf(mealIngredients) : List.of();  // Use an unmodifiable list
        this.userCount = userCount;
        this.createdBy = createdBy;
    }

    // Getters only (no setters)

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMealDescription() {
        return mealDescription;
    }

    public List<MealIngredientDTO> getMealIngredients() {
        return List.copyOf(mealIngredients);  // Ensure the list cannot be mutated outside this DTO
    }

    /**
     * Gets the count of users associated with the meal.
     *
     * @return the user count.
     */
    public int getUserCount() {
        return userCount;
    }

    public UserDTO getCreatedBy() {
        return createdBy;
    }
}

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
     * @param mealIngredients   the list of ingredients in the meal.
     * @param users             the list of users associated with the meal.
     * @param createdBy         the user that created the meal.
     */
    public MealDTO(Long id, String name, List<MealIngredientDTO> mealIngredients, List<UserDTO> users, UserDTO createdBy) {
        this.id = id;
        this.name = name;
        this.mealIngredients = (mealIngredients != null) ? List.copyOf(mealIngredients) : List.of();  // Gebruik een niet-wijzigbare lijst
        this.users = (users != null) ? List.copyOf(users) : List.of();  // Gebruik een niet-wijzigbare lijst
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
     * Gets the list of ingredients in the meal.
     *
     * @return an unmodifiable list of MealIngredientDTO.
     */
    public List<MealIngredientDTO> getMealIngredients() {
        return List.copyOf(mealIngredients);  // Zorg ervoor dat de lijst niet gemuteerd kan worden buiten deze DTO
    }

    /**
     * Gets the list of users associated with the meal.
     *
     * @return an unmodifiable list of users.
     */
    public List<UserDTO> getUsers() {
        return List.copyOf(users);  // Zorg ervoor dat de lijst niet gemuteerd kan worden buiten deze DTO
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

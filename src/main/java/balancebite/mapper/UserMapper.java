package balancebite.mapper;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.mealingredient.MealIngredientDTO;
import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserInputDTO;
import balancebite.model.User;
import balancebite.model.Meal;

import java.util.stream.Collectors;

/**
 * Mapper class for converting between User entities and DTOs.
 */
public class UserMapper {

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user the User entity to convert.
     * @return the corresponding UserDTO.
     */
    public static UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());

        // Convert meals to full MealDTOs with ingredients
        userDTO.setMeals(user.getMeals().stream()
                .map(meal -> new MealDTO(
                        meal.getId(),
                        meal.getName(),
                        meal.getMealIngredients().stream()
                                .map(ingredient -> new MealIngredientDTO(
                                        ingredient.getId(),
                                        meal.getId(),
                                        ingredient.getFoodItem().getId(),
                                        ingredient.getQuantity()
                                ))
                                .collect(Collectors.toList()),
                        null // You can set a message here if needed
                ))
                .collect(Collectors.toList()));
        return userDTO;
    }

    /**
     * Converts a UserInputDTO to a User entity.
     *
     * @param inputDTO the UserInputDTO containing user creation/update data.
     * @return the corresponding User entity.
     */
    public static User toEntity(UserInputDTO inputDTO) {
        User user = new User();
        user.setName(inputDTO.getName());
        user.setEmail(inputDTO.getEmail());
        user.setPassword(inputDTO.getPassword()); // Make sure to hash the password before storing it!
        return user;
    }
}

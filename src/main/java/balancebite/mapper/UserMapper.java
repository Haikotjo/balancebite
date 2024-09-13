package balancebite.mapper;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.mealingredient.MealIngredientDTO;
import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserInputDTO;
import balancebite.model.User;
import balancebite.model.Meal;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper class for converting between User entities and DTOs.
 * This class is responsible for mapping User and Meal entities
 * to their corresponding Data Transfer Objects (DTOs) and vice versa.
 */
@Component
public class UserMapper {

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user the User entity to convert.
     * @return the corresponding UserDTO, containing the user data and associated meals.
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        // Create a new UserDTO and set the user information
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());

        // Convert meals associated with the user into MealDTOs with ingredients
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
                        null // Optional: You can set a message here if needed
                ))
                .collect(Collectors.toList()));

        return userDTO;
    }

    /**
     * Converts a UserInputDTO to a User entity.
     * This method is used when creating or updating a User entity.
     *
     * @param inputDTO the UserInputDTO containing the input data for the user.
     * @return the corresponding User entity, ready to be saved to the database.
     */
    public User toEntity(UserInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }

        // Create a new User entity and set the properties from the DTO
        User user = new User();
        user.setName(inputDTO.getName());
        user.setEmail(inputDTO.getEmail());
        user.setPassword(inputDTO.getPassword()); // Make sure to hash the password before storing it!

        return user;
    }
}

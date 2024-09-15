package balancebite.mapper;

import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserInputDTO;
import balancebite.dto.meal.MealDTO;
import balancebite.model.User;
import balancebite.model.Role;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class responsible for converting between User entities and User DTOs.
 * This class handles mapping from User to UserDTO, and from UserInputDTO to User.
 */
@Component
public class UserMapper {

    private final MealMapper mealMapper;

    /**
     * Constructor that accepts a MealMapper.
     *
     * @param mealMapper Mapper to convert between Meal entities and MealDTOs.
     */
    public UserMapper(MealMapper mealMapper) {
        this.mealMapper = mealMapper;
    }

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user The User entity to convert.
     * @return The UserDTO object containing user data for client consumption.
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        // Convert the list of meals associated with the user to MealDTOs
        List<MealDTO> mealDTOs = user.getMeals().stream()
                .map(mealMapper::toDTO)
                .collect(Collectors.toList());

        // Create and return the UserDTO with all relevant fields
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setMeals(mealDTOs);
        userDTO.setRole(user.getRole()); // Map the role of the user
        return userDTO;
    }

    /**
     * Converts a UserInputDTO to a User entity.
     * Used when creating or updating a user based on client input.
     *
     * @param userInputDTO The input DTO containing user data from the client.
     * @return The User entity to be stored in the database.
     */
    public User toEntity(UserInputDTO userInputDTO) {
        if (userInputDTO == null) {
            return null;
        }

        // Create a new User entity
        User user = new User();
        user.setName(userInputDTO.getName());
        user.setEmail(userInputDTO.getEmail());
        user.setPassword(userInputDTO.getPassword());  // Make sure to hash the password before saving in the service layer
        user.setRole(userInputDTO.getRole()); // Set the role based on the input DTO

        return user;
    }
}

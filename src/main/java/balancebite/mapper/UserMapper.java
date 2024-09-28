package balancebite.mapper;

import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserInputDTO;
import balancebite.dto.meal.MealDTO;
import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.mapper.MealMapper;
import balancebite.mapper.RecommendedDailyIntakeMapper;
import balancebite.model.User;
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
    private final RecommendedDailyIntakeMapper recommendedDailyIntakeMapper;

    /**
     * Constructor that accepts a MealMapper and RecommendedDailyIntakeMapper.
     *
     * @param mealMapper Mapper to convert between Meal entities and MealDTOs.
     * @param recommendedDailyIntakeMapper Mapper to convert between RecommendedDailyIntake entities and DTOs.
     */
    public UserMapper(MealMapper mealMapper, RecommendedDailyIntakeMapper recommendedDailyIntakeMapper) {
        this.mealMapper = mealMapper;
        this.recommendedDailyIntakeMapper = recommendedDailyIntakeMapper;
    }

    /**
     * Converts a User entity to a UserDTO.
     * This includes converting meals and recommended daily intake.
     *
     * @param user The User entity to convert.
     * @return The UserDTO object containing user data for client consumption.
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        // Convert the list of meals associated with the user to MealDTOs
        List<MealDTO> mealDTOs = user.getMeals() != null ? user.getMeals().stream()
                .map(mealMapper::toDTO)
                .collect(Collectors.toList()) : null;

        // Convert the recommended daily intake associated with the user to a DTO, with null-check
        RecommendedDailyIntakeDTO recommendedDailyIntakeDTO = user.getRecommendedDailyIntake() != null
                ? recommendedDailyIntakeMapper.toDTO(user.getRecommendedDailyIntake())
                : null;

        // Create and return the UserDTO with all relevant fields
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setMeals(mealDTOs);
        userDTO.setRole(user.getRole());
        userDTO.setRecommendedDailyIntake(recommendedDailyIntakeDTO); // Map the recommended daily intake

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

        // Create a new User entity without ID, as it's auto-generated
        User user = new User();
        user.setName(userInputDTO.getName());
        user.setEmail(userInputDTO.getEmail());
        user.setPassword(userInputDTO.getPassword());  // Make sure to hash the password before saving in the service layer
        user.setRole(userInputDTO.getRole());

        // Convert the RecommendedDailyIntakeDTO to the entity, with null-check
        if (userInputDTO.getRecommendedDailyIntake() != null) {
            user.setRecommendedDailyIntake(recommendedDailyIntakeMapper.toEntity(userInputDTO.getRecommendedDailyIntake()));
        }

        return user;
    }
}

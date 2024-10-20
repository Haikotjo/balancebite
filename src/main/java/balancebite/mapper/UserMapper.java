package balancebite.mapper;

import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserInputDTO;
import balancebite.dto.meal.MealDTO;
import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.model.User;
import balancebite.model.RecommendedDailyIntake;
import balancebite.mapper.MealMapper;
import balancebite.mapper.RecommendedDailyIntakeMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
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
     * This includes converting meals and recommended daily intakes.
     *
     * @param user The User entity to convert.
     * @return The UserDTO object containing user data for client consumption.
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        // Convert the set of meals associated with the user to MealDTOs
        List<MealDTO> mealDTOs = user.getMeals() != null ? user.getMeals().stream()
                .map(mealMapper::toDTO)
                .collect(Collectors.toList()) : List.of();

        // Convert the set of recommended daily intakes associated with the user to DTOs
        List<RecommendedDailyIntakeDTO> recommendedDailyIntakeDTOs = user.getRecommendedDailyIntakes() != null
                ? user.getRecommendedDailyIntakes().stream()
                .map(recommendedDailyIntakeMapper::toDTO)
                .collect(Collectors.toList())
                : List.of();

        // Create and return the UserDTO with all relevant fields
        return new UserDTO(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getWeight(),
                user.getAge(),
                user.getHeight(),
                user.getGender(),
                user.getActivityLevel(),
                user.getGoal(),
                mealDTOs,
                user.getRole(),
                recommendedDailyIntakeDTOs
        );
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
            throw new IllegalArgumentException("UserInputDTO cannot be null");
        }

        if (userInputDTO.getUserName() == null || userInputDTO.getUserName().isBlank()) {
            throw new IllegalArgumentException("User name cannot be null or blank");
        }

        if (userInputDTO.getEmail() == null || userInputDTO.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        if (userInputDTO.getPassword() == null || userInputDTO.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }

        // Create a new User entity without ID, as it's auto-generated
        return new User(
                userInputDTO.getUserName(),
                userInputDTO.getEmail(),
                userInputDTO.getPassword(),  // Make sure to hash the password before saving in the service layer
                userInputDTO.getRole()
        );
    }
}

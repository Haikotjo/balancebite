package balancebite.mapper;

import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserInputDTO;
import balancebite.dto.meal.MealDTO;
import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.mapper.MealMapper;
import balancebite.mapper.RecommendedDailyIntakeMapper;
import balancebite.model.User;
import balancebite.model.RecommendedDailyIntake;
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

        // Convert the list of meals associated with the user to MealDTOs
        List<MealDTO> mealDTOs = user.getMeals() != null ? user.getMeals().stream()
                .map(mealMapper::toDTO)
                .collect(Collectors.toList()) : null;

        // Convert the set of recommended daily intakes associated with the user to DTOs
        List<RecommendedDailyIntakeDTO> recommendedDailyIntakeDTOs = user.getRecommendedDailyIntakes() != null
                ? user.getRecommendedDailyIntakes().stream()
                .map(recommendedDailyIntakeMapper::toDTO)
                .collect(Collectors.toList())
                : null;

        // Create and return the UserDTO with all relevant fields
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUserName(user.getUserName());
        userDTO.setEmail(user.getEmail());
        userDTO.setWeight(user.getWeight());  // Map the weight
        userDTO.setAge(user.getAge());        // Map the age
        userDTO.setHeight(user.getHeight());  // Map the height
        userDTO.setGender(user.getGender());  // Map the gender
        userDTO.setActivityLevel(user.getActivityLevel());  // Map the activity level
        userDTO.setGoal(user.getGoal());  // Map the goal
        userDTO.setMeals(mealDTOs);
        userDTO.setRole(user.getRole());
        userDTO.setRecommendedDailyIntakes(recommendedDailyIntakeDTOs); // Map the recommended daily intakes

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
        user.setUserName(userInputDTO.getUserName());
        user.setEmail(userInputDTO.getEmail());
        user.setPassword(userInputDTO.getPassword());  // Make sure to hash the password before saving in the service layer
        user.setWeight(userInputDTO.getWeight());  // Set the weight
        user.setAge(userInputDTO.getAge());        // Set the age
        user.setHeight(userInputDTO.getHeight());  // Set the height
        user.setGender(userInputDTO.getGender());  // Set the gender
        user.setActivityLevel(userInputDTO.getActivityLevel());  // Set the activity level
        user.setGoal(userInputDTO.getGoal());  // Set the goal
        user.setRole(userInputDTO.getRole());

        return user;
    }
}
package balancebite.mapper;

import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserBasicInfoInputDTO;
import balancebite.dto.user.UserDetailsInputDTO;
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
 * This class handles mapping from User to UserDTO, UserBasicInfoInputDTO, and UserDetailsInputDTO to User.
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
     * Converts a UserBasicInfoInputDTO to a User entity.
     * Used when creating or updating basic user information.
     *
     * @param userBasicInfoInputDTO The input DTO containing basic user data from the client.
     * @return The User entity to be stored in the database.
     */
    public User toEntity(UserBasicInfoInputDTO userBasicInfoInputDTO) {
        if (userBasicInfoInputDTO == null) {
            throw new IllegalArgumentException("UserBasicInfoInputDTO cannot be null");
        }

        // Create a new User entity without ID, as it's auto-generated
        return new User(
                userBasicInfoInputDTO.getUserName(),
                userBasicInfoInputDTO.getEmail(),
                userBasicInfoInputDTO.getPassword(),  // Make sure to hash the password before saving in the service layer
                userBasicInfoInputDTO.getRole()
        );
    }

    /**
     * Updates an existing User entity with details from UserDetailsInputDTO.
     * Used when updating detailed user information.
     *
     * @param user The existing User entity to be updated.
     * @param userDetailsInputDTO The input DTO containing detailed user data from the client.
     */
    public void updateEntityWithDetails(User user, UserDetailsInputDTO userDetailsInputDTO) {
        if (user == null || userDetailsInputDTO == null) {
            throw new IllegalArgumentException("User and UserDetailsInputDTO cannot be null");
        }

        if (userDetailsInputDTO.getWeight() != null) {
            user.setWeight(userDetailsInputDTO.getWeight());
        }
        if (userDetailsInputDTO.getAge() != null) {
            user.setAge(userDetailsInputDTO.getAge());
        }
        if (userDetailsInputDTO.getHeight() != null) {
            user.setHeight(userDetailsInputDTO.getHeight());
        }
        if (userDetailsInputDTO.getGender() != null) {
            user.setGender(userDetailsInputDTO.getGender());
        }
        if (userDetailsInputDTO.getActivityLevel() != null) {
            user.setActivityLevel(userDetailsInputDTO.getActivityLevel());
        }
        if (userDetailsInputDTO.getGoal() != null) {
            user.setGoal(userDetailsInputDTO.getGoal());
        }
    }
}

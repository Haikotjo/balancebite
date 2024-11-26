package balancebite.mapper;

import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserDetailsInputDTO;
import balancebite.dto.user.UserLoginInputDTO;
import balancebite.dto.user.UserRegistrationInputDTO;
import balancebite.dto.meal.MealDTO;
import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.model.user.Role;
import balancebite.model.user.User;
import balancebite.model.user.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper class responsible for converting between User entities and DTOs.
 * Handles mapping for UserRegistrationInputDTO, UserLoginInputDTO, and UserDTO.
 */
@Component
public class UserMapper {

    private static final Logger log = LoggerFactory.getLogger(UserMapper.class);

    private final MealMapper mealMapper;
    private final RecommendedDailyIntakeMapper recommendedDailyIntakeMapper;

    /**
     * Constructor for UserMapper.
     *
     * @param mealMapper                   Mapper for converting Meal entities to MealDTOs.
     * @param recommendedDailyIntakeMapper Mapper for converting RecommendedDailyIntake entities to DTOs.
     */
    public UserMapper(MealMapper mealMapper, RecommendedDailyIntakeMapper recommendedDailyIntakeMapper) {
        this.mealMapper = mealMapper;
        this.recommendedDailyIntakeMapper = recommendedDailyIntakeMapper;
    }

    /**
     * Converts a User entity to a UserDTO.
     * Includes associated meals and recommended daily intakes.
     *
     * @param user The User entity to convert.
     * @return The converted UserDTO or null if the input is null.
     */
    public UserDTO toDTO(User user) {
        log.info("Mapping User entity to UserDTO for user ID: {}", user != null ? user.getId() : "null");
        if (user == null) {
            log.warn("User entity is null, returning null for UserDTO.");
            return null;
        }

        List<MealDTO> mealDTOs = user.getMeals() != null
                ? user.getMeals().stream()
                .map(mealMapper::toDTO)
                .collect(Collectors.toList())
                : List.of();

        List<RecommendedDailyIntakeDTO> recommendedDailyIntakeDTOs = user.getRecommendedDailyIntakes() != null
                ? user.getRecommendedDailyIntakes().stream()
                .map(recommendedDailyIntakeMapper::toDTO)
                .collect(Collectors.toList())
                : List.of();

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
                user.getRoles(),
                recommendedDailyIntakeDTOs
        );
    }

    /**
     * Converts a UserRegistrationInputDTO to a User entity.
     * Used for creating a new user during registration.
     *
     * @param registrationDTO The input DTO containing user registration details.
     * @return The created User entity.
     * @throws IllegalArgumentException if the input DTO is null.
     */
    public User toEntity(UserRegistrationInputDTO registrationDTO) {
        log.info("Mapping UserRegistrationInputDTO to User entity.");
        if (registrationDTO == null) {
            log.error("Input UserRegistrationInputDTO is null.");
            throw new IllegalArgumentException("UserRegistrationInputDTO cannot be null.");
        }

        Set<Role> roles = registrationDTO.getRoles().stream()
                .map(roleName -> new Role(UserRole.valueOf(roleName)))
                .collect(Collectors.toSet());

        return new User(
                registrationDTO.getUserName(),
                registrationDTO.getEmail(),
                registrationDTO.getPassword(),
                roles
        );
    }

    /**
     * Updates an existing User entity with details from UserDetailsInputDTO.
     *
     * @param user                 The existing User entity to update.
     * @param userDetailsInputDTO  The DTO containing updated user details.
     */
    public void updateDetailsFromDTO(User user, UserDetailsInputDTO userDetailsInputDTO) {
        log.info("Updating User entity with details from UserDetailsInputDTO for user ID: {}", user.getId());

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

    /**
     * Processes UserLoginInputDTO for authentication purposes.
     * This method does not perform any conversion but ensures valid input is logged.
     *
     * @param loginDTO The input DTO containing login details.
     */
    public void validateLoginDTO(UserLoginInputDTO loginDTO) {
        log.info("Validating UserLoginInputDTO for email: {}", loginDTO.getEmail());
        if (loginDTO == null) {
            log.error("UserLoginInputDTO is null.");
            throw new IllegalArgumentException("UserLoginInputDTO cannot be null.");
        }
        log.debug("UserLoginInputDTO validation passed for email: {}", loginDTO.getEmail());
    }
}

//package balancebite.mapper;
//
//import balancebite.dto.user.UserDTO;
//import balancebite.dto.user.UserBasicInfoInputDTO;
//import balancebite.dto.user.UserDetailsInputDTO;
//import balancebite.dto.meal.MealDTO;
//import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
//import balancebite.model.user.User;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * Mapper class responsible for converting between User entities and User DTOs.
// * Handles mapping between User and UserDTO, UserBasicInfoInputDTO, and UserDetailsInputDTO.
// * Ensures immutability and encapsulation of sensitive information.
// */
//@Component
//public class UserMapper {
//
//    private static final Logger log = LoggerFactory.getLogger(UserMapper.class);
//
//    private final MealMapper mealMapper;
//    private final RecommendedDailyIntakeMapper recommendedDailyIntakeMapper;
//
//    /**
//     * Constructor for UserMapper.
//     *
//     * @param mealMapper                   Mapper for converting Meal entities to MealDTOs.
//     * @param recommendedDailyIntakeMapper Mapper for converting RecommendedDailyIntake entities to DTOs.
//     */
//    public UserMapper(MealMapper mealMapper, RecommendedDailyIntakeMapper recommendedDailyIntakeMapper) {
//        this.mealMapper = mealMapper;
//        this.recommendedDailyIntakeMapper = recommendedDailyIntakeMapper;
//    }
//
//    /**
//     * Converts a User entity to a UserDTO.
//     * Includes associated meals and recommended daily intakes.
//     *
//     * @param user The User entity to convert.
//     * @return The converted UserDTO or null if the input is null.
//     */
//    public UserDTO toDTO(User user) {
//        log.info("Mapping User entity to UserDTO for user ID: {}", user != null ? user.getId() : "null");
//        if (user == null) {
//            log.warn("User entity is null, returning null for UserDTO.");
//            return null;
//        }
//
//        // Map meals to MealDTOs
//        List<MealDTO> mealDTOs = user.getMeals() != null
//                ? user.getMeals().stream()
//                .map(mealMapper::toDTO)
//                .collect(Collectors.toList())
//                : List.of();
//
//        // Map recommended daily intakes to DTOs
//        List<RecommendedDailyIntakeDTO> recommendedDailyIntakeDTOs = user.getRecommendedDailyIntakes() != null
//                ? user.getRecommendedDailyIntakes().stream()
//                .map(recommendedDailyIntakeMapper::toDTO)
//                .collect(Collectors.toList())
//                : List.of();
//
//        UserDTO userDTO = new UserDTO(
//                user.getId(),
//                user.getUserName(),
//                user.getEmail(),
//                user.getWeight(),
//                user.getAge(),
//                user.getHeight(),
//                user.getGender(),
//                user.getActivityLevel(),
//                user.getGoal(),
//                mealDTOs,
//                user.getRole(),
//                recommendedDailyIntakeDTOs
//        );
//
//        log.debug("Successfully mapped User entity to UserDTO: {}", userDTO);
//        return userDTO;
//    }
//
//    /**
//     * Converts a UserBasicInfoInputDTO to a User entity.
//     * Used for creating new users or updating basic user information.
//     *
//     * @param userBasicInfoInputDTO The input DTO containing user basic information.
//     * @return The created User entity.
//     * @throws IllegalArgumentException if the input DTO is null.
//     */
//    public User toEntity(UserBasicInfoInputDTO userBasicInfoInputDTO) {
//        log.info("Mapping UserBasicInfoInputDTO to User entity.");
//        if (userBasicInfoInputDTO == null) {
//            log.error("Input UserBasicInfoInputDTO is null.");
//            throw new IllegalArgumentException("UserBasicInfoInputDTO cannot be null.");
//        }
//
//        User user = new User(
//                userBasicInfoInputDTO.getUserName(),
//                userBasicInfoInputDTO.getEmail(),
//                userBasicInfoInputDTO.getPassword(),  // Password hashing should occur in the service layer
//                userBasicInfoInputDTO.getRole()
//        );
//
//        log.debug("Successfully mapped UserBasicInfoInputDTO to User entity: {}", user);
//        return user;
//    }
//
//    /**
//     * Updates an existing User entity with data from UserDetailsInputDTO.
//     * Does not modify fields not provided in the DTO.
//     *
//     * @param user                  The existing User entity to update.
//     * @param userDetailsInputDTO   The input DTO with detailed user information.
//     * @throws IllegalArgumentException if the User or DTO is null.
//     */
//    public void updateEntityWithDetails(User user, UserDetailsInputDTO userDetailsInputDTO) {
//        log.info("Updating User entity with ID: {} using UserDetailsInputDTO.", user != null ? user.getId() : "null");
//        if (user == null || userDetailsInputDTO == null) {
//            log.error("User or UserDetailsInputDTO is null. User: {}, DTO: {}", user, userDetailsInputDTO);
//            throw new IllegalArgumentException("User and UserDetailsInputDTO cannot be null.");
//        }
//
//        if (userDetailsInputDTO.getWeight() != null) {
//            log.debug("Updating weight for user ID {}: {} -> {}", user.getId(), user.getWeight(), userDetailsInputDTO.getWeight());
//            user.setWeight(userDetailsInputDTO.getWeight());
//        }
//        if (userDetailsInputDTO.getAge() != null) {
//            log.debug("Updating age for user ID {}: {} -> {}", user.getId(), user.getAge(), userDetailsInputDTO.getAge());
//            user.setAge(userDetailsInputDTO.getAge());
//        }
//        if (userDetailsInputDTO.getHeight() != null) {
//            log.debug("Updating height for user ID {}: {} -> {}", user.getId(), user.getHeight(), userDetailsInputDTO.getHeight());
//            user.setHeight(userDetailsInputDTO.getHeight());
//        }
//        if (userDetailsInputDTO.getGender() != null) {
//            log.debug("Updating gender for user ID {}: {} -> {}", user.getId(), user.getGender(), userDetailsInputDTO.getGender());
//            user.setGender(userDetailsInputDTO.getGender());
//        }
//        if (userDetailsInputDTO.getActivityLevel() != null) {
//            log.debug("Updating activity level for user ID {}: {} -> {}", user.getId(), user.getActivityLevel(), userDetailsInputDTO.getActivityLevel());
//            user.setActivityLevel(userDetailsInputDTO.getActivityLevel());
//        }
//        if (userDetailsInputDTO.getGoal() != null) {
//            log.debug("Updating goal for user ID {}: {} -> {}", user.getId(), user.getGoal(), userDetailsInputDTO.getGoal());
//            user.setGoal(userDetailsInputDTO.getGoal());
//        }
//
//        log.info("Successfully updated User entity with ID: {}", user.getId());
//    }
//}

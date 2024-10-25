package balancebite.service;

import balancebite.dto.NutrientInfoDTO;
import balancebite.dto.user.UserBasicInfoInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserDetailsInputDTO;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.mapper.UserMapper;
import balancebite.model.Meal;
import balancebite.model.RecommendedDailyIntake;
import balancebite.model.User;
import balancebite.repository.MealRepository;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing users.
 */
@Service
@Transactional
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final RecommendedDailyIntakeRepository recommendedDailyIntakeRepository;
    private final UserMapper userMapper;
    private final MealService mealService;  // Add MealService

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Constructor that initializes the UserService with the required repositories, mappers, and services.
     *
     * @param userRepository Repository to interact with the User data in the database.
     * @param mealRepository Repository to interact with the Meal data in the database.
     * @param userMapper Mapper to convert between User entities and DTOs.
     * @param mealService Service to interact with Meal-related operations.
     */
    public UserService(UserRepository userRepository, MealRepository mealRepository, UserMapper userMapper, MealService mealService, RecommendedDailyIntakeRepository recommendedDailyIntakeRepository) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.userMapper = userMapper;
        this.mealService = mealService;
        this.recommendedDailyIntakeRepository = recommendedDailyIntakeRepository;
    }

    /**
     * Creates a new user in the system based on the provided UserBasicInfoInputDTO.
     * Meals are not added at the time of creation.
     *
     * @param userBasicInfoInputDTO The input data for creating the user.
     * @return The created UserDTO.
     */
    public UserDTO createUser(UserBasicInfoInputDTO userBasicInfoInputDTO) {
        log.info("Attempting to create a new user with email: {}", userBasicInfoInputDTO.getEmail());
        try {
            User savedUser = userRepository.save(userMapper.toEntity(userBasicInfoInputDTO));
            log.info("Successfully created a new user with ID: {}", savedUser.getId());
            return userMapper.toDTO(savedUser);
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to create user due to data integrity violation: {}", e.getMessage());
            throw new EntityAlreadyExistsException("User with email " + userBasicInfoInputDTO.getEmail() + " already exists.");
        } catch (Exception e) {
            log.error("Unexpected error during user creation: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Updates the basic information of an existing user in the system based on the provided UserBasicInfoInputDTO.
     *
     * @param id The ID of the user to update.
     * @param userBasicInfoInputDTO The input data for updating the user.
     * @return The updated UserDTO.
     */
    public UserDTO updateUserBasicInfo(Long id, UserBasicInfoInputDTO userBasicInfoInputDTO) {
        log.info("Updating basic info for user with ID: {}", id);
        // Retrieve the existing user
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID " + id));

        // Directly update the fields without rechecking for null or blank
        existingUser.setUserName(userBasicInfoInputDTO.getUserName());
        existingUser.setEmail(userBasicInfoInputDTO.getEmail());
        // TODO: Hash the password before setting it here
        existingUser.setPassword(userBasicInfoInputDTO.getPassword());
        existingUser.setRole(userBasicInfoInputDTO.getRole());

        // Save and return updated user information
        User updatedUser = userRepository.save(existingUser);
        log.info("Successfully updated basic info for user with ID: {}", id);
        return userMapper.toDTO(updatedUser);
    }

    /**
     * Updates the detailed information of an existing user in the system based on the provided UserDetailsInputDTO.
     *
     * @param id The ID of the user to update.
     * @param userDetailsInputDTO The input data for updating the user's detailed information.
     * @return The updated UserDTO.
     */
    public UserDTO updateUserDetails(Long id, UserDetailsInputDTO userDetailsInputDTO) {
        log.info("Updating details for user with ID: {}", id);
        // Retrieve the existing user, throw custom UserNotFoundException if not found
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Update the user entity with the details provided in the DTO
        userMapper.updateEntityWithDetails(existingUser, userDetailsInputDTO);

        // Save and return the updated user
        User updatedUser = userRepository.save(existingUser);
        log.info("Successfully updated detailed info for user with ID: {}", id);
        return userMapper.toDTO(updatedUser);
    }

    /**
     * Retrieves all users in the system.
     *
     * @return A list of UserDTOs representing all users.
     */
    public List<UserDTO> getAllUsers() {
        log.info("Retrieving all users from the system.");
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            log.info("No users found in the system.");
        } else {
            log.info("Found {} users in the system.", users.size());
        }
        return users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The UserDTO representing the user.
     * @throws UserNotFoundException if the user is not found in the database.
     */
    public UserDTO getUserById(Long id) {
        log.info("Retrieving user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID " + id));
        log.info("Successfully retrieved user with ID: {}", id);
        return userMapper.toDTO(user);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     * @throws UserNotFoundException if the user is not found in the database.
     */
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            log.warn("Attempted to delete non-existing user with ID: {}", id); // Log a warning
            throw new UserNotFoundException("User not found with ID " + id);
        }
        userRepository.deleteById(id);
        log.info("Successfully deleted user with ID: {}", id);
    }

    /**
     * Adds an existing meal to the user's list of meals.
     *
     * @param userId The ID of the user to whom the meal is to be added.
     * @param mealId The ID of the meal to be added.
     * @return The updated UserDTO with the newly added meal.
     * @throws UserNotFoundException if the user is not found.
     * @throws MealNotFoundException if the meal is not found.
     */
    @Transactional
    public UserDTO addMealToUser(Long userId, Long mealId) {
        log.info("Attempting to add meal to user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException(mealId));

        // Add the meal to the user's list of meals.
        user.getMeals().add(meal);

        // Save the user with the new meal and return the UserDTO.
        User updatedUser = userRepository.save(user);
        log.info("Successfully added meal to user with ID: {}", userId);
        return userMapper.toDTO(updatedUser);
    }

    /**
     * Removes a meal from the user's list of meals.
     * This method checks if the user exists, and if the meal is associated with the user.
     *
     * @param userId The ID of the user.
     * @param mealId The ID of the meal to be removed.
     * @return The updated UserDTO without the removed meal.
     * @throws UserNotFoundException if the user is not found.
     * @throws MealNotFoundException if the meal is not found in the user's meal list.
     */
    @Transactional
    public UserDTO removeMealFromUser(Long userId, Long mealId) {
        log.info("Attempting to remove meal from user with ID: {}", userId);
        // Retrieve the user by their ID, throw UserNotFoundException if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Check if the meal belongs to the user, throw MealNotFoundException if not found
        Meal meal = user.getMeals().stream()
                .filter(m -> m.getId().equals(mealId))
                .findFirst()
                .orElseThrow(() -> new MealNotFoundException("The meal with ID " + mealId + " is not part of the user's meal list."));

        // Remove the meal from the user's list and save the changes to the repository
        user.getMeals().remove(meal);
        User updatedUser = userRepository.save(user);

        log.info("Successfully removed meal from user with ID: {}", userId);
        return userMapper.toDTO(updatedUser);
    }
}

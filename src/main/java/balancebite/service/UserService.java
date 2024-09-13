package balancebite.service;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserInputDTO;
import balancebite.mapper.MealMapper;
import balancebite.mapper.UserMapper;
import balancebite.model.Meal;
import balancebite.model.User;
import balancebite.repository.MealRepository;
import balancebite.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing User entities.
 * Handles creation, retrieval, and deletion of users.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final UserMapper userMapper;
    private final MealMapper mealMapper;

    // Constructor injection
    @Autowired
    public UserService(UserRepository userRepository, MealRepository mealRepository, UserMapper userMapper, MealMapper mealMapper) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.userMapper = userMapper;
        this.mealMapper = mealMapper;
    }

    /**
     * Retrieves all users from the repository and converts them to UserDTOs.
     *
     * @return a list of all UserDTOs.
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)  // Use instance method
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by their ID and converts them to a UserDTO.
     *
     * @param id the ID of the user.
     * @return the UserDTO if found, or Optional.empty if not.
     */
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO);  // Use instance method
    }

    /**
     * Creates a new user based on the UserInputDTO and saves it to the repository.
     *
     * @param inputDTO the data for creating the user.
     * @return the created UserDTO.
     */
    public UserDTO createUser(UserInputDTO inputDTO) {
        User user = userMapper.toEntity(inputDTO);
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to be deleted.
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Adds a meal to a user's meal list by user ID and meal ID.
     *
     * @param userId the ID of the user.
     * @param mealId the ID of the meal to add.
     */
    public void addMealToUser(Long userId, Long mealId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid meal ID: " + mealId));

        user.getMeals().add(meal); // Add meal to user's meal list
        userRepository.save(user); // Save changes
    }

    /**
     * Retrieves all meals for a specific user.
     *
     * @param userId the ID of the user.
     * @return a list of MealDTOs for the user.
     * @throws IllegalArgumentException if the user is not found.
     */
    public List<MealDTO> getAllMealsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));

        // Convert meals to MealDTOs
        return user.getMeals().stream()
                .map(mealMapper::toDTO)  // Use instance method
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific meal for a user by the meal ID.
     *
     * @param userId the ID of the user.
     * @param mealId the ID of the meal.
     * @return the MealDTO if the meal is found, or throws an exception if not.
     * @throws IllegalArgumentException if the user or meal is not found.
     */
    public MealDTO getMealForUser(Long userId, Long mealId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));

        // Find the meal in the user's meals
        return user.getMeals().stream()
                .filter(meal -> meal.getId().equals(mealId))
                .findFirst()
                .map(mealMapper::toDTO)  // Use instance method
                .orElseThrow(() -> new IllegalArgumentException("Meal not found for user: " + mealId));
    }
}

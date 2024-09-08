package balancebite.service;

import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserInputDTO;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MealRepository mealRepository;

    /**
     * Retrieves all users from the repository and converts them to UserDTOs.
     *
     * @return a list of all UserDTOs.
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by their ID and converts them to a UserDTO.
     *
     * @param id the ID of the user.
     * @return the UserDTO if found, or Optional.empty if not.
     */
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id).map(UserMapper::toDTO);
    }

    /**
     * Creates a new user based on the UserInputDTO and saves it to the repository.
     *
     * @param inputDTO the data for creating the user.
     * @return the created UserDTO.
     */
    public UserDTO createUser(UserInputDTO inputDTO) {
        User user = UserMapper.toEntity(inputDTO);
        User savedUser = userRepository.save(user);
        return UserMapper.toDTO(savedUser);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete.
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Adds a meal to the user's personal meal list.
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
}

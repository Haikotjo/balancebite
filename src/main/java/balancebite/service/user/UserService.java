package balancebite.service.user;

import balancebite.dto.user.UserBasicInfoInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserDetailsInputDTO;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.mapper.UserMapper;
import balancebite.model.Meal;
import balancebite.model.User;
import balancebite.repository.MealRepository;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import balancebite.service.MealService;
import balancebite.service.RecommendedDailyIntakeService;
import balancebite.service.interfaces.IUserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing users.
 */
@Service
@Transactional
public class UserService implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RecommendedDailyIntakeRepository recommendedDailyIntakeRepository;
    private final UserMapper userMapper;
    private final RecommendedDailyIntakeService recommendedDailyIntakeService;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Constructor that initializes the UserService with the required repositories, mappers, and services.
     *
     * @param userRepository Repository to interact with the User data in the database.
     * @param userMapper Mapper to convert between User entities and DTOs.
     * @param recommendedDailyIntakeRepository Repository to interact with RecommendedDailyIntake data.
     * @param recommendedDailyIntakeService Service for managing recommended daily intake calculations.
     */
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       RecommendedDailyIntakeRepository recommendedDailyIntakeRepository,
                       RecommendedDailyIntakeService recommendedDailyIntakeService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.recommendedDailyIntakeRepository = recommendedDailyIntakeRepository;
        this.recommendedDailyIntakeService = recommendedDailyIntakeService;
    }


    /**
     * Creates a new user in the system based on the provided UserBasicInfoInputDTO.
     * Meals are not added at the time of creation.
     *
     * @param userBasicInfoInputDTO The input data for creating the user.
     * @return The created UserDTO.
     */
    @Override
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
    @Override
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
    @Override
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

        // Generate or update the recommended daily intake for the user
        log.info("Generating or updating recommended daily intake for user ID: {}", id);
        recommendedDailyIntakeService.getOrCreateDailyIntakeForUser(id);

        return userMapper.toDTO(updatedUser);
    }

    /**
     * Retrieves all users in the system.
     *
     * @return A list of UserDTOs representing all users.
     */
    @Override
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
    @Override
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
    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            log.warn("Attempted to delete non-existing user with ID: {}", id); // Log a warning
            throw new UserNotFoundException("User not found with ID " + id);
        }
        userRepository.deleteById(id);
        log.info("Successfully deleted user with ID: {}", id);
    }
}
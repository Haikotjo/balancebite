package balancebite.service.user;

import balancebite.dto.user.UserBasicInfoInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserDetailsInputDTO;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.mapper.UserMapper;
import balancebite.model.User;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import balancebite.service.RecommendedDailyIntakeService;
import balancebite.service.interfaces.IUserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing users.
 * This class provides methods for creating, retrieving, updating, and deleting user entities.
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
     * Constructor to initialize dependencies for the UserService.
     *
     * @param userRepository                  The repository to manage user entities.
     * @param userMapper                      The mapper to convert between User and UserDTO.
     * @param recommendedDailyIntakeRepository The repository to manage recommended daily intakes.
     * @param recommendedDailyIntakeService   The service to handle recommended daily intake logic.
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
     * Creates a new user and saves it to the database.
     *
     * @param userBasicInfoInputDTO The input DTO containing basic user information.
     * @return The created UserDTO.
     * @throws EntityAlreadyExistsException If the email already exists in the database.
     * @throws RuntimeException             If an unexpected error occurs during user creation.
     */
    @Override
    public UserDTO createUser(UserBasicInfoInputDTO userBasicInfoInputDTO) {
        log.info("Attempting to create a new user with email: {}", userBasicInfoInputDTO.getEmail());
        try {
            // Convert input DTO to User entity and save
            User savedUser = userRepository.save(userMapper.toEntity(userBasicInfoInputDTO));
            log.info("Successfully created a new user with ID: {}", savedUser.getId());
            return userMapper.toDTO(savedUser);
        } catch (DataIntegrityViolationException e) {
            // Handle duplicate email error
            log.error("Failed to create user due to a duplicate email: {}", userBasicInfoInputDTO.getEmail(), e);
            throw new EntityAlreadyExistsException("A user with email " + userBasicInfoInputDTO.getEmail() + " already exists. Please use a different email.");
        } catch (Exception e) {
            // Handle unexpected errors
            log.error("Unexpected error during user creation: {}", e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred while creating the user. Please try again.");
        }
    }

    /**
     * Updates basic information of an existing user.
     *
     * @param id                    The ID of the user to update.
     * @param userBasicInfoInputDTO The input DTO containing updated user information.
     * @return The updated UserDTO.
     * @throws UserNotFoundException       If the user with the specified ID does not exist.
     * @throws EntityAlreadyExistsException If the provided email is already in use by another user.
     */
    @Override
    public UserDTO updateUserBasicInfo(Long id, UserBasicInfoInputDTO userBasicInfoInputDTO) {
        log.info("Updating basic info for user with ID: {}", id);

        // Fetch the existing user or throw exception if not found
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Cannot update user: No user found with ID " + id));

        // Check for duplicate email and ensure it belongs to another user
        if (userRepository.existsByEmail(userBasicInfoInputDTO.getEmail()) &&
                !existingUser.getEmail().equals(userBasicInfoInputDTO.getEmail())) {
            String errorMessage = "The email " + userBasicInfoInputDTO.getEmail() + " is already in use by another account.";
            log.warn(errorMessage);
            throw new EntityAlreadyExistsException(errorMessage);
        }

        // Update user details
        existingUser.setUserName(userBasicInfoInputDTO.getUserName());
        existingUser.setEmail(userBasicInfoInputDTO.getEmail());
        existingUser.setPassword(userBasicInfoInputDTO.getPassword());
        existingUser.setRole(userBasicInfoInputDTO.getRole());

        // Save and return updated user
        User updatedUser = userRepository.save(existingUser);
        log.info("Successfully updated basic info for user with ID: {}", id);
        return userMapper.toDTO(updatedUser);
    }

    /**
     * Updates detailed information of an existing user.
     *
     * @param id                  The ID of the user to update.
     * @param userDetailsInputDTO The input DTO containing detailed user information.
     * @return The updated UserDTO.
     * @throws UserNotFoundException If the user with the specified ID does not exist.
     */
    @Override
    public UserDTO updateUserDetails(Long id, UserDetailsInputDTO userDetailsInputDTO) {
        log.info("Updating details for user with ID: {}", id);

        // Fetch the user or throw exception if not found
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Cannot update user details: No user found with ID " + id));

        // Update user entity with details
        userMapper.updateEntityWithDetails(existingUser, userDetailsInputDTO);

        // Save the updated user
        User updatedUser = userRepository.save(existingUser);
        log.info("Successfully updated detailed info for user with ID: {}", id);

        // Ensure recommended daily intake is updated
        recommendedDailyIntakeService.getOrCreateDailyIntakeForUser(id);
        return userMapper.toDTO(updatedUser);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A list of UserDTOs.
     */
    @Override
    public List<UserDTO> getAllUsers() {
        log.info("Retrieving all users from the system.");
        List<User> users = userRepository.findAll();

        // Log if no users are found
        if (users.isEmpty()) {
            log.info("No users found in the system.");
        } else {
            log.info("Found {} users in the system.", users.size());
        }

        // Map entities to DTOs
        return users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The UserDTO of the requested user.
     * @throws UserNotFoundException If no user with the specified ID is found.
     */
    @Override
    public UserDTO getUserById(Long id) {
        log.info("Retrieving user with ID: {}", id);

        // Fetch user or throw exception if not found
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No user found with ID " + id));

        log.info("Successfully retrieved user with ID: {}", id);
        return userMapper.toDTO(user);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     * @throws UserNotFoundException If the user with the specified ID does not exist.
     */
    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        // Check if user exists
        if (!userRepository.existsById(id)) {
            log.warn("Attempted to delete non-existing user with ID: {}", id);
            throw new UserNotFoundException("Cannot delete user: No user found with ID " + id);
        }

        // Perform deletion
        userRepository.deleteById(id);
        log.info("Successfully deleted user with ID: {}", id);
    }
}

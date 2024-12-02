package balancebite.service.user;

import balancebite.dto.user.UserRegistrationInputDTO;
import balancebite.dto.user.UserLoginInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserDetailsInputDTO;
import balancebite.dto.user.UserRegistrationInputDTO;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.mapper.UserMapper;
import balancebite.model.user.Role;
import balancebite.model.user.User;
import balancebite.model.user.UserRole;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import balancebite.service.RecommendedDailyIntakeService;
import balancebite.service.interfaces.IUserService;
import balancebite.utils.UserUpdateHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private final UserUpdateHelper userUpdateHelper;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Constructor to initialize dependencies for the UserService.
     *
     * @param userRepository                  The repository to manage user entities.
     * @param userMapper                      The mapper to convert between User and UserDTO.
     * @param recommendedDailyIntakeRepository The repository to manage recommended daily intakes.
     * @param recommendedDailyIntakeService   The service to handle recommended daily intake logic.
     * @param userUpdateHelper
     */
    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       RecommendedDailyIntakeRepository recommendedDailyIntakeRepository,
                       RecommendedDailyIntakeService recommendedDailyIntakeService,
                       UserUpdateHelper userUpdateHelper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.recommendedDailyIntakeRepository = recommendedDailyIntakeRepository;
        this.recommendedDailyIntakeService = recommendedDailyIntakeService;
        this.userUpdateHelper = userUpdateHelper;
    }

    /**
     * Updates basic information of the currently logged-in user.
     * If the user does not exist, throws a UserNotFoundException.
     *
     * @param userRegistrationInputDTO The input DTO containing updated user information.
     * @param userId                   The ID of the currently logged-in user (from JWT token).
     * @return The updated UserDTO.
     */
    public UserDTO updateUserBasicInfo(Long userId, UserRegistrationInputDTO userRegistrationInputDTO) {
        log.info("Updating basic info for logged-in user with ID: {}", userId);

        // Fetch the user by ID
        User existingUser = userUpdateHelper.fetchUserById(userId);

        // Validate unique email
        if (userRegistrationInputDTO.getEmail() != null) {
            userUpdateHelper.validateUniqueEmail(userRegistrationInputDTO.getEmail(), userId);
        }

        // Update basic info
        existingUser = userUpdateHelper.updateBasicInfo(existingUser, userRegistrationInputDTO);

        // Save and return the updated user
        User updatedUser = userRepository.save(existingUser);
        log.info("Successfully updated basic info for user with ID: {}", userId);
        return userMapper.toDTO(updatedUser);
    }


    /**
     * Updates detailed information of the currently logged-in user.
     *
     * @param id                  The ID of the user to update (from JWT token).
     * @param userDetailsInputDTO The input DTO containing detailed user information.
     * @return The updated UserDTO.
     * @throws UserNotFoundException If the user with the specified ID does not exist.
     */
    @Override
    public UserDTO updateUserDetails(Long id, UserDetailsInputDTO userDetailsInputDTO) {
        log.info("Updating details for logged-in user with ID: {}", id);

        // Fetch the user or throw exception if not found
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Cannot update user details: No user found with ID " + id));

        // Update user entity with details
        userMapper.updateDetailsFromDTO(existingUser, userDetailsInputDTO);

        // Save the updated user
        User updatedUser = userRepository.save(existingUser);
        log.info("Successfully updated detailed info for user with ID: {}", id);

        // Ensure recommended daily intake is updated
        recommendedDailyIntakeService.getOrCreateDailyIntakeForUser(id);
        return userMapper.toDTO(updatedUser);
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

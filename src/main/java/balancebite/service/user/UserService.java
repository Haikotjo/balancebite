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

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       RecommendedDailyIntakeRepository recommendedDailyIntakeRepository,
                       RecommendedDailyIntakeService recommendedDailyIntakeService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.recommendedDailyIntakeRepository = recommendedDailyIntakeRepository;
        this.recommendedDailyIntakeService = recommendedDailyIntakeService;
    }

    @Override
    public UserDTO createUser(UserBasicInfoInputDTO userBasicInfoInputDTO) {
        log.info("Attempting to create a new user with email: {}", userBasicInfoInputDTO.getEmail());
        try {
            User savedUser = userRepository.save(userMapper.toEntity(userBasicInfoInputDTO));
            log.info("Successfully created a new user with ID: {}", savedUser.getId());
            return userMapper.toDTO(savedUser);
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to create user due to a duplicate email: {}", userBasicInfoInputDTO.getEmail(), e);
            throw new EntityAlreadyExistsException("A user with email " + userBasicInfoInputDTO.getEmail() + " already exists. Please use a different email.");
        } catch (Exception e) {
            log.error("Unexpected error during user creation: {}", e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred while creating the user. Please try again.");
        }
    }

    @Override
    public UserDTO updateUserBasicInfo(Long id, UserBasicInfoInputDTO userBasicInfoInputDTO) {
        log.info("Updating basic info for user with ID: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Cannot update user: No user found with ID " + id));

        // Check for duplicate email
        if (userRepository.existsByEmail(userBasicInfoInputDTO.getEmail()) &&
                !existingUser.getEmail().equals(userBasicInfoInputDTO.getEmail())) {
            throw new EntityAlreadyExistsException("The email " + userBasicInfoInputDTO.getEmail() + " is already in use by another account.");
        }

        existingUser.setUserName(userBasicInfoInputDTO.getUserName());
        existingUser.setEmail(userBasicInfoInputDTO.getEmail());
        existingUser.setPassword(userBasicInfoInputDTO.getPassword());
        existingUser.setRole(userBasicInfoInputDTO.getRole());

        User updatedUser = userRepository.save(existingUser);
        log.info("Successfully updated basic info for user with ID: {}", id);
        return userMapper.toDTO(updatedUser);
    }

    @Override
    public UserDTO updateUserDetails(Long id, UserDetailsInputDTO userDetailsInputDTO) {
        log.info("Updating details for user with ID: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Cannot update user details: No user found with ID " + id));
        userMapper.updateEntityWithDetails(existingUser, userDetailsInputDTO);
        User updatedUser = userRepository.save(existingUser);
        log.info("Successfully updated detailed info for user with ID: {}", id);
        recommendedDailyIntakeService.getOrCreateDailyIntakeForUser(id);
        return userMapper.toDTO(updatedUser);
    }

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

    @Override
    public UserDTO getUserById(Long id) {
        log.info("Retrieving user with ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No user found with ID " + id));
        log.info("Successfully retrieved user with ID: {}", id);
        return userMapper.toDTO(user);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            log.warn("Attempted to delete non-existing user with ID: {}", id);
            throw new UserNotFoundException("Cannot delete user: No user found with ID " + id);
        }
        userRepository.deleteById(id);
        log.info("Successfully deleted user with ID: {}", id);
    }
}

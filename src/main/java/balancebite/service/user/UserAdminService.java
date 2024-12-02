package balancebite.service.user;

import balancebite.dto.user.UserDTO;
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
import balancebite.service.interfaces.IUserAdminService;
import balancebite.utils.UserUpdateHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserAdminService implements IUserAdminService {

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
    public UserAdminService(UserRepository userRepository,
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
     * Updates basic information of an existing user.
     * Only accessible by admins.
     *
     * @param userRegistrationInputDTO The input DTO containing the user ID and updated user information.
     * @return The updated UserDTO.
     */
    public UserDTO updateUserBasicInfoForAdmin(UserRegistrationInputDTO userRegistrationInputDTO) {
        log.info("Admin attempting to update user with ID: {}", userRegistrationInputDTO.getId());

        // Fetch the user by ID
        User existingUser = userUpdateHelper.fetchUserById(userRegistrationInputDTO.getId());

        // Update username if provided
        if (userRegistrationInputDTO.getUserName() != null) {
            log.debug("Updating username for user ID {}: '{}' -> '{}'",
                    existingUser.getId(), existingUser.getUserName(), userRegistrationInputDTO.getUserName());
            existingUser.setUserName(userRegistrationInputDTO.getUserName());
        }

        // Update roles if roles are provided
        if (userRegistrationInputDTO.getRoles() != null && !userRegistrationInputDTO.getRoles().isEmpty()) {
            log.debug("Updating roles for user ID {}: Current Roles = {}, New Roles = {}",
                    existingUser.getId(),
                    existingUser.getRoles().stream().map(Role::getRoleName).toList(),
                    userRegistrationInputDTO.getRoles());

            Set<Role> roles = userRegistrationInputDTO.getRoles().stream()
                    .map(roleName -> {
                        try {
                            return new Role(UserRole.valueOf(roleName)); // Convert String to Role Enum
                        } catch (IllegalArgumentException e) {
                            log.warn("Invalid role provided: {}", roleName);
                            throw new RuntimeException("Invalid role provided: " + roleName);
                        }
                    })
                    .collect(Collectors.toSet());
            existingUser.setRoles(roles);
        }

        // Update email if provided
        if (userRegistrationInputDTO.getEmail() != null) {
            log.debug("Updating email for user ID {}: '{}' -> '{}'",
                    existingUser.getId(), existingUser.getEmail(), userRegistrationInputDTO.getEmail());
            userUpdateHelper.validateUniqueEmail(userRegistrationInputDTO.getEmail(), existingUser.getId());
            existingUser.setEmail(userRegistrationInputDTO.getEmail());
        }

        // Save and return the updated user
        User updatedUser = userRepository.save(existingUser);
        log.info("Successfully updated user with ID: {}. Updated Username: '{}', Updated Roles: '{}', Updated Email: '{}'",
                updatedUser.getId(),
                updatedUser.getUserName(),
                updatedUser.getRoles().stream().map(Role::getRoleName).toList(),
                updatedUser.getEmail());

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
}

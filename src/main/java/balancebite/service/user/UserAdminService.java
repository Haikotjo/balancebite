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
    public UserAdminService(UserRepository userRepository,
                       UserMapper userMapper,
                       RecommendedDailyIntakeRepository recommendedDailyIntakeRepository,
                       RecommendedDailyIntakeService recommendedDailyIntakeService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.recommendedDailyIntakeRepository = recommendedDailyIntakeRepository;
        this.recommendedDailyIntakeService = recommendedDailyIntakeService;
    }

    /**
     * Updates basic information of an existing user.
     * Only accessible by admins.
     *
     * @param id                    The ID of the user to update.
     * @param userRegistrationInputDTO The input DTO containing updated user information.
     * @return The updated UserDTO.
     * @throws UserNotFoundException       If the user with the specified ID does not exist.
     * @throws EntityAlreadyExistsException If the provided email is already in use by another user.
     */
    @Override
    public UserDTO updateUserBasicInfoForAdmin(Long id, UserRegistrationInputDTO userRegistrationInputDTO) {
        log.info("Updating basic info for user with ID: {}", id);

        // Fetch the existing user or throw exception if not found
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Cannot update user: No user found with ID " + id));

        // Check for duplicate email and ensure it belongs to another user
        if (userRepository.existsByEmail(userRegistrationInputDTO.getEmail()) &&
                !existingUser.getEmail().equals(userRegistrationInputDTO.getEmail())) {
            String errorMessage = "The email " + userRegistrationInputDTO.getEmail() + " is already in use by another account.";
            log.warn(errorMessage);
            throw new EntityAlreadyExistsException(errorMessage);
        }

        // Update basic user details
        existingUser.setUserName(userRegistrationInputDTO.getUserName());
        existingUser.setEmail(userRegistrationInputDTO.getEmail());

        // Update roles if roles are provided (handled for admin logic only)
        if (userRegistrationInputDTO.getRoles() != null && !userRegistrationInputDTO.getRoles().isEmpty()) {
            Set<Role> roles = userRegistrationInputDTO.getRoles().stream()
                    .map(roleName -> new Role(UserRole.valueOf(roleName))) // Convert String to Role
                    .collect(Collectors.toSet());
            existingUser.setRoles(roles); // Overwrite all existing roles
        }

        // Save and return the updated user
        User updatedUser = userRepository.save(existingUser);
        log.info("Successfully updated basic info for user with ID: {}", id);
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

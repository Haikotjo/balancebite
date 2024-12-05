package balancebite.service.user;

import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserRegistrationInputDTO;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.mapper.UserMapper;
import balancebite.model.Meal;
import balancebite.model.user.Role;
import balancebite.model.user.User;
import balancebite.model.user.UserRole;
import balancebite.repository.MealRepository;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import balancebite.service.RecommendedDailyIntakeService;
import balancebite.service.interfaces.user.IUserAdminService;
import balancebite.utils.UserUpdateHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
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
    private final MealRepository mealRepository;

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
     * @param mealRepository
     */
    public UserAdminService(UserRepository userRepository,
                            UserMapper userMapper,
                            RecommendedDailyIntakeRepository recommendedDailyIntakeRepository,
                            RecommendedDailyIntakeService recommendedDailyIntakeService,
                            UserUpdateHelper userUpdateHelper,
                            MealRepository mealRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.recommendedDailyIntakeRepository = recommendedDailyIntakeRepository;
        this.recommendedDailyIntakeService = recommendedDailyIntakeService;
        this.userUpdateHelper = userUpdateHelper;
        this.mealRepository = mealRepository;
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

    /**
     * Deletes a user by their ID.
     *
     * This method removes all meals with `isTemplate == false` associated with the user
     * and ensures template meals (`isTemplate == true`) have their `createdBy` reference removed.
     * All relationships in the `user_meals` join table are handled automatically by Hibernate.
     * After processing the meals, the user is deleted from the database.
     *
     * @param id The ID of the user to delete.
     * @throws UserNotFoundException If the user with the specified ID does not exist.
     */
    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        // Fetch the user, or throw an exception if they do not exist
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Cannot delete user: No user found with ID " + id));

        // Fetch meals where the user is referenced as createdBy or adjustedBy
        List<Meal> mealsToUpdate = mealRepository.findByCreatedByOrAdjustedBy(user);

        // Iterate through the meals and unlink the user from createdBy/adjustedBy
        for (Meal meal : mealsToUpdate) {
            if (meal.getCreatedBy() != null && meal.getCreatedBy().equals(user)) {
                log.info("Unlinking user from 'createdBy' for meal ID: {}", meal.getId());
                meal.setCreatedBy(null);
            }
            if (meal.getAdjustedBy() != null && meal.getAdjustedBy().equals(user)) {
                log.info("Unlinking user from 'adjustedBy' for meal ID: {}", meal.getId());
                meal.setAdjustedBy(null);
            }
            mealRepository.save(meal); // Persist changes
        }

        // Remove meals associated with the user and delete non-template meals
        Iterator<Meal> mealIterator = user.getMeals().iterator();
        while (mealIterator.hasNext()) {
            Meal meal = mealIterator.next();
            if (!meal.isTemplate()) {
                log.info("Deleting non-template meal with ID: {}", meal.getId());
                mealIterator.remove(); // Remove from user's meal list
                mealRepository.delete(meal); // Delete the meal itself
            }
        }

        // Flush changes to ensure join table updates are persisted
        log.info("Flushing changes to the database before deleting the user.");
        userRepository.save(user); // Save updated user state
        userRepository.flush();

        // Finally, delete the user
        userRepository.delete(user);
        log.info("Successfully deleted user with ID: {}", id);
    }



}

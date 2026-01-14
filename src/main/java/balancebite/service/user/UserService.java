package balancebite.service.user;

import balancebite.dto.user.*;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.mapper.UserMapper;
import balancebite.model.Nutrient;
import balancebite.model.meal.Meal;
import balancebite.model.RecommendedDailyIntake;
import balancebite.model.user.User;
import balancebite.repository.MealRepository;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import balancebite.service.RecommendedDailyIntakeService;
import balancebite.service.interfaces.user.IUserService;
import balancebite.utils.DailyIntakeCalculatorUtil;
import balancebite.utils.UserUpdateHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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
    public UserService(UserRepository userRepository,
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

        // 1. Fetch user
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID " + id));

        // 2. Update user entity fields
        userMapper.updateDetailsFromDTO(user, userDetailsInputDTO);
        user = userRepository.save(user);

        // 3. Calculate new intake based on your model (this returns a new RecommendedDailyIntake object)
        RecommendedDailyIntake calculatedValues = DailyIntakeCalculatorUtil.calculateDailyIntake(user);

        // 4. Update Today's Intake (Sync values inside the existing set)
        RecommendedDailyIntake todaysIntake = DailyIntakeCalculatorUtil.getOrCreateDailyIntakeForUser(user);
        syncNutrientSet(todaysIntake.getNutrients(), calculatedValues.getNutrients());
        recommendedDailyIntakeRepository.save(todaysIntake);

        // 5. Handle BaseRDI (Always overwrite existing set)
        RecommendedDailyIntake baseRDI = user.getBaseRecommendedDailyIntake();
        if (baseRDI == null) {
            log.info("Creating new BaseRDI for user ID {}", id);
            baseRDI = new RecommendedDailyIntake();
            baseRDI.setUser(user);
            baseRDI.setCreatedAt(LocalDate.now());
            user.setBaseRecommendedDailyIntake(baseRDI);
        }

        // Overwrite the values in the BaseRDI nutrient set
        syncNutrientSet(baseRDI.getNutrients(), calculatedValues.getNutrients());

        // 6. Save everything
        recommendedDailyIntakeRepository.save(baseRDI);
        userRepository.save(user);

        log.info("Successfully updated and synced BaseRDI for user ID {}", id);
        return userMapper.toDTO(user);
    }

    /**
     * Updates ONLY the weight of the currently logged-in user.
     * This is used for quick daily updates.
     *
     * @param id                   The ID of the user (from JWT).
     * @param weightUpdateInputDTO The small DTO containing only the new weight.
     * @return The updated UserDTO including the new history point for the chart.
     * @throws UserNotFoundException If no user with the specified ID is found.
     */
    @Override
    public UserDTO updateWeightOnly(Long id, WeightEntryUpdateInputDTO weightUpdateInputDTO) {
        log.info("Quick weight update for user ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Cannot update weight: No user found with ID " + id));

        user.addWeight(weightUpdateInputDTO.getWeight());
        user = userRepository.save(user);

        RecommendedDailyIntake calculatedValues = DailyIntakeCalculatorUtil.calculateDailyIntake(user);

        // Update Today's Intake
        RecommendedDailyIntake todaysIntake = DailyIntakeCalculatorUtil.getOrCreateDailyIntakeForUser(user);
        syncNutrientSet(todaysIntake.getNutrients(), calculatedValues.getNutrients());
        recommendedDailyIntakeRepository.save(todaysIntake);

        // Update BaseRDI with new weight
        RecommendedDailyIntake baseRDI = user.getBaseRecommendedDailyIntake();
        if (baseRDI != null) {
            syncNutrientSet(baseRDI.getNutrients(), calculatedValues.getNutrients());
            recommendedDailyIntakeRepository.save(baseRDI);
        }

        return userMapper.toDTO(user);
    }

    /**
     * Updates ONLY the target weight of the currently logged-in user.
     *
     * @param id                   The ID of the user (from JWT).
     * @param targetWeightUpdateDTO A small DTO containing only the new target weight.
     * @return The updated UserDTO.
     * @throws UserNotFoundException If no user with the specified ID is found.
     */
    @Override
    public UserDTO updateTargetWeightOnly(Long id, TargetWeightUpdateInputDTO targetWeightUpdateDTO) {
        log.info("Quick target weight update for user ID: {}", id);

        // Fetch user
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Cannot update target weight: No user found with ID " + id));

        // Update the field
        user.setTargetWeight(targetWeightUpdateDTO.getTargetWeight());

        // Save
        userRepository.save(user);

        log.info("Successfully updated target weight for user ID: {}. New target: {}", id, targetWeightUpdateDTO.getTargetWeight());

        return userMapper.toDTO(user);
    }

    /**
     * Retrieves the currently logged-in user's details.
     *
     * @param userId The ID of the currently logged-in user (extracted from JWT token).
     * @return The UserDTO of the logged-in user.
     * @throws UserNotFoundException If no user with the specified ID is found.
     */
    @Override
    public UserDTO getOwnDetails(Long userId) {
        log.info("Retrieving details for the currently logged-in user with ID: {}", userId);

        // Fetch user or throw exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("No user found with ID " + userId));

        log.info("Successfully retrieved details for logged-in user with ID: {}", userId);
        return userMapper.toDTO(user);
    }


    /**
     * Deletes the currently logged-in user by their ID.
     *
     * This method removes all meals with `isTemplate == false` associated with the user
     * and ensures template meals (`isTemplate == true`) have their `createdBy` reference removed.
     * All relationships in the `user_meals` join table are handled automatically by Hibernate.
     * After processing the meals, the user is deleted from the database.
     *
     * @param id The ID of the logged-in user to delete (extracted from the JWT token).
     * @throws UserNotFoundException If the user with the specified ID does not exist.
     */
    @Override
    public void deleteLoggedInUser(Long id) {
        log.info("Deleting logged-in user with ID: {}", id);

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
        log.info("Flushing changes to the database before deleting the logged-in user.");
        userRepository.save(user); // Save updated user state
        userRepository.flush();

        // Finally, delete the logged-in user
        userRepository.delete(user);
        log.info("Successfully deleted logged-in user with ID: {}", id);
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No user found with ID: " + id));
    }

    @Override
    public List<UserSearchDTO> searchUsersByName(String query) {
        List<User> users = userRepository.findByUserNameContainingIgnoreCase(query);

        return users.stream()
                .filter(user ->
                        user.getMeals().stream().anyMatch(meal -> !meal.isPrivate()) ||
                                user.getDietPlans().stream().anyMatch(diet -> !diet.isPrivate())
                )
                .map(user -> new UserSearchDTO(user.getId(), user.getUserName()))
                .collect(Collectors.toList());
    }

    /**
     * Helper method to update values within an existing nutrient set.
     * This prevents "Multiple representations of the same entity" by not replacing the set/objects.
     */
    private void syncNutrientSet(Set<Nutrient> targetSet, Set<Nutrient> sourceSet) {
        for (Nutrient source : sourceSet) {
            targetSet.stream()
                    .filter(target -> target.getName().equals(source.getName()))
                    .findFirst()
                    .ifPresent(target -> target.setValue(source.getValue()));
        }
    }
}


package balancebite.service.user;

import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.mapper.UserMapper;
import balancebite.model.Meal;
import balancebite.model.User;
import balancebite.repository.MealRepository;
import balancebite.repository.UserRepository;
import balancebite.service.interfaces.IUserMealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class responsible for managing the relationship between users and meals.
 * This includes adding and removing meals to/from a user's list of meals.
 */
@Service
@Transactional
public class UserMealService implements IUserMealService {

    private static final Logger log = LoggerFactory.getLogger(UserMealService.class);

    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final UserMapper userMapper;

    public UserMealService(UserRepository userRepository, MealRepository mealRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.userMapper = userMapper;
    }

    /**
     * Adds a meal to a user's set of meals and increments the user count for that meal.
     * If either the user or meal is not found, a custom exception is thrown.
     *
     * @param userId The ID of the user to whom the meal will be added.
     * @param mealId The ID of the meal to be added to the user.
     * @return UserDTO The updated user information with the added meal.
     * @throws UserNotFoundException If the user is not found.
     * @throws MealNotFoundException If the meal is not found.
     */
    /**
     * Adds a meal to a user's set of meals and increments the user count for that meal
     * only if the user has not already added this meal.
     * If either the user or meal is not found, a custom exception is thrown.
     *
     * @param userId The ID of the user to whom the meal will be added.
     * @param mealId The ID of the meal to be added to the user.
     * @return UserDTO The updated user information with the added meal.
     * @throws UserNotFoundException If the user is not found.
     * @throws MealNotFoundException If the meal is not found.
     */
    @Override
    public UserDTO addMealToUser(Long userId, Long mealId) {
        log.info("Attempting to add meal with ID: {} to user with ID: {}", mealId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal not found with ID: " + mealId));

        // Check if the user has already added this meal to prevent duplicate additions
        if (!user.getMeals().contains(meal)) {
            user.getMeals().add(meal);
            meal.incrementUserCount(); // Increment the user count in the meal entity
        } else {
            log.info("User with ID: {} has already added meal with ID: {}", userId, mealId);
        }

        User updatedUser = userRepository.save(user);
        log.info("Successfully added meal with ID: {} to user with ID: {}", mealId, userId);

        return userMapper.toDTO(updatedUser);
    }


    @Override
    public UserDTO removeMealFromUser(Long userId, Long mealId) {
        log.info("Attempting to remove meal from user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Meal meal = user.getMeals().stream()
                .filter(m -> m.getId().equals(mealId))
                .findFirst()
                .orElseThrow(() -> new MealNotFoundException("The meal with ID " + mealId + " is not part of the user's meal list."));

        user.getMeals().remove(meal);
        User updatedUser = userRepository.save(user);

        log.info("Successfully removed meal from user with ID: {}", userId);
        return userMapper.toDTO(updatedUser);
    }
}

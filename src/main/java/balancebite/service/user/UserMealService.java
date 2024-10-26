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

    @Override
    public UserDTO addMealToUser(Long userId, Long mealId) {
        log.info("Attempting to add meal to user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException(mealId));

        user.getMeals().add(meal);
        User updatedUser = userRepository.save(user);
        log.info("Successfully added meal to user with ID: {}", userId);
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

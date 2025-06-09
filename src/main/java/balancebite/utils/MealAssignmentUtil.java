package balancebite.utils;

import balancebite.errorHandling.DuplicateMealException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.model.meal.Meal;
import balancebite.model.user.User;
import balancebite.repository.MealRepository;
import balancebite.repository.UserRepository;
import balancebite.service.interfaces.user.IUserMealService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MealAssignmentUtil {

    private static final Logger log = LoggerFactory.getLogger(MealAssignmentUtil.class);

    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final IUserMealService userMealService;

    public MealAssignmentUtil(UserRepository userRepository,
                              MealRepository mealRepository,
                              IUserMealService userMealService) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.userMealService = userMealService;
    }

    public Meal getOrAddMealToUser(Long userId, Long mealId) {
        if (mealId == null) {
            throw new IllegalArgumentException("Meal ID must not be null.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // 1. Check if user already has this meal or a copy
        Optional<Meal> existingMeal = user.getMeals().stream()
                .filter(m -> m.getId().equals(mealId) ||
                        (m.getOriginalMealId() != null && m.getOriginalMealId().equals(mealId)))
                .findFirst();

        if (existingMeal.isPresent()) {
            return existingMeal.get();
        }

        // 2. Check if user is the creator of the meal
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + mealId));

        if (meal.getCreatedBy() != null && meal.getCreatedBy().getId().equals(userId)) {
            log.info("User {} is the creator of meal {}. Re-linking instead of copying.", userId, mealId);
            user.getMeals().add(meal);
            userRepository.save(user);
            return meal;
        }

        // 3. Otherwise, make a copy
        log.info("Meal with ID {} not found for user {}. Creating a copy...", mealId, userId);
        try {
            userMealService.addMealToUser(userId, mealId);
        } catch (DuplicateMealException e) {
            log.warn("Meal already exists for user {}. Using existing meal instead.", userId);
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found after adding meal"))
                .getMeals().stream()
                .filter(m -> m.getOriginalMealId() != null && m.getOriginalMealId().equals(mealId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Meal copy not found after adding for user ID: " + userId));
    }
}

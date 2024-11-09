package balancebite.service.user;

import balancebite.dto.NutrientInfoDTO;
import balancebite.errorHandling.DailyIntakeNotFoundException;
import balancebite.errorHandling.DailyIntakeUpdateException;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.model.Meal;
import balancebite.model.Nutrient;
import balancebite.model.RecommendedDailyIntake;
import balancebite.model.User;
import balancebite.repository.MealRepository;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import balancebite.service.MealService;
import balancebite.service.interfaces.IConsumeMealService;
import balancebite.utils.HelperMethods;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static balancebite.utils.HelperMethods.normalizeNutrientName;

/**
 * Service class responsible for handling the consumption of meals by users.
 * This class processes the nutrient intake, updates the user's recommended daily intake,
 * and keeps track of the remaining intake values.
 */
@Service
@Transactional
public class ConsumeMealService implements IConsumeMealService {

    private static final Logger log = LoggerFactory.getLogger(ConsumeMealService.class);

    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final RecommendedDailyIntakeRepository recommendedDailyIntakeRepository;
    private final MealService mealService;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Constructor to initialize the ConsumeMealService with the required repositories.
     *
     * @param userRepository Repository to interact with User data in the database.
     * @param mealRepository Repository to interact with Meal data in the database.
     * @param recommendedDailyIntakeRepository Repository to interact with RecommendedDailyIntake data in the database.
     * @param mealService Service to interact with Meal nutrient calculations.
     */
    public ConsumeMealService(UserRepository userRepository, MealRepository mealRepository,
                              RecommendedDailyIntakeRepository recommendedDailyIntakeRepository, MealService mealService) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.recommendedDailyIntakeRepository = recommendedDailyIntakeRepository;
        this.mealService = mealService;
    }

    /**
     * Processes the consumption of a meal by a user, updating the user's intake of nutrients for the current day.
     * The method retrieves the nutrients of the meal, deducts them from the recommended daily intake for today,
     * and updates the remaining intake for each nutrient. The updated intake values are then saved
     * for the user in the RecommendedDailyIntake.
     *
     * @param userId The ID of the user consuming the meal.
     * @param mealId The ID of the meal being consumed.
     * @return A map containing the remaining daily intake for each nutrient after the meal consumption for today.
     * @throws UserNotFoundException if the user is not found.
     * @throws MealNotFoundException if the meal is not found.
     * @throws DailyIntakeNotFoundException if today's daily intake is not found for the user.
     * @throws DailyIntakeUpdateException if an error occurs while saving updated intake.
     */
    @Override
    @Transactional
    public Map<String, Double> consumeMeal(Long userId, Long mealId) {
        log.debug("Starting meal consumption process for user ID: {} and meal ID: {}", userId, mealId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", userId);
                    return new UserNotFoundException("User not found with ID " + userId);
                });
        log.debug("User with ID: {} retrieved", userId);

        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> {
                    log.error("Meal with ID {} not found", mealId);
                    return new MealNotFoundException("Meal not found with ID " + mealId);
                });
        log.debug("Meal with ID: {} retrieved", mealId);

        Map<String, NutrientInfoDTO> mealNutrients = mealService.calculateNutrients(mealId);
        log.debug("Nutrient values for meal with ID: {} calculated", mealId);

        LocalDate startOfToday = LocalDate.now();
        RecommendedDailyIntake dailyIntake = recommendedDailyIntakeRepository
                .findByUser_IdAndCreatedAt(userId, startOfToday)
                .orElseThrow(() -> {
                    log.error("No recommended daily intake found for user ID {} on {}", userId, startOfToday);
                    return new DailyIntakeNotFoundException("Recommended daily intake for today not found for user with ID " + userId);
                });
        log.debug("Daily intake for user ID: {} retrieved for {}", userId, startOfToday);

        Map<String, Nutrient> nutrientMap = dailyIntake.getNutrients().stream()
                .collect(Collectors.toMap(
                        nutrient -> normalizeNutrientName(nutrient.getName()),
                        nutrient -> nutrient
                ));
        log.debug("Nutrients mapped for user ID: {}", userId);

        mealNutrients.forEach((originalNutrientName, nutrientInfo) -> {
            String normalizedNutrientName = normalizeNutrientName(originalNutrientName);

            if (nutrientMap.containsKey(normalizedNutrientName)) {
                Nutrient nutrient = nutrientMap.get(normalizedNutrientName);
                double currentValue = HelperMethods.getValueOrDefault(nutrient.getValue());
                double nutrientValue = HelperMethods.getValueOrDefault(nutrientInfo.getValue());

                double newValue = currentValue - nutrientValue;
                nutrient.setValue(newValue);

                log.info("Nutrient {}: Initial = {}, Consumed = {}, Remaining = {}",
                        normalizedNutrientName, currentValue, nutrientValue, newValue);
            } else {
                log.warn("Nutrient {} not found in daily intake for user ID {}", originalNutrientName, userId);
            }
        });

        try {
            recommendedDailyIntakeRepository.save(dailyIntake);
            entityManager.flush();
            log.info("Updated daily intake for user ID: {} saved", userId);
        } catch (DataAccessException e) {
            log.error("Error saving updated daily intake for user ID {}: {}", userId, e.getMessage(), e);
            throw new DailyIntakeUpdateException("Failed to update daily intake for user with ID " + userId, e);
        }

        return dailyIntake.getNutrients().stream()
                .collect(Collectors.toMap(Nutrient::getName, nutrient -> nutrient.getValue() != null ? nutrient.getValue() : 0.0));
    }
}

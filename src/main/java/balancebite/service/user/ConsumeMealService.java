package balancebite.service.user;

import balancebite.dto.NutrientInfoDTO;
import balancebite.errorHandling.DailyIntakeNotFoundException;
import balancebite.errorHandling.DailyIntakeUpdateException;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.model.Meal;
import balancebite.model.Nutrient;
import balancebite.model.RecommendedDailyIntake;
import balancebite.model.user.User;
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
 * Service class responsible for processing meal consumption by users.
 * This includes retrieving meal nutrient data, updating daily intake values,
 * and logging all operations and exceptions in detail.
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
     * Constructor to initialize the ConsumeMealService with required dependencies.
     *
     * @param userRepository Repository for User data.
     * @param mealRepository Repository for Meal data.
     * @param recommendedDailyIntakeRepository Repository for RecommendedDailyIntake data.
     * @param mealService Service for calculating Meal nutrient data.
     */
    public ConsumeMealService(UserRepository userRepository, MealRepository mealRepository,
                              RecommendedDailyIntakeRepository recommendedDailyIntakeRepository, MealService mealService) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.recommendedDailyIntakeRepository = recommendedDailyIntakeRepository;
        this.mealService = mealService;
    }

    /**
     * Consumes a meal for a specific user by updating the user's daily nutrient intake.
     * Nutrient values from the meal are subtracted from the user's recommended daily intake.
     *
     * @param userId The ID of the user consuming the meal.
     * @param mealId The ID of the meal being consumed.
     * @return A map of nutrient names and their remaining values after consumption.
     * @throws UserNotFoundException If the user with the given ID is not found.
     * @throws MealNotFoundException If the meal with the given ID is not found.
     * @throws DailyIntakeNotFoundException If the user's recommended daily intake for today is not found.
     * @throws DailyIntakeUpdateException If an error occurs while saving the updated daily intake.
     */
    @Override
    @Transactional
    public Map<String, Double> consumeMeal(Long userId, Long mealId) {
        log.info("Starting meal consumption process for user ID: {} and meal ID: {}", userId, mealId);

        // Retrieve user and meal
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found during meal consumption.", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });
        log.debug("User retrieved: {}", user);

        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> {
                    log.error("Meal with ID {} not found during consumption process.", mealId);
                    return new MealNotFoundException("Meal not found with ID: " + mealId);
                });
        log.debug("Meal retrieved: {}", meal);

        // Calculate nutrients in the meal
        Map<String, NutrientInfoDTO> mealNutrients = mealService.calculateNutrients(mealId);
        log.debug("Nutrient values for meal ID {} calculated: {}", mealId, mealNutrients);

        // Retrieve recommended daily intake for today
        LocalDate today = LocalDate.now();
        RecommendedDailyIntake dailyIntake = recommendedDailyIntakeRepository
                .findByUser_IdAndCreatedAt(userId, today)
                .orElseThrow(() -> {
                    log.error("Daily intake not found for user ID {} on {}", userId, today);
                    return new DailyIntakeNotFoundException("Recommended daily intake not found for user with ID: " + userId);
                });
        log.debug("Daily intake retrieved for user ID {}: {}", userId, dailyIntake);

        // Map nutrients by normalized names
        Map<String, Nutrient> nutrientMap = dailyIntake.getNutrients().stream()
                .collect(Collectors.toMap(
                        nutrient -> normalizeNutrientName(nutrient.getName()),
                        nutrient -> nutrient
                ));
        log.debug("Mapped nutrients for daily intake: {}", nutrientMap);

        // Update nutrient values based on the meal consumed
        mealNutrients.forEach((originalName, nutrientInfo) -> {
            String normalizedName = normalizeNutrientName(originalName);

            if (nutrientMap.containsKey(normalizedName)) {
                Nutrient nutrient = nutrientMap.get(normalizedName);
                double currentValue = HelperMethods.getValueOrDefault(nutrient.getValue());
                double consumedValue = HelperMethods.getValueOrDefault(nutrientInfo.getValue());
                double newValue = currentValue - consumedValue;

                nutrient.setValue(newValue);
                log.info("Nutrient '{}' updated: Initial = {}, Consumed = {}, Remaining = {}",
                        normalizedName, currentValue, consumedValue, newValue);
            } else {
                log.warn("Nutrient '{}' not found in daily intake for user ID {}. Skipping update.", originalName, userId);
            }
        });

        // Save the updated daily intake
        try {
            recommendedDailyIntakeRepository.save(dailyIntake);
            entityManager.flush();
            log.info("Daily intake for user ID {} successfully updated and saved.", userId);
        } catch (DataAccessException e) {
            log.error("Error saving updated daily intake for user ID {}: {}", userId, e.getMessage(), e);
            throw new DailyIntakeUpdateException("Failed to update daily intake for user with ID: " + userId, e);
        }

        // Return remaining daily intake
        return dailyIntake.getNutrients().stream()
                .collect(Collectors.toMap(
                        Nutrient::getName,
                        nutrient -> nutrient.getValue() != null ? nutrient.getValue() : 0.0
                ));
    }
}

package balancebite.service;

import balancebite.dto.NutrientInfoDTO;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.model.Meal;
import balancebite.model.Nutrient;
import balancebite.model.RecommendedDailyIntake;
import balancebite.model.User;
import balancebite.repository.MealRepository;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
     */
    @Override
    @Transactional
    public Map<String, Double> consumeMeal(Long userId, Long mealId) {
        // Retrieve the user by their ID, throw UserNotFoundException if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID " + userId));

        // Retrieve the meal by its ID, throw MealNotFoundException if not found
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal not found with ID " + mealId));

        // Retrieve the nutrient values of the meal using mealService
        Map<String, NutrientInfoDTO> mealNutrients = mealService.calculateNutrients(mealId);

        // Get the user's existing recommended daily intake for today
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();

        RecommendedDailyIntake recommendedDailyIntake = recommendedDailyIntakeRepository
                .findByUser_IdAndCreatedAt(userId, startOfToday)
                .orElseThrow(() -> new RuntimeException("Recommended daily intake for today not found for user with ID " + userId));

        // Retrieve the recommended daily intake values and normalize the keys
        Map<String, Nutrient> nutrientMap = recommendedDailyIntake.getNutrients().stream()
                .collect(Collectors.toMap(
                        nutrient -> normalizeNutrientName(nutrient.getName()),
                        nutrient -> nutrient
                ));

        // Subtract the nutrients from the meal from the recommended daily intake
        for (Map.Entry<String, NutrientInfoDTO> entry : mealNutrients.entrySet()) {
            String originalNutrientName = entry.getKey();
            String normalizedNutrientName = normalizeNutrientName(originalNutrientName);

            NutrientInfoDTO nutrientInfo = entry.getValue();

            // Check if the normalized nutrient exists in the recommended intake
            if (nutrientMap.containsKey(normalizedNutrientName)) {
                Nutrient nutrient = nutrientMap.get(normalizedNutrientName);

                // Handle null nutrient value
                double currentValue = nutrient.getValue() != null ? nutrient.getValue() : 0.0;
                double nutrientValue = nutrientInfo.getValue() != null ? nutrientInfo.getValue() : 0.0;

                // Subtract the nutrient value from the daily intake
                double remainingIntake = currentValue - nutrientValue;
                nutrient.setValue(remainingIntake);
            }
        }

        // Save the updated RecommendedDailyIntake and ensure the changes are flushed to the database
        try {
            recommendedDailyIntakeRepository.save(recommendedDailyIntake);
            entityManager.flush();
            log.info("Successfully saved updated recommended daily intake for user with ID: {}", userId);
        } catch (Exception e) {
            log.error("Error saving RecommendedDailyIntake: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update recommended daily intake for user with ID " + userId);
        }

        // Return the remaining intake for each nutrient to the client
        return recommendedDailyIntake.getNutrients().stream()
                .collect(Collectors.toMap(Nutrient::getName, nutrient -> nutrient.getValue() != null ? nutrient.getValue() : 0.0));
    }

    /**
     * Normalizes nutrient names by converting them to lowercase, removing units like "g", "mg", and "µg"
     * only at the end of the string, and then removing all spaces.
     *
     * @param nutrientName The nutrient name to normalize.
     * @return The normalized nutrient name without units at the end and without spaces.
     */
    private String normalizeNutrientName(String nutrientName) {
        return nutrientName.toLowerCase()
                .replaceAll("\\s(g|mg|µg)$", "")  // Remove " g", " mg", " µg" at the end
                .replace(" ", "");  // Remove all remaining spaces
    }
}

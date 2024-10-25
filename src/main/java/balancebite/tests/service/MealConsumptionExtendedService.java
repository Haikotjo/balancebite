package balancebite.service;

import balancebite.model.Meal;
import balancebite.model.User;
import balancebite.model.RecommendedDailyIntake;
import balancebite.repository.MealRepository;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import balancebite.dto.NutrientInfoDTO;
import balancebite.service.interfaces.IConsumeMealService;
import balancebite.utils.HelperMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static balancebite.utils.HelperMethods.normalizeNutrientName;

@Service
public class MealConsumptionExtendedService implements IConsumeMealService {

    private static final Logger log = LoggerFactory.getLogger(MealConsumptionExtendedService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private MealService mealService;

    @Autowired
    private RecommendedDailyIntakeRepository recommendedDailyIntakeRepository;

    /**
     * Processes the consumption of a meal by a user on a specific date, updating the user's intake of nutrients.
     * The method retrieves the nutrients of the meal, deducts them from the recommended daily intake for that date,
     * and updates the remaining intake for each nutrient. The updated intake values are then saved
     * for the user in the RecommendedDailyIntake.
     *
     * @param userId The ID of the user consuming the meal.
     * @param mealId The ID of the meal being consumed.
     * @param date The date on which the meal was consumed.
     * @return A map containing the remaining daily intake for each nutrient after the meal consumption on the specific date.
     */
    @Transactional
    @Override
    public Map<String, Double> consumeMeal(Long userId, Long mealId) {
        return eatMealForSpecificDate(userId, mealId, LocalDate.now());
    }

    @Transactional
    public Map<String, Double> eatMealForSpecificDate(Long userId, Long mealId, LocalDate date) {
        log.debug("Start consuming meal process for user ID: {} and meal ID: {} on date: {}", userId, mealId, date);

        // Retrieve the user by their ID, throw UserNotFoundException if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID " + userId));
        log.debug("Retrieved user with ID: {}", userId);

        // Retrieve the meal by its ID, throw MealNotFoundException if not found
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Meal not found with ID " + mealId));
        log.debug("Retrieved meal with ID: {}", mealId);

        // Retrieve the nutrient values of the meal using mealService
        Map<String, NutrientInfoDTO> mealNutrients = mealService.calculateNutrients(mealId);
        log.debug("Calculated nutrient values for meal with ID: {}", mealId);

        // Get the user's recommended daily intake for the given date
        Optional<RecommendedDailyIntake> intakeForSpecificDate = recommendedDailyIntakeRepository
                .findByUser_IdAndCreatedAt(userId, date.atStartOfDay());

        if (intakeForSpecificDate.isEmpty()) {
            throw new RuntimeException("Recommended daily intake for the given date not found for user with ID " + userId);
        }

        RecommendedDailyIntake recommendedDailyIntake = intakeForSpecificDate.get();
        log.debug("Retrieved recommended daily intake for user ID: {} for date: {}", userId, date);

        // Retrieve the recommended daily intake values and normalize the keys
        Map<String, Double> recommendedIntakes = recommendedDailyIntake.getNutrients().stream()
                .collect(Collectors.toMap(
                        nutrient -> normalizeNutrientName(nutrient.getName()),
                        nutrient -> HelperMethods.getValueOrDefault(nutrient.getValue())
                ));
        log.debug("Mapped nutrients for recommended daily intake for user ID: {}", userId);

        // Map to store the remaining intake for each nutrient
        Map<String, Double> remainingIntakes = new HashMap<>(recommendedIntakes);

        // Subtract the nutrients from the meal from the recommended daily intake
        for (Map.Entry<String, NutrientInfoDTO> entry : mealNutrients.entrySet()) {
            String originalNutrientName = entry.getKey();
            String normalizedNutrientName = normalizeNutrientName(originalNutrientName);

            NutrientInfoDTO nutrientInfo = entry.getValue();

            // Check if the normalized nutrient exists in the recommended intake
            if (remainingIntakes.containsKey(normalizedNutrientName)) {
                double currentValue = remainingIntakes.get(normalizedNutrientName);
                double nutrientValue = HelperMethods.getValueOrDefault(nutrientInfo.getValue());

                // Subtract the nutrient value from the daily intake
                double remainingIntake = currentValue - nutrientValue;
                remainingIntakes.put(normalizedNutrientName, remainingIntake);
                log.debug("Updated remaining intake for nutrient: {} to value: {}", normalizedNutrientName, remainingIntake);

                // Update the recommended intake for this nutrient in the Nutrient entity
                recommendedDailyIntake.getNutrients().forEach(nutrient -> {
                    if (normalizeNutrientName(nutrient.getName()).equals(normalizedNutrientName)) {
                        nutrient.setValue(remainingIntake);
                    }
                });
            } else {
                log.debug("Nutrient {} not found in recommended daily intake for user ID: {}", originalNutrientName, userId);
            }
        }

        try {
            recommendedDailyIntakeRepository.save(recommendedDailyIntake);
            log.info("Successfully saved updated recommended daily intake for user with ID: {}", userId);
        } catch (DataAccessException e) {
            log.error("Database error occurred while saving RecommendedDailyIntake: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update recommended daily intake for user with ID " + userId, e);
        }

        return remainingIntakes;
    }
}

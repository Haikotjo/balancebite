package balancebite.service;

import balancebite.model.Meal;
import balancebite.model.User;
import balancebite.model.RecommendedDailyIntake;
import balancebite.repository.MealRepository;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import balancebite.dto.NutrientInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MealConsumptionService {

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
    public Map<String, Double> eatMealForSpecificDate(Long userId, Long mealId, LocalDate date) {
        // Retrieve the user by their ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID " + userId));

        // Retrieve the meal by its ID
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Meal not found with ID " + mealId));

        // Retrieve the nutrient values of the meal
        Map<String, NutrientInfoDTO> mealNutrients = mealService.calculateNutrients(mealId);

        // Get the user's recommended daily intake for the given date
        Optional<RecommendedDailyIntake> intakeForSpecificDate = user.getRecommendedDailyIntakes().stream()
                .filter(intake -> intake.getCreatedAt().toLocalDate().equals(date))
                .findFirst();

        if (intakeForSpecificDate.isEmpty()) {
            throw new RuntimeException("Recommended daily intake for the given date not found for user with ID " + userId);
        }

        RecommendedDailyIntake recommendedDailyIntake = intakeForSpecificDate.get();

        // Retrieve the recommended daily intake values and normalize the keys
        Map<String, Double> recommendedIntakes = recommendedDailyIntake.getNutrients().stream()
                .collect(Collectors.toMap(
                        nutrient -> normalizeNutrientName(nutrient.getName()),  // Normalize the nutrient names
                        nutrient -> nutrient.getValue() != null ? nutrient.getValue() : 0.0  // Handle null values
                ));

        // Map to store the remaining intake for each nutrient
        Map<String, Double> remainingIntakes = new HashMap<>(recommendedIntakes);

        // Subtract the nutrients from the meal from the recommended daily intake
        for (Map.Entry<String, NutrientInfoDTO> entry : mealNutrients.entrySet()) {
            String originalNutrientName = entry.getKey();  // Original nutrient name from meal
            String normalizedNutrientName = normalizeNutrientName(originalNutrientName);  // Normalized version of the meal nutrient name

            NutrientInfoDTO nutrientInfo = entry.getValue();

            // Check if the normalized nutrient exists in the recommended intake
            if (remainingIntakes.containsKey(normalizedNutrientName)) {
                // Check if nutrient value is not null before subtracting
                double nutrientValue = nutrientInfo.getValue() != null ? nutrientInfo.getValue() : 0.0;
                // Subtract the nutrient value from the daily intake
                double remainingIntake = remainingIntakes.get(normalizedNutrientName) - nutrientValue;
                remainingIntakes.put(normalizedNutrientName, remainingIntake);  // Update remaining intake

                // Update the recommended intake for this nutrient in the Nutrient entity
                recommendedDailyIntake.getNutrients().forEach(nutrient -> {
                    if (normalizeNutrientName(nutrient.getName()).equals(normalizedNutrientName)) {
                        nutrient.setValue(remainingIntake);  // Allow negative values here
                    }
                });
            }
        }

        // Save the updated RecommendedDailyIntake back to the database
        recommendedDailyIntakeRepository.save(recommendedDailyIntake);

        return remainingIntakes;
    }

    // Helper method to normalize nutrient names (you can adjust this logic as needed)
    private String normalizeNutrientName(String nutrientName) {
        return nutrientName.trim().toLowerCase().replaceAll("\\s+", "_");
    }
}

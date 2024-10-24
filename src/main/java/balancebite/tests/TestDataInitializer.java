package balancebite.tests;

import balancebite.model.User;
import balancebite.repository.UserRepository;
import balancebite.service.MealConsumptionExtendedService;
import balancebite.service.RecommendedDailyIntakeService;
import balancebite.tests.service.TestRecommendedDailyIntakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Optional;

@Configuration
public class TestDataInitializer {

    @Autowired
    private RecommendedDailyIntakeService recommendedDailyIntakeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRecommendedDailyIntakeService testRecommendedDailyIntakeService;

    @Autowired
    private MealConsumptionExtendedService MealConsumptionExtendedService;

    @Bean
    public ApplicationRunner init() {
        return args -> {
            try {
                // Zoek naar de user met ID 1 en voeg gewicht, leeftijd, enz. toe
                updateUserDetails(1L, 80.0, 30, 180.0, "MALE", "ACTIVE", "MAINTENANCE");

                // Zoek naar de user met ID 2 en voeg gewicht, leeftijd, enz. toe
                updateUserDetails(2L, 65.0, 25, 165.0, "FEMALE", "MODERATE", "WEIGHT_LOSS");

                // Zoek naar de user met ID 3 en voeg gewicht, leeftijd, enz. toe
                updateUserDetails(3L, 95.0, 35, 185.0, "MALE", "SEDENTARY", "WEIGHT_GAIN");

                // Zoek naar de user met ID 4 en voeg gewicht, leeftijd, enz. toe
                updateUserDetails(4L, 55.0, 28, 170.0, "FEMALE", "LIGHT", "WEIGHT_GAIN_WITH_MUSCLE_FOCUS");

                // Zoek naar de user met ID 5 en voeg gewicht, leeftijd, enz. toe
                updateUserDetails(5L, 110.0, 41, 185.0, "MALE", "ACTIVE", "MAINTENANCE_WITH_MUSCLE_FOCUS");

                // Zoek naar de user met ID 6 en voeg gewicht, leeftijd, enz. toe
                updateUserDetails(6L, 80.0, 74, 164.0, "FEMALE", "MODERATE", "WEIGHT_LOSS_WITH_MUSCLE_MAINTENANCE");

                // Voor de users met ID 1 t/m 3 een RecommendedDailyIntake aanmaken
                recommendedDailyIntakeService.getOrCreateDailyIntakeForUser(1L);
                recommendedDailyIntakeService.getOrCreateDailyIntakeForUser(2L);
                recommendedDailyIntakeService.getOrCreateDailyIntakeForUser(3L);

                // Create or retrieve RecommendedDailyIntake for user 5 and 6 for multiple days
                LocalDate threeDaysAgo = LocalDate.now().minusDays(3);
                LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
                LocalDate yesterday = LocalDate.now().minusDays(1);
                LocalDate today = LocalDate.now();
                LocalDate tomorrow = LocalDate.now().plusDays(1);
                LocalDate dayAfterTomorrow = LocalDate.now().plusDays(2);

                // For user 5
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(5L, threeDaysAgo);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(5L, twoDaysAgo);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(5L, yesterday);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(5L, tomorrow);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(5L, dayAfterTomorrow);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(5L, today); // Always add 'today' as the last entry to ensure correct processing


                // For user 6
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(6L, threeDaysAgo);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(6L, twoDaysAgo);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(6L, yesterday);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(6L, tomorrow);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(5L, today); // Always add 'today' as the last entry to ensure correct processing


                System.out.println("Recommended daily intake created for users 5 and 6 for the specified days.");

                // Voeg maaltijden toe aan users 5 en 6 voor gisteren met herhalingen
                addMealsForDay(5L, new Long[]{1L}, new int[]{2}, LocalDate.now().minusDays(1)); // Gisteren
                addMealsForDay(5L, new Long[]{2L}, new int[]{3}, LocalDate.now().minusDays(2)); // Twee dagen geleden
                addMealsForDay(5L, new Long[]{3L}, new int[]{1}, LocalDate.now().minusDays(3)); // Drie dagen geleden

//                addMealsForYesterday(6L, new Long[]{4L}, new int[]{20}); // User 6 eet maaltijd 4 twintig keer
                addMealsForDay(6L, new Long[]{4L}, new int[]{20}, LocalDate.now().minusDays(1)); // Gisteren
                addMealsForDay(6L, new Long[]{2L}, new int[]{3}, LocalDate.now().minusDays(2)); // Twee dagen geleden
                addMealsForDay(6L, new Long[]{3L}, new int[]{1}, LocalDate.now().minusDays(3)); // Drie dagen geleden

                System.out.println("Meals added for users 5 and 6 for yesterday.");

            } catch (Exception e) {
                System.err.println("Error in TestDataInitializer: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    // Methode om extra gegevens aan een bestaande user toe te voegen
    private void updateUserDetails(Long userId, Double weight, Integer age, Double height, String gender, String activityLevel, String goal) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setWeight(weight);
            user.setAge(age);
            user.setHeight(height);
            user.setGender(Enum.valueOf(balancebite.model.userenums.Gender.class, gender));
            user.setActivityLevel(Enum.valueOf(balancebite.model.userenums.ActivityLevel.class, activityLevel));
            user.setGoal(Enum.valueOf(balancebite.model.userenums.Goal.class, goal));

            userRepository.save(user);
        } else {
            System.err.println("User with ID " + userId + " not found.");
        }
    }

    // Methode om specifieke maaltijden en herhalingen toe te voegen voor een bepaalde dag
    private void addMealsForDay(Long userId, Long[] mealIds, int[] repetitions, LocalDate day) {

        if (mealIds.length != repetitions.length) {
            throw new IllegalArgumentException("Meal IDs and repetitions array must have the same length.");
        }

        for (int i = 0; i < mealIds.length; i++) {
            for (int j = 0; j < repetitions[i]; j++) {
                MealConsumptionExtendedService.eatMealForSpecificDate(userId, mealIds[i], day);
            }
        }
    }

    // Methode om specifieke maaltijden en herhalingen toe te voegen voor gisteren
    private void addMealsForYesterday(Long userId, Long[] mealIds, int[] repetitions) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        addMealsForDay(userId, mealIds, repetitions, yesterday);
    }

    // Methode om specifieke maaltijden en herhalingen toe te voegen voor twee dagen geleden
    private void addMealsForTwoDaysAgo(Long userId, Long[] mealIds, int[] repetitions) {
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
        addMealsForDay(userId, mealIds, repetitions, twoDaysAgo);
    }

    // Methode om specifieke maaltijden en herhalingen toe te voegen voor drie dagen geleden
    private void addMealsForThreeDaysAgo(Long userId, Long[] mealIds, int[] repetitions) {
        LocalDate threeDaysAgo = LocalDate.now().minusDays(3);
        addMealsForDay(userId, mealIds, repetitions, threeDaysAgo);
    }

}
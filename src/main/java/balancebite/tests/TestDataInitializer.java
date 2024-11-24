package balancebite.tests;

import balancebite.model.user.User;
import balancebite.model.user.userenums.ActivityLevel;
import balancebite.model.user.userenums.Gender;
import balancebite.model.user.userenums.Goal;
import balancebite.repository.UserRepository;
import balancebite.service.MealConsumptionExtendedService;
import balancebite.service.RecommendedDailyIntakeService;
import balancebite.service.user.UserMealService;
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
    private MealConsumptionExtendedService mealConsumptionExtendedService;

    @Autowired
    private UserMealService userMealService;

    @Bean
    public ApplicationRunner init(UserMealService userMealService) {
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

                // Maaltijden kopiëren tussen gebruikers
                userMealService.addMealToUser(2L, 1L); // Maaltijd 1 naar User 2
                userMealService.addMealToUser(2L, 2L); // Maaltijd 2 naar User 2
                userMealService.addMealToUser(2L, 3L); // Maaltijd 3 naar User 2
                userMealService.addMealToUser(1L, 4L); // Maaltijd 4 naar User 1
                userMealService.addMealToUser(1L, 5L); // Maaltijd 5 naar User 1
                userMealService.addMealToUser(1L, 6L); // Maaltijd 6 naar User 1

                // Maak RecommendedDailyIntake aan voor meerdere dagen voor user 5 en 6
                LocalDate threeDaysAgo = LocalDate.now().minusDays(3);
                LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
                LocalDate yesterday = LocalDate.now().minusDays(1);
                LocalDate today = LocalDate.now();
                LocalDate tomorrow = LocalDate.now().plusDays(1);
                LocalDate dayAfterTomorrow = LocalDate.now().plusDays(2);

                // Voor de users met ID 1 t/m 3 een RecommendedDailyIntake aanmaken
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(1L, today);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(2L, today);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(3L, today);

                // Voor user 5
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(5L, threeDaysAgo);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(5L, twoDaysAgo);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(5L, yesterday);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(5L, tomorrow);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(5L, dayAfterTomorrow);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(5L, today); // Voeg 'today' als laatste toe

                // Voor user 6
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(6L, threeDaysAgo);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(6L, twoDaysAgo);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(6L, yesterday);
                testRecommendedDailyIntakeService.createOrRetrieveRecommendedDailyIntakeForDate(6L, today); // Voeg 'today' als laatste toe

                System.out.println("Recommended daily intake created for users 5 and 6 for the specified days.");

                // Voeg maaltijden toe aan users 5 en 6 voor gisteren met herhalingen
                addMealsForDay(5L, new Long[]{1L}, new int[]{2}, LocalDate.now().minusDays(1)); // Gisteren
                addMealsForDay(5L, new Long[]{2L}, new int[]{3}, LocalDate.now().minusDays(2)); // Twee dagen geleden
                addMealsForDay(5L, new Long[]{3L}, new int[]{1}, LocalDate.now().minusDays(3)); // Drie dagen geleden

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
            user.setGender(Enum.valueOf(Gender.class, gender));
            user.setActivityLevel(Enum.valueOf(ActivityLevel.class, activityLevel));
            user.setGoal(Enum.valueOf(Goal.class, goal));

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
                mealConsumptionExtendedService.eatMealForSpecificDate(userId, mealIds[i], day);
            }
        }
    }
}

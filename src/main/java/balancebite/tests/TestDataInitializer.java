package balancebite.tests;

import balancebite.model.User;
import balancebite.repository.UserRepository;
import balancebite.service.MealConsumptionService;
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
    private MealConsumptionService mealConsumptionService;

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

                System.out.println("Users updated with additional details.");

                // Voor de users met ID 1 t/m 3 een RecommendedDailyIntake aanmaken
                recommendedDailyIntakeService.getOrCreateDailyIntakeForUser(1L);
                recommendedDailyIntakeService.getOrCreateDailyIntakeForUser(2L);
                recommendedDailyIntakeService.getOrCreateDailyIntakeForUser(3L);

                // Voor de users met ID 5 en 6 een RecommendedDailyIntake aanmaken voor gisteren, vandaag en morgen
                LocalDate yesterday = LocalDate.now().minusDays(1);
                LocalDate today = LocalDate.now();
                LocalDate tomorrow = LocalDate.now().plusDays(1);

                testRecommendedDailyIntakeService.createRecommendedDailyIntakeForDate(5L, yesterday);
                testRecommendedDailyIntakeService.createRecommendedDailyIntakeForDate(5L, today);
                testRecommendedDailyIntakeService.createRecommendedDailyIntakeForDate(5L, tomorrow);

                testRecommendedDailyIntakeService.createRecommendedDailyIntakeForDate(6L, yesterday);
                testRecommendedDailyIntakeService.createRecommendedDailyIntakeForDate(6L, today);
                testRecommendedDailyIntakeService.createRecommendedDailyIntakeForDate(6L, tomorrow);

                System.out.println("Recommended daily intake created for users 5 and 6 for yesterday, today, and tomorrow.");

                // Voeg maaltijden toe aan users 5 en 6 voor gisteren met herhalingen
                addMealsForYesterday(5L, new Long[]{1L}, new int[]{3}); // User 5 eet maaltijd 1 twee keer en maaltijd 2 drie keer
                addMealsForYesterday(6L, new Long[]{4L}, new int[]{20}); // User 6 eet maaltijd 4 twintig keer

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

    // Methode om specifieke maaltijden en herhalingen toe te voegen voor gisteren
    private void addMealsForYesterday(Long userId, Long[] mealIds, int[] repetitions) {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        if (mealIds.length != repetitions.length) {
            throw new IllegalArgumentException("Meal IDs and repetitions array must have the same length.");
        }

        for (int i = 0; i < mealIds.length; i++) {
            for (int j = 0; j < repetitions[i]; j++) {
                mealConsumptionService.eatMealForSpecificDate(userId, mealIds[i], yesterday);
            }
        }
    }
}

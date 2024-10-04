package balancebite.config;

import balancebite.model.User;
import balancebite.repository.UserRepository;
import balancebite.service.RecommendedDailyIntakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class TestDataInitializer {

    @Autowired
    private RecommendedDailyIntakeService recommendedDailyIntakeService;

    @Autowired
    private UserRepository userRepository;

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
                updateUserDetails(5L, 110.0, 41, 185.0, "MALE", "ACTIVE", "MAINTENANCE_WITH_MUSCLE_MAINTENANCE");

                // Zoek naar de user met ID 6 en voeg gewicht, leeftijd, enz. toe
                updateUserDetails(6L, 75.0, 74, 160.0, "FEMALE", "MODERATE", "WEIGHT_LOSS_WITH_MUSCLE_MAINTENANCE");

                System.out.println("Users updated with additional details.");

                // Voor de users met ID 1 t/m 4 een RecommendedDailyIntake aanmaken
                recommendedDailyIntakeService.createRecommendedDailyIntakeForUser(1L);
                recommendedDailyIntakeService.createRecommendedDailyIntakeForUser(2L);
                recommendedDailyIntakeService.createRecommendedDailyIntakeForUser(3L);
                recommendedDailyIntakeService.createRecommendedDailyIntakeForUser(4L);
                recommendedDailyIntakeService.createRecommendedDailyIntakeForUser(5L);
                recommendedDailyIntakeService.createRecommendedDailyIntakeForUser(6L);

                System.out.println("Recommended daily intake created for users with IDs 1 to 6.");

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
}

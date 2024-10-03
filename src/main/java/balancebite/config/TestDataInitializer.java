package balancebite.config;

import balancebite.service.RecommendedDailyIntakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestDataInitializer {

    @Autowired
    private RecommendedDailyIntakeService recommendedDailyIntakeService;

    @Bean
    public ApplicationRunner init() {
        return args -> {
            try {
                // Voor de users met ID 1 en 2 een RecommendedDailyIntake aanmaken
                recommendedDailyIntakeService.createRecommendedDailyIntakeForUser(1L);
                recommendedDailyIntakeService.createRecommendedDailyIntakeForUser(2L);

                System.out.println("Recommended daily intake created for users with IDs 1 and 2.");

            } catch (Exception e) {
                System.err.println("Error in TestDataInitializer: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}

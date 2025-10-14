package balancebite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

// Simple one-time DB fix runner
@Configuration
public class DbFixConfig {

    @Bean
    public org.springframework.boot.CommandLineRunner fixFoodCategoryCheck(JdbcTemplate jdbc) {
        return args -> {
            try {
                // Drop old CHECK constraint if it exists
                jdbc.execute("ALTER TABLE food_items DROP CONSTRAINT IF EXISTS food_items_food_category_check");
            } catch (Exception ignored) { /* ignore */ }

            try {
                // Recreate CHECK with the new enum value
                jdbc.execute("""
                    ALTER TABLE food_items
                    ADD CONSTRAINT food_items_food_category_check
                    CHECK (food_category IN (
                      'VEGETABLE','FRUIT','MEAT','FISH','DAIRY','GRAIN','LEGUME','NUT','EGG','SWEET',
                      'DRINK','SAUCE','OIL','SPICE','READY_MEAL','SNACK','SUPPLEMENT','PROTEIN_SUPPLEMENT','OTHER'
                    ))
                """);
            } catch (Exception e) {
                // don't crash app; log if you want
            }
        };
    }
}

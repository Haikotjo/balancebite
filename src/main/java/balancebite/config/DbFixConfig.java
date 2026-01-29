package balancebite.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DbFixConfig {
    private static final Logger log = LoggerFactory.getLogger(DbFixConfig.class);

    @Bean
    public CommandLineRunner fixFoodCategoryCheck(JdbcTemplate jdbc) {
        return args -> {
            try { jdbc.execute("SET statement_timeout = 5000"); } catch (Exception ignore) {}

            String dropSql = jdbc.query(
                    """
                    SELECT 'ALTER TABLE public.food_items DROP CONSTRAINT ' || quote_ident(conname) || ';' AS sql
                    FROM pg_constraint c
                    JOIN pg_class t ON c.conrelid = t.oid
                    JOIN pg_namespace n ON t.relnamespace = n.oid
                    WHERE t.relname = 'food_items'
                      AND n.nspname = 'public'
                      AND pg_get_constraintdef(c.oid) LIKE '%food_category%'
                      AND c.contype = 'c'
                    """,
                    (rs, i) -> rs.getString("sql")
            ).stream().findFirst().orElse(null);

            if (dropSql != null) {
                log.info("Dropping constraint via: {}", dropSql);
                try { jdbc.execute(dropSql); } catch (Exception e) { log.warn("Drop failed: {}", e.getMessage()); }
            } else {
                log.info("No existing CHECK constraint found on public.food_items.food_category");
            }

            try {
                jdbc.execute("""
                    ALTER TABLE public.food_items
                    ADD CONSTRAINT food_items_food_category_check
                    CHECK (food_category IN (
                      'VEGETABLE','FRUIT','MEAT','FISH','DAIRY','GRAIN','LEGUME','NUT','EGG','SWEET',
                      'DRINK','SAUCE','OIL','SPICE','READY_MEAL','SNACK','SUPPLEMENT','PROTEIN_SUPPLEMENT','OTHER'
                    ))
                """);
                log.info("Added CHECK constraint with PROTEIN_SUPPLEMENT");
            } catch (Exception e) {
                log.warn("Add CHECK failed (maybe already correct): {}", e.getMessage());
            }
        };
    }

    @Bean
    public CommandLineRunner makeMealImagePublicIdNullable(JdbcTemplate jdbc) {
        return args -> {
            try {
                jdbc.execute("ALTER TABLE public.meal_images ALTER COLUMN public_id DROP NOT NULL");
                log.info("✅ meal_images.public_id is now nullable");
            } catch (Exception e) {
                log.warn("public_id already nullable or alter failed: {}", e.getMessage());
            }
        };
    }

    @Bean
    public CommandLineRunner fixMealImagesFromImageUrl(JdbcTemplate jdbc) {
        return args -> {
            log.info("Starting one-time Meal → MealImage migration");

            try {
                // 1) Update existing meal_images rows that have empty/null image_url, using meals.image_url
                int updated = jdbc.update("""
                    UPDATE meal_images mi
                    SET image_url = m.image_url,
                        is_primary = true,
                        order_index = 0
                    FROM meals m
                    WHERE mi.meal_id = m.id
                      AND (mi.image_url IS NULL OR mi.image_url = '')
                      AND m.image_url IS NOT NULL
                      AND m.image_url <> ''
                """);

                // 2) Insert missing meal_images rows (only if there is no existing image_url row for that meal)
                int inserted = jdbc.update("""
                    INSERT INTO meal_images (meal_id, image_url, is_primary, order_index)
                    SELECT m.id, m.image_url, true, 0
                    FROM meals m
                    WHERE m.image_url IS NOT NULL
                      AND m.image_url <> ''
                      AND NOT EXISTS (
                        SELECT 1
                        FROM meal_images mi
                        WHERE mi.meal_id = m.id
                          AND mi.image_url IS NOT NULL
                          AND mi.image_url <> ''
                      )
                """);

                log.info("MealImage migration done. Updated rows: {}, Inserted rows: {}", updated, inserted);
            } catch (Exception e) {
                log.error("MealImage migration failed", e);
            }
        };
    }

    @Bean
    public CommandLineRunner createConsumedMealTable(JdbcTemplate jdbc) {
        return args -> {
            jdbc.execute("""
            CREATE TABLE IF NOT EXISTS consumed_meal (
                id BIGSERIAL PRIMARY KEY,
                recommended_daily_intake_id BIGINT NOT NULL,
                meal_id BIGINT NOT NULL,
                meal_name VARCHAR(255) NOT NULL,
                total_calories DOUBLE PRECISION NOT NULL,
                total_protein DOUBLE PRECISION NOT NULL,
                total_carbs DOUBLE PRECISION NOT NULL,
                total_fat DOUBLE PRECISION NOT NULL,
                total_sugars DOUBLE PRECISION,
                total_saturated_fat DOUBLE PRECISION,
                total_unsaturated_fat DOUBLE PRECISION,
                consumed_date DATE NOT NULL,
                consumed_time TIME NOT NULL,
                CONSTRAINT fk_consumed_rdi
                    FOREIGN KEY (recommended_daily_intake_id)
                    REFERENCES recommended_daily_intake(id)
                    ON DELETE CASCADE
            )
        """);
        };
    }

    @Bean
    public CommandLineRunner deleteMealsById(JdbcTemplate jdbc) {
        return args -> {
            // IMPORTANT: Remove this runner after it succeeded once
            long[] ids = {260L, 287L, 262L, 290L, 263L, 261L, }; // <-- put the meal IDs you want to delete here

            for (long id : ids) {
                try {
                    // Remove enum/join rows first (these block deletion)
                    jdbc.update("DELETE FROM public.meal_diets WHERE meal_id = ?", id);
                    jdbc.update("DELETE FROM public.meal_cuisines WHERE meal_id = ?", id);
                    jdbc.update("DELETE FROM public.meal_meal_types WHERE meal_id = ?", id);

                    // Now delete the meal
                    int deleted = jdbc.update("DELETE FROM public.meals WHERE id = ?", id);

                    log.info("✅ Deleted meal id={} deletedRows={}", id, deleted);
                } catch (Exception e) {
                    log.error("❌ Failed deleting meal id={}", id, e);
                }
            }
        };
    }

}

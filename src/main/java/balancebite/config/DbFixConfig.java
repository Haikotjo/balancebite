package balancebite.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DbFixConfig {
    private static final Logger log = LoggerFactory.getLogger(DbFixConfig.class);

    @Bean
    public org.springframework.boot.CommandLineRunner fixFoodCategoryCheck(JdbcTemplate jdbc) {
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
}


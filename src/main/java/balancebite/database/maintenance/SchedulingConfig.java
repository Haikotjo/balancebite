package balancebite.database.maintenance;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration class to enable scheduling in the application.
 *
 * This class activates the Spring scheduling mechanism, allowing
 * scheduled tasks annotated with @Scheduled to run automatically.
 *
 * Even though this class is empty, it is required to inform Spring
 * that scheduling is enabled.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // No logic needed here; @EnableScheduling activates the scheduling system.
}

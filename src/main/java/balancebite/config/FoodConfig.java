package balancebite.config;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class to load FDC IDs from application properties.
 * Binds properties with the prefix "food" to this class.
 */
@Configuration
@ConfigurationProperties(prefix = "food")
public class FoodConfig {

    private static final Logger log = LoggerFactory.getLogger(FoodConfig.class);

    /**
     * List of FoodData Central IDs loaded from the configuration.
     */
    @NotEmpty(message = "The list of FDC IDs must not be empty")
    private List<String> fdcIds;

    /**
     * Gets the list of FDC IDs.
     *
     * @return The list of FDC IDs.
     */
    public List<String> getFdcIds() {
        return fdcIds;
    }

    /**
     * Sets the list of FDC IDs.
     *
     * @param fdcIds The list of FDC IDs to set.
     */
    public void setFdcIds(List<String> fdcIds) {
        this.fdcIds = fdcIds;
    }

    /**
     * Logs a warning if the list of FDC IDs is empty.
     */
    @PostConstruct
    public void validateFdcIds() {
        if (fdcIds == null || fdcIds.isEmpty()) {
            log.warn("No FDC IDs were provided. Please ensure the 'food.fdcIds' property is set in the configuration.");
        }
    }
}

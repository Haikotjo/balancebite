package balancebite.config;

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

    /**
     * List of FoodData Central IDs loaded from the configuration.
     */
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
}

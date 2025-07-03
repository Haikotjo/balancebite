package balancebite.config;

import balancebite.service.fooditem.FoodItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * Configuration class responsible for initializing the application with necessary food items from the USDA API.
 */
@Configuration
public class AppInitializer {

    private static final Logger log = LoggerFactory.getLogger(AppInitializer.class);

    private final FoodItemService foodItemService;
    private final FoodConfig foodConfig;

    /**
     * Constructor for dependency injection.
     *
     * @param foodItemService Service for managing food item operations.
     * @param foodConfig Configuration containing food item IDs for initialization.
     */
    @Autowired
    public AppInitializer(FoodItemService foodItemService, FoodConfig foodConfig) {
        this.foodItemService = foodItemService;
        this.foodConfig = foodConfig;
    }

    /**
     * Method executed after bean initialization to fetch and save food items using the provided FDC IDs.
     */
    @PostConstruct
    public void init() {
        log.info("Starting application initialization with food items.");

        try {
            List<String> fdcIds = foodConfig.getFdcIds(); // Get the list from configuration (e.g., YAML)

            if (fdcIds == null || fdcIds.isEmpty()) {
                log.warn("No FDC IDs found in configuration for initialization.");
                return;
            }

            log.info("Found {} FDC IDs to initialize food items.", fdcIds.size());
            foodItemService.fetchAndSaveAllFoodItems(fdcIds);
            log.info("Successfully initialized food items with FDC IDs.");

        } catch (Exception e) {
            log.error("Error during application initialization: {}", e.getMessage(), e);
        }
    }
}

package balancebite.config;

import balancebite.service.FoodItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * Configuration class responsible for initializing the application with necessary food items from the USDA API.
 */
@Configuration
public class AppInitializer {

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
        try {
            List<String> fdcIds = foodConfig.getFdcIds(); // Get the list from configuration (e.g., YAML)

            if (fdcIds != null && !fdcIds.isEmpty()) {
                foodItemService.fetchAndSaveAllFoodItems(fdcIds);
            } else {
                System.err.println("No FDC IDs found for initialization.");
            }
        } catch (Exception e) {
            System.err.println("Error in AppInitializer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

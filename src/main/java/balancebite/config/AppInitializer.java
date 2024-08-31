package balancebite.config;

import balancebite.service.FoodItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Configuration
public class AppInitializer {

    @Autowired
    private FoodItemService foodItemService;

    @Autowired
    private FoodConfig foodConfig; // Inject the configuration class

    @PostConstruct
    public void init() {
        try {
            List<String> fdcIds = foodConfig.getFdcIds(); // Get the list from YAML

            foodItemService.fetchAndSaveAllFoodItems(fdcIds);
        } catch (Exception e) {
            System.err.println("Error in AppInitializer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
Wat een ellende!!!!!!!!!!!!!!
package balancebite.config;

import balancebite.service.FoodItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;  // Veranderd naar jakarta.annotation.PostConstruct
import java.util.Arrays;
import java.util.List;

@Configuration
public class AppInitializer {

    @Autowired
    private FoodItemService foodItemService;

    @PostConstruct
    public void init() {
        List<String> fdcIds = Arrays.asList("170513 ", "169287", "169999", "173944", "323505", "169910", "2346409", "2685573", "1999634", "747447", "171705", "169124", "2344766", "173424", "172688", "171409", "171269", "174276");
        foodItemService.fetchAndSaveAllFoodItems(fdcIds);
    }
}

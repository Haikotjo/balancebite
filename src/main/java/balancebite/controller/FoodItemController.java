package balancebite.controller;

import balancebite.service.FoodItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fooditems")
public class FoodItemController {

    @Autowired
    private FoodItemService foodItemService;

    @GetMapping("/fetch/{fdcId}")
    public String fetchFoodItem(@PathVariable String fdcId) {
        foodItemService.fetchAndSaveFoodItem(fdcId);
        return "Food item for FDC ID " + fdcId + " fetched and saved successfully";
    }

    @PostMapping("/fetchAll")
    public String fetchAllFoodItems(@RequestBody List<String> fdcIds) {
        foodItemService.fetchAndSaveAllFoodItems(fdcIds);
        return "Food items for provided FDC IDs fetched and saved successfully";
    }
}

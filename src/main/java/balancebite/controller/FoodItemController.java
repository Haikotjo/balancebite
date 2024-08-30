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

    /**
     * Endpoint to fetch and save a single FoodItem by FDC ID.
     * Calls the FoodItemService to retrieve and save the food item.
     *
     * @param fdcId The FDC ID of the food item to fetch.
     * @return A success message indicating the food item has been fetched and saved.
     */
    @GetMapping("/{fdcId}")
    public String fetchFoodItem(@PathVariable String fdcId) {
        boolean isNew = foodItemService.fetchAndSaveFoodItem(fdcId);

        if (isNew) {
            return "Food item for FDC ID " + fdcId + " fetched and saved successfully";
        } else {
            return "Food item for FDC ID " + fdcId + " already exists and was not added again";
        }
    }

    /**
     * Endpoint to fetch and save multiple FoodItems by a list of FDC IDs.
     * Calls the FoodItemService to retrieve and save the food items in a single API call.
     *
     * @param fdcIds The list of FDC IDs of the food items to fetch.
     * @return A success message indicating the food items have been fetched and saved.
     */
    @PostMapping("/fetchAll")
    public String fetchAllFoodItems(@RequestBody List<String> fdcIds) {
        foodItemService.fetchAndSaveAllFoodItems(fdcIds);
        return "Food items for provided FDC IDs fetched and saved successfully";
    }
}

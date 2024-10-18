package balancebite.controller;

import balancebite.dto.fooditem.FoodItemDTO;
import balancebite.service.FoodItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for managing FoodItem-related operations.
 */
@RestController
@RequestMapping("/fooditems")
public class FoodItemController {

    private final FoodItemService foodItemService;

    /**
     * Constructor for dependency injection.
     *
     * @param foodItemService Service for managing FoodItem operations.
     */
    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    /**
     * Endpoint to fetch and save a single FoodItem by FDC ID.
     * Calls the FoodItemService to retrieve and save the food item.
     *
     * @param fdcId The FDC ID of the food item to fetch.
     * @return A ResponseEntity with a success or error message, either CREATED (201) or a CONFLICT (409) if the item already exists.
     */
    @GetMapping("/fetch/{fdcId:[0-9]+}")
    public ResponseEntity<String> fetchFoodItem(@PathVariable String fdcId) {
        foodItemService.fetchAndSaveFoodItem(fdcId);
        return ResponseEntity.ok("Food item fetched and saved successfully");
    }

    /**
     * Endpoint to fetch and save multiple FoodItems by a list of FDC IDs.
     * Calls the FoodItemService to retrieve and save the food items asynchronously.
     *
     * The use of CompletableFuture allows the client to receive a response immediately,
     * while the processing of fetching and saving the food items continues in the background.
     *
     * @param fdcIds The list of FDC IDs of the food items to fetch.
     * @return A CompletableFuture containing a ResponseEntity with a success or error message.
     *         This ensures non-blocking behavior, enhancing the application's responsiveness.
     */
    @PostMapping("/bulk-fetch-items")
    public CompletableFuture<ResponseEntity<String>> fetchAllFoodItems(@RequestBody List<String> fdcIds) {
        foodItemService.fetchAndSaveAllFoodItems(fdcIds);
        return CompletableFuture.completedFuture(ResponseEntity.ok("Bulk food items fetched and saved successfully"));
    }

    /**
     * Endpoint to retrieve a single FoodItem by its ID from the database.
     *
     * @param id The ID of the food item to retrieve.
     * @return A ResponseEntity with the corresponding FoodItemDTO or a NOT_FOUND (404) status if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FoodItemDTO> getFoodItemById(@PathVariable Long id) {
        FoodItemDTO foodItem = foodItemService.getFoodItemById(id);
        return ResponseEntity.ok(foodItem);
    }

    /**
     * Endpoint to retrieve all FoodItems from the database.
     *
     * @return A ResponseEntity with a list of all FoodItemDTOs.
     */
    @GetMapping
    public ResponseEntity<List<FoodItemDTO>> getAllFoodItems() {
        List<FoodItemDTO> foodItems = foodItemService.getAllFoodItems();
        return ResponseEntity.ok(foodItems);
    }

    /**
     * Endpoint to delete a FoodItem by its ID.
     * Calls the FoodItemService to delete the food item from the database.
     *
     * @param id The ID of the food item to delete.
     * @return A ResponseEntity indicating the result of the delete operation, either NO_CONTENT (204) if deleted or NOT_FOUND (404) if not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodItemById(@PathVariable Long id) {
        foodItemService.deleteFoodItemById(id);
        return ResponseEntity.noContent().build();
    }
}

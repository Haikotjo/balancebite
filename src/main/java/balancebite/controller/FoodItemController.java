package balancebite.controller;

import balancebite.dto.fooditem.FoodItemDTO;
import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.service.FoodItemService;
import balancebite.service.UsdaApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing FoodItem-related operations.
 * Provides endpoints to fetch and save food items from the USDA API.
 */
@RestController
@RequestMapping("/fooditems")
public class FoodItemController {

    private final FoodItemService foodItemService;
    private final UsdaApiService usdaApiService;

    /**
     * Constructor for dependency injection.
     *
     * @param foodItemService Service for managing FoodItem operations.
     * @param usdaApiService  Service for interacting with the USDA API.
     */
    public FoodItemController(FoodItemService foodItemService, UsdaApiService usdaApiService) {
        this.foodItemService = foodItemService;
        this.usdaApiService = usdaApiService;
    }

    /**
     * Endpoint to fetch and save a single FoodItem by FDC ID.
     * Calls the FoodItemService to retrieve and save the food item.
     *
     * @param fdcId The FDC ID of the food item to fetch.
     * @return A ResponseEntity with a success or error message.
     */
    @GetMapping("/{fdcId}")
    public ResponseEntity<String> fetchFoodItem(@PathVariable String fdcId) {
        UsdaFoodResponseDTO response = usdaApiService.getFoodData(fdcId);
        boolean isNew = foodItemService.fetchAndSaveFoodItem(fdcId);

        if (isNew) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Food item '" + response.getDescription() + "' for FDC ID " + fdcId + " fetched and saved successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body("Food item for FDC ID " + fdcId + " already exists or could not be fetched.");
        }
    }

    /**
     * Endpoint to fetch and save multiple FoodItems by a list of FDC IDs.
     * Calls the FoodItemService to retrieve and save the food items in a single API call.
     *
     * @param fdcIds The list of FDC IDs of the food items to fetch.
     * @return A ResponseEntity with a success or error message.
     */
    @PostMapping("/fetchAll")
    public ResponseEntity<String> fetchAllFoodItems(@RequestBody List<String> fdcIds) {
        List<String> savedItems = foodItemService.fetchAndSaveAllFoodItems(fdcIds);

        if (savedItems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("No food items were fetched and saved. Please check the provided FDC IDs.");
        } else {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Food items for the following FDC IDs were fetched and saved successfully: " + String.join(", ", savedItems));
        }
    }

    /**
     * Endpoint to retrieve a single FoodItem by its ID from the database.
     *
     * @param id The ID of the food item to retrieve.
     * @return A ResponseEntity with the corresponding FoodItemDTO or a NOT_FOUND status if not found.
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<FoodItemDTO> getFoodItemById(@PathVariable Long id) {
        FoodItemDTO foodItemDTO = foodItemService.getFoodItemById(id);
        if (foodItemDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(foodItemDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Endpoint to retrieve all FoodItems from the database.
     *
     * @return A ResponseEntity with a list of all FoodItemDTOs.
     */
    @GetMapping("/all")
    public ResponseEntity<List<FoodItemDTO>> getAllFoodItems() {
        List<FoodItemDTO> foodItems = foodItemService.getAllFoodItems();
        return ResponseEntity.status(HttpStatus.OK).body(foodItems);
    }

    /**
     * Endpoint to delete a FoodItem by its ID.
     *
     * @param id The ID of the food item to delete.
     * @return A ResponseEntity indicating the result of the delete operation.
     */
    @DeleteMapping("/id/{id}")
    public ResponseEntity<String> deleteFoodItemById(@PathVariable Long id) {
        boolean deleted = foodItemService.deleteFoodItemById(id);
        if (deleted) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Food item deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Food item not found.");
        }
    }
}

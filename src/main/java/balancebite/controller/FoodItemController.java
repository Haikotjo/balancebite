package balancebite.controller;

import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.dto.fooditem.FoodItemDTO;
import balancebite.dto.fooditem.FoodItemNameDTO;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.errorHandling.EntityNotFoundException;
import balancebite.service.FoodItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for managing FoodItem-related operations.
 */
@RestController
@RequestMapping("/fooditems")
public class FoodItemController {

    private static final Logger log = LoggerFactory.getLogger(FoodItemController.class);
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
     * @return A ResponseEntity with details of the saved food item or an error message.
     */
    @GetMapping("/fetch/{fdcId:[0-9]+}")
    public ResponseEntity<?> fetchFoodItem(@PathVariable String fdcId) {
        log.info("Fetching food item with FDC ID: {}", fdcId);
        try {
            UsdaFoodResponseDTO response = foodItemService.fetchAndSaveFoodItem(fdcId);
            log.info("Successfully fetched and saved food item with FDC ID: {}", fdcId);

            // Create a response with details of the saved item.
            Map<String, String> responseBody = Map.of(
                    "message", "Food item fetched and saved successfully",
                    "fdcId", String.valueOf(response.getFdcId()),
                    "name", response.getDescription()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        } catch (EntityAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint to fetch and save multiple FoodItems by a list of FDC IDs.
     * Calls the FoodItemService to retrieve and save the food items asynchronously.
     *
     * @param fdcIds The list of FDC IDs of the food items to fetch.
     * @return A CompletableFuture containing a ResponseEntity with the list of saved items.
     */
    @PostMapping("/bulk-fetch-items")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> fetchAllFoodItems(@RequestBody List<String> fdcIds) {
        log.info("Fetching multiple food items with FDC IDs: {}", fdcIds);
        return foodItemService.fetchAndSaveAllFoodItems(fdcIds)
                .thenApply(result -> {
                    log.info("Bulk fetch and save completed for food items.");
                    return ResponseEntity.status(HttpStatus.CREATED).body(result);
                })
                .exceptionally(e -> {
                    log.error("Error during bulk fetch and save: {}", e.getMessage(), e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "An unexpected error occurred during bulk fetch."));
                });
    }

    /**
     * Endpoint to retrieve a single FoodItem by its ID from the database.
     *
     * @param id The ID of the food item to retrieve.
     * @return A ResponseEntity with the corresponding FoodItemDTO or a NOT_FOUND (404) status if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFoodItemById(@PathVariable Long id) {
        log.info("Retrieving food item with ID: {}", id);
        try {
            FoodItemDTO foodItem = foodItemService.getFoodItemById(id);
            return ResponseEntity.ok(foodItem);
        } catch (EntityNotFoundException e) {
            log.warn("Food item not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during retrieval for food item ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint to retrieve all FoodItems from the database.
     *
     * @return A ResponseEntity with a list of all FoodItemDTOs or NO_CONTENT (204) status if none found.
     */
    @GetMapping
    public ResponseEntity<?> getAllFoodItems() {
        log.info("Retrieving all food items from the database.");
        List<FoodItemDTO> foodItems = foodItemService.getAllFoodItems();
        if (foodItems.isEmpty()) {
            log.info("No food items found.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
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
    public ResponseEntity<Map<String, String>> deleteFoodItemById(@PathVariable Long id) {
        log.info("Deleting food item with ID: {}", id);
        try {
            foodItemService.deleteFoodItemById(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.warn("Food item not found for deletion with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Food item with ID " + id + " not found."));
        } catch (Exception e) {
            log.error("Unexpected error during deletion for food item ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint to retrieve FoodItems by a prefix of their name.
     *
     * @param prefix The prefix to search for.
     * @return A ResponseEntity with the list of FoodItemDTOs that match the criteria.
     */
    @GetMapping("/search-by-name")
    public ResponseEntity<?> getFoodItemsByNamePrefix(@RequestParam String prefix) {
        log.info("Fetching food items with names starting with: {}", prefix);
        try {
            List<FoodItemDTO> foodItems = foodItemService.getFoodItemsByNamePrefix(prefix);
            return ResponseEntity.ok(foodItems);
        } catch (EntityNotFoundException e) {
            log.warn("No food items found with names starting with: {}", prefix);
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while fetching food items with prefix: {}", prefix, e);
            return ResponseEntity.status(500).body("An unexpected error occurred.");
        }
    }

    /**
     * Endpoint to retrieve only the IDs and names of all FoodItems.
     * This is optimized for search functionality where full food item details are not needed.
     *
     * @return A ResponseEntity containing a list of FoodItemDTOs with only ID and name fields.
     */
    @GetMapping("/names")
    public ResponseEntity<?> getAllFoodItemNames() {
        log.info("Fetching all food item names and IDs.");
        List<FoodItemNameDTO> foodItemNames = foodItemService.getAllFoodItemNames();

        if (foodItemNames.isEmpty()) {
            log.info("No food item names found.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(foodItemNames);
    }
}

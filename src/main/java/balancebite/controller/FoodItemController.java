package balancebite.controller;

import balancebite.dto.FoodItemDTO;
import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.service.FoodItemService;
import balancebite.service.UsdaApiService;
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
    private final UsdaApiService usdaApiService; // Voeg de UsdaApiService hier toe

    /**
     * Constructor for dependency injection.
     *
     * @param foodItemService Service for managing FoodItem operations.
     * @param usdaApiService Service for interacting with the USDA API.
     */
    public FoodItemController(FoodItemService foodItemService, UsdaApiService usdaApiService) {
        this.foodItemService = foodItemService;
        this.usdaApiService = usdaApiService; // Initialiseer de UsdaApiService hier
    }

    /**
     * Endpoint to fetch and save a single FoodItem by FDC ID.
     * Calls the FoodItemService to retrieve and save the food item.
     *
     * @param fdcId The FDC ID of the food item to fetch.
     * @return A success message indicating the food item has been fetched and saved, or an error message.
     */
    @GetMapping("/{fdcId}")
    public String fetchFoodItem(@PathVariable String fdcId) {
        UsdaFoodResponseDTO response = usdaApiService.getFoodData(fdcId);  // Haal de USDA API response op
        boolean isNew = foodItemService.fetchAndSaveFoodItem(fdcId);

        if (isNew) {
            return "Food item '" + response.getDescription() + "' for FDC ID " + fdcId + " fetched and saved successfully.";
        } else {
            return "Food item for FDC ID " + fdcId + " already exists or could not be fetched.";
        }
    }

    /**
     * Endpoint to fetch and save multiple FoodItems by a list of FDC IDs.
     * Calls the FoodItemService to retrieve and save the food items in a single API call.
     *
     * @param fdcIds The list of FDC IDs of the food items to fetch.
     * @return A message indicating the result of the operation.
     */
    @PostMapping("/fetchAll")
    public String fetchAllFoodItems(@RequestBody List<String> fdcIds) {
        List<String> savedItems = foodItemService.fetchAndSaveAllFoodItems(fdcIds);

        if (savedItems.isEmpty()) {
            return "No food items were fetched and saved. Please check the provided FDC IDs.";
        } else {
            return "Food items for the following FDC IDs were fetched and saved successfully: " + String.join(", ", savedItems);
        }
    }


    /**
     * Endpoint to retrieve a single FoodItem by its ID from the database.
     *
     * @param id The ID of the food item to retrieve.
     * @return The corresponding FoodItemDTO, or null if not found.
     */
    @GetMapping("/id/{id}")
    public FoodItemDTO getFoodItemById(@PathVariable Long id) {
        return foodItemService.getFoodItemById(id);
    }

    /**
     * Endpoint to retrieve all FoodItems from the database.
     *
     * @return A list of all FoodItemDTOs in the database.
     */
    @GetMapping("/all")
    public List<FoodItemDTO> getAllFoodItems() {
        return foodItemService.getAllFoodItems();
    }
}

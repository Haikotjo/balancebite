package balancebite.utils;

import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.model.FoodItem;
import balancebite.repository.FoodItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for processing bulk fetch requests for FoodItems.
 * This class provides methods to filter out existing FDC IDs, handle USDA API responses,
 * and manage the conversion and saving of food items.
 */
public class FoodItemBulkFetchUtil {
    private static final Logger log = LoggerFactory.getLogger(FoodItemBulkFetchUtil.class);

    /**
     * Filters out FDC IDs that already exist in the database.
     *
     * @param fdcIds List of FDC IDs to filter.
     * @param foodItemRepository The repository to check existing items.
     * @param skippedItems List to track the skipped FDC IDs.
     * @return A list of new FDC IDs that are not in the database.
     */
    public static List<String> filterExistingFdcIds(List<String> fdcIds, FoodItemRepository foodItemRepository, List<String> skippedItems) {
        return fdcIds.stream()
                .map(String::trim)
                .filter(fdcId -> {
                    // If the FDC ID already exists, add it to skippedItems and filter it out
                    if (foodItemRepository.existsByFdcId(Integer.parseInt(fdcId))) {
                        skippedItems.add(fdcId);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * Converts a USDA API response into a map representing a saved food item.
     *
     * @param response The USDA API response.
     * @return A map containing the FDC ID and name of the saved item.
     */
    public static Map<String, String> convertToSavedItemMap(UsdaFoodResponseDTO response) {
        Map<String, String> savedItem = new HashMap<>();
        savedItem.put("fdcId", String.valueOf(response.getFdcId()));
        savedItem.put("name", response.getDescription());
        return savedItem;
    }

    /**
     * Checks if a USDA API response is considered invalid.
     *
     * @param response The USDA API response.
     * @return True if the response is invalid (e.g., null or missing critical data), otherwise false.
     */
    public static boolean isResponseInvalid(UsdaFoodResponseDTO response) {
        return response == null || response.getFoodNutrients() == null ||
                response.getDescription() == null || response.getDescription().isEmpty();
    }

    /**
     * Processes a valid USDA API response, converts it into a FoodItem, and saves it to the database.
     * Also adds the saved item information to the savedItems list.
     *
     * @param response The USDA API response.
     * @param foodItemRepository The repository to save the food item.
     * @param savedItems List to track saved food items.
     */
    public static void processAndSaveResponse(UsdaFoodResponseDTO response,
                                              FoodItemRepository foodItemRepository,
                                              List<Map<String, String>> savedItems) {
        // Convert the response to a FoodItem entity and save it
        FoodItem foodItem = balancebite.utils.FoodItemUtil.convertToFoodItem(response);
        foodItemRepository.save(foodItem);

        // Convert the response into a saved item map and add it to the list
        Map<String, String> savedItem = convertToSavedItemMap(response);
        savedItems.add(savedItem);

        log.info("Successfully saved food item with name: {} and FDC ID: {}", response.getDescription(), response.getFdcId());
    }

    /**
     * Creates a response map with the given message, saved items, skipped items, and invalid items.
     *
     * @param message The summary message of the operation.
     * @param savedItems List of saved items as maps.
     * @param skippedItems List of FDC IDs that were skipped.
     * @param invalidItems List of FDC IDs that were considered invalid.
     * @return A map containing the message, savedItems, skippedItems, and invalidItems.
     */
    public static Map<String, Object> createResponse(String message, List<Map<String, String>> savedItems, List<String> skippedItems, List<String> invalidItems) {
        return Map.of(
                "message", message,
                "savedItems", savedItems,
                "skippedItems", skippedItems,
                "invalidItems", invalidItems
        );
    }

    /**
     * Handles errors encountered when interacting with the USDA API.
     * Adds the provided FDC IDs to the invalidItems list and logs the error.
     *
     * @param newFdcIds List of new FDC IDs that encountered an error.
     * @param savedItems List of saved items as maps.
     * @param skippedItems List of FDC IDs that were skipped.
     * @param invalidItems List of FDC IDs that were considered invalid.
     * @param e The exception encountered during the USDA API call.
     * @return A response map indicating that an error occurred, along with the saved, skipped, and invalid items.
     */
    public static Map<String, Object> handleUsdaApiError(List<String> newFdcIds, List<Map<String, String>> savedItems, List<String> skippedItems, List<String> invalidItems, Exception e) {
        log.debug("USDA API error for FDC IDs: {}. Adding to invalidItems.", newFdcIds, e);
        invalidItems.addAll(newFdcIds);
        return createResponse("An error occurred while processing some FDC IDs.", savedItems, skippedItems, invalidItems);
    }
}

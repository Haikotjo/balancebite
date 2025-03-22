package balancebite.service.interfaces;

import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.dto.fooditem.FoodItemDTO;
import balancebite.dto.fooditem.FoodItemNameDTO;
import balancebite.errorHandling.EntityAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for FoodItem service operations.
 * Defines methods to manage and retrieve FoodItem data from the USDA API.
 */
public interface IFoodItemService {

    /**
     * Fetches a single food item from the USDA API by its FDC ID and saves it.
     * If the food item already exists in the database, it will not be added again.
     *
     * @param fdcId The FDC ID of the food item to fetch.
     * @return The USDA API response DTO containing details of the fetched food item.
     * @throws EntityAlreadyExistsException if the food item already exists in the database.
     * @throws IllegalArgumentException if the USDA API response is invalid.
     */
    UsdaFoodResponseDTO fetchAndSaveFoodItem(String fdcId);


    /**
     * Fetches multiple food items from the USDA API by a list of FDC IDs and saves them.
     *
     * @param fdcIds List of FDC IDs for the food items to fetch.
     * @return A CompletableFuture indicating the completion of the operation.
     * @throws IllegalArgumentException if no valid food items were fetched.
     */
    CompletableFuture<Map<String, Object>> fetchAndSaveAllFoodItems(List<String> fdcIds);

    /**
     * Retrieves a single FoodItem by its ID from the database.
     *
     * @param id The ID of the food item to retrieve.
     * @return The corresponding FoodItemDTO.
     */
    FoodItemDTO getFoodItemById(Long id);

    /**
     * Retrieves all FoodItems from the database.
     *
     * @return A list of all FoodItemDTOs.
     */
    List<FoodItemDTO> getAllFoodItems();

    /**
     * Deletes a FoodItem by its ID from the database.
     *
     * @param id The ID of the food item to delete.
     * @throws EntityNotFoundException if the food item with the specified ID does not exist.
     */
    void deleteFoodItemById(Long id);

    /**
     * Retrieves a list of all FoodItems, returning only their ID and name.
     *
     * @return A list of FoodItemNameDTOs containing only ID and name.
     */
    List<FoodItemNameDTO> getAllFoodItemNames();
}

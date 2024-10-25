package balancebite.service;

import balancebite.dto.fooditem.FoodItemDTO;
import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.errorHandling.EntityAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for FoodItem service operations.
 * Defines methods to manage and retrieve FoodItem data from the USDA API.
 */
public interface IFoodItemService {

    /**
     * Fetches a single food item from the USDA API by its FDC ID and saves it.
     *
     * @param fdcId The FDC ID of the food item to fetch.
     * @throws EntityAlreadyExistsException if the food item already exists in the database.
     * @throws IllegalArgumentException if the food description or response from the USDA API is invalid.
     */
    void fetchAndSaveFoodItem(String fdcId);

    /**
     * Fetches multiple food items from the USDA API by a list of FDC IDs and saves them.
     *
     * @param fdcIds List of FDC IDs for the food items to fetch.
     * @return A CompletableFuture indicating the completion of the operation.
     * @throws IllegalArgumentException if no valid food items were fetched.
     */
    CompletableFuture<Void> fetchAndSaveAllFoodItems(List<String> fdcIds);

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
}

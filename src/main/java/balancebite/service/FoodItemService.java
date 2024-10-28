package balancebite.service;

import balancebite.dto.fooditem.FoodItemDTO;
import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.errorHandling.UsdaApiException;
import balancebite.mapper.FoodItemMapper;
import balancebite.model.FoodItem;
import balancebite.repository.FoodItemRepository;
import balancebite.service.interfaces.IFoodItemService;
import balancebite.errorHandling.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Service class for handling operations related to FoodItems.
 * This service interacts with the USDA API to fetch food data and store it in the repository.
 */
@Service
public class FoodItemService implements IFoodItemService {

    private static final Logger log = LoggerFactory.getLogger(FoodItemService.class);

    private final FoodItemRepository foodItemRepository;
    private final UsdaApiService usdaApiService;
    private final FoodItemMapper foodItemMapper;

    /**
     * Constructor for dependency injection.
     *
     * @param foodItemRepository Repository for storing and retrieving FoodItems.
     * @param usdaApiService Service for interacting with the USDA API.
     * @param foodItemMapper Mapper for converting between FoodItem entities and DTOs.
     */
    public FoodItemService(FoodItemRepository foodItemRepository, UsdaApiService usdaApiService, FoodItemMapper foodItemMapper) {
        this.foodItemRepository = foodItemRepository;
        this.usdaApiService = usdaApiService;
        this.foodItemMapper = foodItemMapper;
    }

    /**
     * Fetches a single food item from the USDA API by its FDC ID and saves it.
     * If the food item already exists in the database, it will not be added again.
     *
     * @param fdcId The FDC ID of the food item to fetch.
     */
    @Override
    @Cacheable(value = "foodItemCache", key = "#fdcId")
    public void fetchAndSaveFoodItem(String fdcId) {
        log.info("Fetching food item with FDC ID: {}", fdcId);

        // Convert the FDC ID from String to Integer.
        int fdcIdInt = Integer.parseInt(fdcId);

        // Check if the FoodItem with the given FDC ID already exists.
        if (foodItemRepository.existsByFdcId(fdcIdInt)) {
            log.warn("Food item already exists with FDC ID: {}", fdcIdInt);
            throw new EntityAlreadyExistsException("Food item already exists with FDC ID: " + fdcIdInt);
        }

        // Fetch the food data from the USDA API.
        UsdaFoodResponseDTO response = usdaApiService.getFoodData(fdcId);

        // Validate the response and save the food item if valid.
        if (response == null || response.getFoodNutrients() == null || response.getDescription() == null || response.getDescription().isEmpty()) {
            String errorMessage = "Invalid response received from USDA API for FDC ID: " + fdcIdInt;
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        // Convert the USDA response to a FoodItem entity and save it.
        FoodItem foodItem = balancebite.utils.FoodItemUtil.convertToFoodItem(response);
        foodItem.setFdcId(fdcIdInt); // Set the FDC ID.
        foodItemRepository.save(foodItem);

        log.info("Successfully saved food item with name: {} and FDC ID: {}", response.getDescription(), fdcIdInt);
    }

    /**
     * Fetches multiple food items from the USDA API by a list of FDC IDs and saves them.
     * This method processes the provided FDC IDs, checks which items already exist in the database,
     * and attempts to fetch the remaining items from the USDA API. It then saves valid items to the database
     * and categorizes them as saved, skipped, or invalid.
     *
     * @param fdcIds List of FDC IDs for the food items to fetch.
     * @return A CompletableFuture containing a Map with the following keys:
     *         - "message": A summary message of the operation.
     *         - "savedItems": A list of maps containing "fdcId" and "name" of the food items that were successfully saved.
     *         - "skippedItems": A list of FDC IDs that were already present in the database.
     *         - "invalidItems": A list of FDC IDs that were either not found in the USDA API or could not be processed.
     * @throws UsdaApiException if an error occurs while communicating with the USDA API.
     */
    @Override
    @Async
    public CompletableFuture<Map<String, Object>> fetchAndSaveAllFoodItems(List<String> fdcIds) {
        log.info("Fetching multiple food items with FDC IDs: {}", fdcIds);

        // Lists to track saved, skipped, and invalid items
        List<Map<String, String>> savedItems = new ArrayList<>();
        List<String> skippedItems = new ArrayList<>();
        List<String> invalidItems = new ArrayList<>();

        // Filter out FDC IDs that already exist in the database
        List<String> newFdcIds = fdcIds.stream()
                .map(String::trim)
                .filter(fdcId -> {
                    if (foodItemRepository.existsByFdcId(Integer.parseInt(fdcId))) {
                        skippedItems.add(fdcId);
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        // If all FDC IDs already exist, return a response directly
        if (newFdcIds.isEmpty()) {
            log.info("All provided FDC IDs already exist in the database. No API calls needed.");
            return CompletableFuture.completedFuture(Map.of(
                    "message", "All provided FDC IDs already exist in the database.",
                    "savedItems", savedItems,
                    "skippedItems", skippedItems,
                    "invalidItems", invalidItems
            ));
        }

        // Attempt to fetch data from the USDA API for new FDC IDs
        List<UsdaFoodResponseDTO> responses;
        try {
            responses = usdaApiService.getMultipleFoodData(newFdcIds);
        } catch (UsdaApiException e) {
            // Add all FDC IDs to invalidItems if the API returns an error
            log.debug("USDA API error for FDC IDs: {}. Adding to invalidItems.", newFdcIds, e);
            invalidItems.addAll(newFdcIds);
            return CompletableFuture.completedFuture(Map.of(
                    "message", "An error occurred while processing some FDC IDs.",
                    "savedItems", savedItems,
                    "skippedItems", skippedItems,
                    "invalidItems", invalidItems
            ));
        }

        // Track which FDC IDs received a valid response
        Set<String> receivedFdcIds = responses.stream()
                .map(response -> String.valueOf(response.getFdcId()))
                .collect(Collectors.toSet());

        // Add FDC IDs to 'invalidItems' if no valid response was received
        newFdcIds.stream()
                .filter(fdcId -> !receivedFdcIds.contains(fdcId))
                .forEach(invalidItems::add);

        // Process the responses
        responses.forEach(response -> {
            if (response == null || response.getFoodNutrients() == null || response.getDescription() == null || response.getDescription().isEmpty()) {
                invalidItems.add(String.valueOf(response.getFdcId()));
            } else {
                FoodItem foodItem = balancebite.utils.FoodItemUtil.convertToFoodItem(response);
                foodItemRepository.save(foodItem);

                Map<String, String> savedItem = new HashMap<>();
                savedItem.put("fdcId", String.valueOf(response.getFdcId()));
                savedItem.put("name", response.getDescription());
                savedItems.add(savedItem);

                log.info("Successfully saved food item with name: {} and FDC ID: {}", response.getDescription(), response.getFdcId());
            }
        });

        log.info("Total food items successfully saved: {}", savedItems.size());
        log.info("Total skipped items: {}", skippedItems.size());
        log.info("Total invalid items: {}", invalidItems.size());

        return CompletableFuture.completedFuture(Map.of(
                "message", "Bulk food items fetched and processed.",
                "savedItems", savedItems,
                "skippedItems", skippedItems,
                "invalidItems", invalidItems
        ));
    }

    /**
     * Retrieves a single FoodItem by its ID from the database.
     *
     * @param id The ID of the food item to retrieve.
     * @return The corresponding FoodItemDTO.
     */
    @Override
    public FoodItemDTO getFoodItemById(Long id) {
        log.info("Retrieving food item with ID: {}", id);
        return foodItemRepository.findById(id)
                .map(foodItemMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Food item with ID " + id + " not found."));
    }

    /**
     * Retrieves all FoodItems from the database.
     *
     * @return A list of all FoodItemDTOs.
     */
    @Override
    public List<FoodItemDTO> getAllFoodItems() {
        log.info("Retrieving all food items from the database.");
        return foodItemRepository.findAll().stream()
                .map(foodItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a FoodItem by its ID from the database.
     *
     * @param id The ID of the food item to delete.
     */
    @Override
    public void deleteFoodItemById(Long id) {
        log.info("Deleting food item with ID: {}", id);
        if (foodItemRepository.existsById(id)) {
            foodItemRepository.deleteById(id);
            log.info("Successfully deleted food item with ID: {}", id);
        } else {
            log.error("Food item with ID {} not found", id);
            throw new EntityNotFoundException("Food item with ID " + id + " not found.");
        }
    }
}

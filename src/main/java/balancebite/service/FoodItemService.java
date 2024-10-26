package balancebite.service;

import balancebite.dto.fooditem.FoodItemDTO;
import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.mapper.FoodItemMapper;
import balancebite.model.FoodItem;
import balancebite.repository.FoodItemRepository;
import balancebite.service.interfaces.IFoodItemService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

        // Validate the response.
        if (response != null && response.getFoodNutrients() != null) {
            String description = response.getDescription();

            if (description != null && !description.isEmpty()) {
                // Convert the USDA response to a FoodItem entity and save it.
                FoodItem foodItem = balancebite.utils.FoodItemUtil.convertToFoodItem(response);
                foodItem.setFdcId(fdcIdInt); // Set the FDC ID.
                foodItemRepository.save(foodItem);

                log.info("Successfully saved food item with name: {} and FDC ID: {}", description, fdcIdInt);
            } else {
                log.error("Invalid food description received from USDA API");
                throw new IllegalArgumentException("Invalid food description received from USDA API");
            }
        } else {
            log.error("Invalid response received from USDA API for FDC ID: {}", fdcIdInt);
            throw new IllegalArgumentException("Invalid response received from USDA API");
        }
    }

    /**
     * Fetches multiple food items from the USDA API by a list of FDC IDs and saves them.
     * If a food item already exists in the database, it will not be added again.
     *
     * @param fdcIds List of FDC IDs for the food items to fetch.
     * @return A CompletableFuture with the result message.
     */
    @Override
    @Async
    public CompletableFuture<Void> fetchAndSaveAllFoodItems(List<String> fdcIds) {
        log.info("Fetching multiple food items with FDC IDs: {}", fdcIds);

        // Filter out FDC IDs that already exist in the database
        List<String> newFdcIds = fdcIds.stream()
                .filter(fdcId -> !foodItemRepository.existsByFdcId(Integer.parseInt(fdcId)))
                .collect(Collectors.toList());

        if (newFdcIds.isEmpty()) {
            log.info("All provided FDC IDs already exist in the database. No API calls needed.");
            return CompletableFuture.completedFuture(null);
        }

        List<UsdaFoodResponseDTO> responses = usdaApiService.getMultipleFoodData(newFdcIds);
        List<String> successfullySavedIds = new ArrayList<>();

        responses.stream()
                .filter(response -> response.getFoodNutrients() != null && response.getDescription() != null && !response.getDescription().isEmpty())
                .forEach(response -> {
                    if (!foodItemRepository.existsByFdcId(response.getFdcId())) {
                        FoodItem foodItem = balancebite.utils.FoodItemUtil.convertToFoodItem(response);
                        foodItemRepository.save(foodItem);
                        successfullySavedIds.add(response.getDescription());
                        log.info("Successfully saved food item with name: {}", response.getDescription());
                    } else {
                        log.warn("Food item with FDC ID {} already exists, skipping save.", response.getFdcId());
                    }
                });

        if (successfullySavedIds.isEmpty()) {
            log.error("No food items were fetched and saved.");
            throw new IllegalArgumentException("No food items were fetched and saved. Please check the provided FDC IDs.");
        }

        log.info("Successfully fetched and saved multiple food items.");
        return CompletableFuture.completedFuture(null);
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

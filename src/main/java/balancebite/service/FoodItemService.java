package balancebite.service;

import balancebite.dto.fooditem.FoodItemDTO;
import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.exceptions.EntityAlreadyExistsException;
import balancebite.exceptions.UsdaApiException;
import balancebite.mapper.FoodItemMapper;
import balancebite.model.FoodItem;
import balancebite.repository.FoodItemRepository;
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
public class FoodItemService {

    private static final Logger logger = LoggerFactory.getLogger(FoodItemService.class);

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
    @Cacheable(value = "foodItemCache", key = "#fdcId")
    public void fetchAndSaveFoodItem(String fdcId) {
        UsdaFoodResponseDTO response = usdaApiService.getFoodData(fdcId);

        if (response != null && response.getFoodNutrients() != null) {
            String description = response.getDescription();

            if (description != null && !description.isEmpty()) {
                if (foodItemRepository.existsByName(description)) {
                    throw new EntityAlreadyExistsException("Food item already exists with name: " + description);
                }

                FoodItem foodItem = balancebite.utils.FoodItemUtil.convertToFoodItem(response);
                foodItemRepository.save(foodItem);
            } else {
                throw new IllegalArgumentException("Invalid food description received from USDA API");
            }
        } else {
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
    @Async
    public CompletableFuture<Void> fetchAndSaveAllFoodItems(List<String> fdcIds) {
        List<String> successfullySavedIds = new ArrayList<>();
        List<UsdaFoodResponseDTO> responses = usdaApiService.getMultipleFoodData(fdcIds);

        responses.stream()
                .filter(response -> response.getFoodNutrients() != null && response.getDescription() != null && !response.getDescription().isEmpty())
                .forEach(response -> {
                    if (!foodItemRepository.existsByName(response.getDescription())) {
                        FoodItem foodItem = balancebite.utils.FoodItemUtil.convertToFoodItem(response);
                        foodItemRepository.save(foodItem);
                        successfullySavedIds.add(response.getDescription());
                    }
                });

        if (successfullySavedIds.isEmpty()) {
            throw new IllegalArgumentException("No food items were fetched and saved. Please check the provided FDC IDs.");
        }

        return CompletableFuture.completedFuture(null);
    }

    /**
     * Retrieves a single FoodItem by its ID from the database.
     *
     * @param id The ID of the food item to retrieve.
     * @return The corresponding FoodItemDTO.
     */
    public FoodItemDTO getFoodItemById(Long id) {
        return foodItemRepository.findById(id)
                .map(foodItemMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Food item with ID " + id + " not found."));
    }

    /**
     * Retrieves all FoodItems from the database.
     *
     * @return A list of all FoodItemDTOs.
     */
    public List<FoodItemDTO> getAllFoodItems() {
        return foodItemRepository.findAll().stream()
                .map(foodItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a FoodItem by its ID from the database.
     *
     * @param id The ID of the food item to delete.
     */
    public void deleteFoodItemById(Long id) {
        if (foodItemRepository.existsById(id)) {
            foodItemRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Food item with ID " + id + " not found.");
        }
    }
}

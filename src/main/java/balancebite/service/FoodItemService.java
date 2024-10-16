package balancebite.service;

import balancebite.dto.fooditem.FoodItemDTO;
import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.exception.UsdaApiException;
import balancebite.mapper.FoodItemMapper;
import balancebite.model.FoodItem;
import balancebite.model.NutrientInfo;
import balancebite.repository.FoodItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
     * @return A ResponseEntity with a success or error message.
     */
    public ResponseEntity<String> fetchAndSaveFoodItem(String fdcId) {
        try {
            UsdaFoodResponseDTO response = usdaApiService.getFoodData(fdcId);

            if (response != null && response.getFoodNutrients() != null && response.getDescription() != null && !response.getDescription().isEmpty()) {
                if (foodItemRepository.existsByName(response.getDescription())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Food item already exists");
                }

                FoodItem foodItem = balancebite.utils.FoodItemUtil.convertToFoodItem(response);
                foodItemRepository.save(foodItem);
                return ResponseEntity.status(HttpStatus.CREATED).body("Food item saved successfully. " + response.getDescription());
            }
        } catch (UsdaApiException e) {
            logger.error("Error fetching food data from USDA API: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching food data");
        } catch (IllegalArgumentException e) {
            logger.error("Invalid data received from USDA API: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid data received from USDA API");
        } catch (Exception e) {
            logger.error("Unexpected error while fetching and saving food item: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure to save food item");
    }

    /**
     * Fetches multiple food items from the USDA API by a list of FDC IDs and saves them.
     * If a food item already exists in the database, it will not be added again.
     *
     * @param fdcIds List of FDC IDs for the food items to fetch.
     * @return A ResponseEntity with a list of descriptions of the food items that were successfully saved or an error message.
     */
    public ResponseEntity<String> fetchAndSaveAllFoodItems(List<String> fdcIds) {
        List<String> successfullySavedIds = new ArrayList<>();
        try {
            List<UsdaFoodResponseDTO> responses = usdaApiService.getMultipleFoodData(fdcIds);

            for (UsdaFoodResponseDTO response : responses) {
                if (response.getFoodNutrients() != null && response.getDescription() != null && !response.getDescription().isEmpty()) {
                    if (!foodItemRepository.existsByName(response.getDescription())) {
                        FoodItem foodItem = balancebite.utils.FoodItemUtil.convertToFoodItem(response);
                        foodItemRepository.save(foodItem);
                        successfullySavedIds.add(response.getDescription());
                    }
                }
            }
        } catch (UsdaApiException e) {
            logger.error("Error fetching food data from USDA API: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching food data from USDA API");
        } catch (Exception e) {
            logger.error("Unexpected error while fetching and saving multiple food items: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred while saving multiple food items");
        }

        if (successfullySavedIds.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No food items were fetched and saved. Please check the provided FDC IDs.");
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body("Food items for the following FDC IDs were fetched and saved successfully: " + String.join(", ", successfullySavedIds));
        }
    }

    /**
     * Retrieves a single FoodItem by its ID from the database.
     *
     * @param id The ID of the food item to retrieve.
     * @return The corresponding FoodItemDTO wrapped in a ResponseEntity, or a NOT_FOUND status if not found.
     *         In case of an unexpected error, an INTERNAL_SERVER_ERROR status is returned.
     */
    public ResponseEntity<FoodItemDTO> getFoodItemById(Long id) {
        try {
            // Attempt to retrieve the food item from the repository
            return foodItemRepository.findById(id)
                    .map(foodItemMapper::toDTO) // Convert the FoodItem entity to a DTO using the mapper
                    .map(ResponseEntity::ok) // Return the DTO with an HTTP 200 OK status if found
                    .orElseGet(() -> {
                        // Log a warning if the food item is not found
                        logger.warn("Food item with ID {} not found.", id);
                        // Return a 404 NOT_FOUND status with a message in the body
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                    });
        } catch (Exception e) {
            // Log an error if an unexpected exception occurs
            logger.error("Unexpected error while retrieving food item with ID {}: {}", id, e.getMessage());
            // Return a 500 INTERNAL_SERVER_ERROR status with a null body
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Retrieves all FoodItems from the database.
     *
     * @return A ResponseEntity with a list of all FoodItemDTOs, or an INTERNAL_SERVER_ERROR status in case of an error.
     */
    public ResponseEntity<List<FoodItemDTO>> getAllFoodItems() {
        try {
            List<FoodItem> foodItems = foodItemRepository.findAll();
            List<FoodItemDTO> foodItemDTOs = foodItems.stream()
                    .map(foodItemMapper::toDTO) // Use the mapper to convert to DTO
                    .collect(Collectors.toList());
            return ResponseEntity.ok(foodItemDTOs);
        } catch (Exception e) {
            logger.error("Unexpected error while retrieving all food items: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Deletes a FoodItem by its ID from the database.
     *
     * @param id The ID of the food item to delete.
     * @return A ResponseEntity indicating the result of the delete operation.
     */
    public ResponseEntity<String> deleteFoodItemById(Long id) {
        Optional<FoodItem> foodItemOptional = foodItemRepository.findById(id);
        if (foodItemOptional.isPresent()) {
            foodItemRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Food item deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Food item not found.");
        }
    }
}
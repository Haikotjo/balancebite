package balancebite.service;

import balancebite.dto.fooditem.FoodItemDTO;
import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.errorHandling.ErrorResponseUtil;
import balancebite.exception.UsdaApiException;
import balancebite.mapper.FoodItemMapper;
import balancebite.model.FoodItem;
import balancebite.repository.FoodItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    private static final String UNEXPECTED_ERROR = "Unexpected error occurred: ";
    private static final String ITEM_NOT_FOUND = "Food item with ID %d not found.";

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
    @Cacheable(value = "foodItemCache", key = "#fdcId")
    public ResponseEntity<String> fetchAndSaveFoodItem(String fdcId) {
        try {
            UsdaFoodResponseDTO response = usdaApiService.getFoodData(fdcId);

            if (response != null && response.getFoodNutrients() != null) {
                String description = response.getDescription();

                if (description != null && !description.isEmpty()) {
                    if (foodItemRepository.existsByName(description)) {
                        return ErrorResponseUtil.createErrorResponse(logger, "Food item already exists", HttpStatus.CONFLICT);
                    }

                    FoodItem foodItem = balancebite.utils.FoodItemUtil.convertToFoodItem(response);
                    foodItemRepository.save(foodItem);
                    return ResponseEntity.status(HttpStatus.CREATED).body("Food item saved successfully: " + description);
                }
            }
        } catch (UsdaApiException e) {
            logger.error("Error fetching food data from USDA API", e);
            return ErrorResponseUtil.createErrorResponse(logger, "Error fetching food data from USDA API: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid data received from USDA API", e);
            return ErrorResponseUtil.createErrorResponse(logger, "Invalid data received from USDA API: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error(UNEXPECTED_ERROR, e);
            return ErrorResponseUtil.createErrorResponse(logger, UNEXPECTED_ERROR + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ErrorResponseUtil.createErrorResponse(logger, "Failure to save food item", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Fetches multiple food items from the USDA API by a list of FDC IDs and saves them.
     * If a food item already exists in the database, it will not be added again.
     *
     * @param fdcIds List of FDC IDs for the food items to fetch.
     * @return A ResponseEntity with a list of descriptions of the food items that were successfully saved or an error message.
     */
    @Async
    public CompletableFuture<ResponseEntity<String>> fetchAndSaveAllFoodItems(List<String> fdcIds) {
        List<String> successfullySavedIds = new ArrayList<>();
        try {
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
        } catch (UsdaApiException e) {
            logger.error("Error fetching food data from USDA API", e);
            return CompletableFuture.completedFuture(ErrorResponseUtil.createErrorResponse(logger, "Error fetching food data from USDA API: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            logger.error(UNEXPECTED_ERROR, e);
            return CompletableFuture.completedFuture(ErrorResponseUtil.createErrorResponse(logger, UNEXPECTED_ERROR + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }

        if (successfullySavedIds.isEmpty()) {
            return CompletableFuture.completedFuture(ErrorResponseUtil.createErrorResponse(logger, "No food items were fetched and saved. Please check the provided FDC IDs.", HttpStatus.BAD_REQUEST));
        } else {
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.CREATED).body("Food items for the following FDC IDs were fetched and saved successfully: " + String.join(", ", successfullySavedIds)));
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
            return foodItemRepository.findById(id)
                    .map(foodItemMapper::toDTO)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ErrorResponseUtil.createErrorResponse(logger,
                            String.format(ITEM_NOT_FOUND, id), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            logger.error(UNEXPECTED_ERROR, e);
            return ErrorResponseUtil.createErrorResponse(logger,
                    UNEXPECTED_ERROR + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves all FoodItems from the database.
     *
     * @return A ResponseEntity with a list of all FoodItemDTOs, or an INTERNAL_SERVER_ERROR status in case of an error.
     */
    public ResponseEntity<List<FoodItemDTO>> getAllFoodItems() {
        try {
            List<FoodItemDTO> foodItemDTOs = foodItemRepository.findAll().stream()
                    .map(foodItemMapper::toDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(foodItemDTOs);
        } catch (Exception e) {
            logger.error(UNEXPECTED_ERROR, e);
            return ErrorResponseUtil.createErrorResponse(logger,
                    UNEXPECTED_ERROR + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deletes a FoodItem by its ID from the database.
     *
     * @param id The ID of the food item to delete.
     * @return A ResponseEntity indicating the result of the delete operation.
     */
    public ResponseEntity<String> deleteFoodItemById(Long id) {
        try {
            if (foodItemRepository.existsById(id)) {
                foodItemRepository.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ErrorResponseUtil.createErrorResponse(logger, String.format(ITEM_NOT_FOUND, id), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error(UNEXPECTED_ERROR, e);
            return ErrorResponseUtil.createErrorResponse(logger, UNEXPECTED_ERROR + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
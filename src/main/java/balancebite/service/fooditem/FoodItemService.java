package balancebite.service.fooditem;

import balancebite.dto.CloudinaryUploadResult;
import balancebite.dto.fooditem.FoodItemDTO;
import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.dto.fooditem.FoodItemInputDTO;
import balancebite.dto.fooditem.FoodItemNameDTO;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.errorHandling.UsdaApiException;
import balancebite.mapper.FoodItemMapper;
import balancebite.model.NutrientInfo;
import balancebite.model.foodItem.FoodCategory;
import balancebite.model.foodItem.FoodItem;
import balancebite.model.foodItem.FoodSource;
import balancebite.repository.FoodItemRepository;
import balancebite.repository.PromotedFoodItemRepository;
import balancebite.repository.UserRepository;
import balancebite.service.CloudinaryService;
import balancebite.service.UsdaApiService;
import balancebite.service.interfaces.fooditem.IFoodItemService;
import balancebite.errorHandling.EntityNotFoundException;
import balancebite.utils.FoodItemBulkFetchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final CloudinaryService cloudinaryService;
    private final PromotedFoodItemRepository promotedFoodItemRepository;
    private final UserRepository userRepository;

    /**
     * Constructor for dependency injection.
     *
     * @param foodItemRepository Repository for storing and retrieving FoodItems.
     * @param usdaApiService Service for interacting with the USDA API.
     * @param foodItemMapper Mapper for converting between FoodItem entities and DTOs.
     */
    public FoodItemService(FoodItemRepository foodItemRepository, UsdaApiService usdaApiService, FoodItemMapper foodItemMapper, CloudinaryService cloudinaryService, PromotedFoodItemRepository promotedFoodItemRepository, UserRepository userRepository) {
        this.foodItemRepository = foodItemRepository;
        this.usdaApiService = usdaApiService;
        this.foodItemMapper = foodItemMapper;
        this.cloudinaryService = cloudinaryService;
        this.promotedFoodItemRepository = promotedFoodItemRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new FoodItem from input DTO and saves it to the database.
     *
     * @param inputDTO The FoodItemInputDTO containing user-defined values.
     * @return The created FoodItem as a DTO.
     */
    @Override
    public FoodItemDTO createFoodItem(FoodItemInputDTO inputDTO) {
        log.info("Creating new FoodItem from user input: {}", inputDTO.getName());

        // 1) Enforce image exclusivity: at most ONE of (file | url)
        int sources = 0;
        if (inputDTO.getImageFile() != null && !inputDTO.getImageFile().isEmpty()) sources++;
        if (inputDTO.getImageUrl() != null && !inputDTO.getImageUrl().isBlank()) sources++;
        if (sources > 1) {
            throw new IllegalArgumentException("Provide exactly one of: imageFile or imageUrl.");
        }

        // 2) Map DTO -> entity
        FoodItem foodItem = foodItemMapper.toEntity(inputDTO);

        // 3) Automatic FoodSource assignment based on the authenticated user
        // The SecurityContextHolder contains the information processed by your JwtRequestFilter
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String currentUsername = auth.getName();

            // Retrieve the user from the database to check for an assigned FoodSource
            userRepository.findByUserName(currentUsername).ifPresent(user -> {
                if (user.getFoodSource() != null) {
                    foodItem.setFoodSource(user.getFoodSource());
                    log.info("Automatically assigned FoodSource '{}' to item '{}' from user '{}'",
                            user.getFoodSource(), foodItem.getName(), currentUsername);
                }
            });
        }

        // 4) Image handling
        String finalUrl = null;
        if (inputDTO.getImageFile() != null && !inputDTO.getImageFile().isEmpty()) {
            CloudinaryUploadResult r = cloudinaryService.uploadFile(inputDTO.getImageFile());
            finalUrl = r.getImageUrl();
        } else if (inputDTO.getImageUrl() != null && !inputDTO.getImageUrl().isBlank()) {
            finalUrl = inputDTO.getImageUrl().trim();
        }
        foodItem.setImageUrl(finalUrl);

        // 5) Persist and return
        foodItemRepository.save(foodItem);
        log.info("Successfully created FoodItem: {}", foodItem.getName());
        return foodItemMapper.toDTO(foodItem);
    }

    /**
     * Updates an existing {@link FoodItem} using the supplied DTO.
     * @param id    the FoodItem ID to update
     * @param input the incoming values
     * @return the updated item as {@link FoodItemDTO}
     */
    @Transactional
    public FoodItemDTO updateFoodItem(Long id, FoodItemInputDTO input) {
        FoodItem existing = foodItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Food item with ID " + id + " not found."));

        // --- 0) Enforce image source exclusivity: at most ONE of (file | url ) ---
        int sources = 0;
        if (input.getImageFile() != null && !input.getImageFile().isEmpty()) sources++;
        if (input.getImageUrl() != null && !input.getImageUrl().isBlank())  sources++;
        if (sources > 1) {
            throw new IllegalArgumentException("Provide exactly one of: imageFile or imageUrl.");
        }

        // --- 1) Basic fields ---
        existing.setName(input.getName());
        existing.setFdcId(input.getFdcId());
        existing.setPortionDescription(input.getPortionDescription());
        existing.setGramWeight(input.getGramWeight());
        existing.setSource(input.getSource());
        existing.setFoodSource(input.getFoodSource());
        existing.setFoodCategory(input.getFoodCategory());
        existing.setStoreBrand(input.getStoreBrand());

        // --- 2) Nutrients (replace collection) ---
        List<NutrientInfo> nutrients = (input.getNutrients() == null) ? List.of()
                : input.getNutrients().stream()
                .filter(n -> n.getValue() != null)
                .map(n -> new NutrientInfo(n.getNutrientName(), n.getValue(), n.getUnitName(), n.getNutrientId()))
                .collect(Collectors.toList());
        existing.getNutrients().clear();
        existing.getNutrients().addAll(nutrients);

        // --- 3) Image handling via central handler (update flow => may delete old when switching/clearing) ---
        String finalUrl = existing.getImageUrl(); // behouden wat er is

        if (input.getImageFile() != null && !input.getImageFile().isEmpty()) {
            CloudinaryUploadResult r = cloudinaryService.uploadFile(input.getImageFile());
            finalUrl = r.getImageUrl();
        } else if (input.getImageUrl() != null && !input.getImageUrl().isBlank()) {
            finalUrl = input.getImageUrl().trim();
        }

        existing.setImageUrl(finalUrl);

        // --- 4) Pricing ---
        existing.setPrice(input.getPrice());
        existing.setGrams(input.getGrams());

        foodItemRepository.save(existing);
        return foodItemMapper.toDTO(existing);
    }

    /**
     * Updates only the base {@code price} of an existing {@link FoodItem}.
     * @param id       the ID of the {@link FoodItem} to update
     * @param newPrice the new base price (may be {@code null} to clear)
     * @return the updated {@link FoodItemDTO}
     * @throws jakarta.persistence.EntityNotFoundException if the item with the given {@code id} does not exist
     */
    @Transactional
    public FoodItemDTO updateFoodItemPrice(Long id, java.math.BigDecimal newPrice) {
        // Load the entity or fail fast if it does not exist
        FoodItem item = foodItemRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Food item " + id + " not found"));

        // Update ONLY the base price:
        // - If a value is provided, standardize to 2 decimals (HALF_UP).
        // - If null is provided, clear the price on the entity.
        item.setPrice(newPrice != null ? newPrice.setScale(2, java.math.RoundingMode.HALF_UP) : null);

        // Persist the change
        foodItemRepository.save(item);

        // Return the mapped DTO; any derived fields are computed by the mapper
        return foodItemMapper.toDTO(item);
    }


    /**
     * Fetches a single food item from the USDA API by its FDC ID and saves it.
     * If the food item already exists in the database, it will not be added again.
     *
     * @param fdcId The FDC ID of the food item to fetch.
     * @return The USDA API response DTO containing details of the fetched food item.
     * @throws EntityAlreadyExistsException if the food item already exists in the database.
     * @throws IllegalArgumentException if the USDA API response is invalid.
     */
    @Override
    @Cacheable(value = "foodItemCache", key = "#fdcId")
    public UsdaFoodResponseDTO fetchAndSaveFoodItem(String fdcId) {
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

        // Return the response DTO for further use.
        return response;
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

        // Filter out FDC IDs that already exist in the database using the utility method
        List<String> newFdcIds = FoodItemBulkFetchUtil.filterExistingFdcIds(fdcIds, foodItemRepository, skippedItems);

        // If all FDC IDs already exist, return a response directly without making any API calls
        if (newFdcIds.isEmpty()) {
            log.info("All provided FDC IDs already exist in the database. No API calls needed.");
            return CompletableFuture.completedFuture(FoodItemBulkFetchUtil.createResponse(
                    "All provided FDC IDs already exist in the database.",
                    savedItems, skippedItems, invalidItems
            ));
        }

        // Attempt to fetch data from the USDA API for new FDC IDs
        List<UsdaFoodResponseDTO> responses;
        try {
            responses = usdaApiService.getMultipleFoodData(newFdcIds);
        } catch (UsdaApiException e) {
            // Handle any errors from the USDA API and return a response with the affected FDC IDs marked as invalid
            return CompletableFuture.completedFuture(
                    FoodItemBulkFetchUtil.handleUsdaApiError(newFdcIds, savedItems, skippedItems, invalidItems, e)
            );
        }

        // Track which FDC IDs received a valid response
        Set<String> receivedFdcIds = responses.stream()
                .map(response -> String.valueOf(response.getFdcId()))
                .collect(Collectors.toSet());

        // Add FDC IDs to 'invalidItems' if no valid response was received for them
        newFdcIds.stream()
                .filter(fdcId -> !receivedFdcIds.contains(fdcId))
                .forEach(invalidItems::add);

        // Process each response, saving valid items and marking invalid ones
        responses.forEach(response -> {
            if (FoodItemBulkFetchUtil.isResponseInvalid(response)) {
                // Add the FDC ID to invalidItems if the response is considered invalid
                invalidItems.add(String.valueOf(response.getFdcId()));
            } else {
                // Save the valid response and add its details to the savedItems list
                FoodItemBulkFetchUtil.processAndSaveResponse(response, foodItemRepository, savedItems);
            }
        });

        // Log the summary of the operation
        log.info("Total food items successfully saved: {}", savedItems.size());
        log.info("Total skipped items: {}", skippedItems.size());
        log.info("Total invalid items: {}", invalidItems.size());

        // Return the final response containing all saved, skipped, and invalid items
        return CompletableFuture.completedFuture(FoodItemBulkFetchUtil.createResponse(
                "Bulk food items fetched and processed.",
                savedItems, skippedItems, invalidItems
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
    @Transactional
    @Override
    public void deleteFoodItemById(Long id) {
        log.info("Deleting food item with ID: {}", id);
        if (!foodItemRepository.existsById(id)) {
            log.error("Food item with ID {} not found", id);
            throw new EntityNotFoundException("Food item with ID " + id + " not found.");
        }
        promotedFoodItemRepository.deleteByFoodItem_Id(id); // remove promo first
        foodItemRepository.deleteById(id);                  // then delete the item
        log.info("Successfully deleted food item with ID: {}", id);
    }

    /**
     * Retrieves a list of FoodItems whose names start with the given prefix.
     *
     * @param prefix The prefix to search for.
     * @return A list of FoodItemDTOs that match the criteria.
     */
    @Override
    public List<FoodItemDTO> getFoodItemsByNamePrefix(String prefix) {
        log.info("Searching for food items with names starting with: {}", prefix);
        List<FoodItem> foodItems = foodItemRepository.findByNameStartingWithIgnoreCase(prefix);
        if (foodItems.isEmpty()) {
            log.warn("No food items found with names starting with: {}", prefix);
            throw new EntityNotFoundException("No food items found with names starting with: " + prefix);
        }
        return foodItems.stream()
                .map(foodItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of all FoodItems, returning only their ID and name.
     *
     * @return A list of FoodItemNameDTOs containing only ID and name.
     */
    @Override
    public List<FoodItemNameDTO> getAllFoodItemNames() {
        log.info("Fetching all food item names and IDs.");
        return foodItemRepository.findAllFoodItemNames();
    }

    /**
     * Retrieves a list of FoodItems filtered by their food source.
     *
     * This method queries the repository for all food items that match the specified
     * {@link balancebite.model.foodItem.FoodSource}. It is useful for filtering food items
     * based on where they were purchased or originated, such as Albert Heijn or Jumbo.
     *
     * @param foodSource The {@link balancebite.model.foodItem.FoodSource} to filter by.
     * @return A list of {@link FoodItemDTO} instances from the specified food source.
     *         Returns an empty list if no matches are found.
     */
    @Override
    public List<FoodItemDTO> getFoodItemsByFoodSource(FoodSource foodSource) {
        log.info("Retrieving food items by food source: {}", foodSource);
        List<FoodItem> foodItems = foodItemRepository.findByFoodSource(foodSource);
        return foodItems.stream()
                .map(foodItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FoodItemDTO> getFoodItemsByCategory(FoodCategory category) {
        log.info("Retrieving food items by category: {}", category);
        return foodItemRepository.findByFoodCategory(category)
                .stream()
                .map(foodItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds items whose name contains the given query (case-insensitive).
     *
     * @param q substring to look for (e.g., "spinazie")
     * @return list of matching DTOs
     * @throws EntityNotFoundException when no items match
     */
    public List<FoodItemDTO> searchByNameSubstring(String q) {
        List<FoodItem> items = foodItemRepository.findByNameContainingIgnoreCase(q);
        if (items.isEmpty()) {
            throw new EntityNotFoundException("No food items found containing: " + q);
        }
        return items.stream().map(foodItemMapper::toDTO).collect(Collectors.toList());
    }


}

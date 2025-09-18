package balancebite.controller.fooditem;

import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.dto.fooditem.FoodItemDTO;
import balancebite.dto.fooditem.FoodItemInputDTO;
import balancebite.dto.fooditem.FoodItemNameDTO;
import balancebite.errorHandling.EntityAlreadyExistsException;
import balancebite.errorHandling.EntityNotFoundException;
import balancebite.model.foodItem.FoodCategory;
import balancebite.model.foodItem.FoodSource;
import balancebite.service.fooditem.FoodItemService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for managing FoodItem-related operations.
 */
@RestController
@RequestMapping("/fooditems")
public class FoodItemController {

    private static final Logger log = LoggerFactory.getLogger(FoodItemController.class);
    private final FoodItemService foodItemService;

    /**
     * Constructor for dependency injection.
     *
     * @param foodItemService Service for managing FoodItem operations.
     */
    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createFoodItem(
            @RequestPart("foodItemInputDTO") String foodItemInputDTOJson,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            // Parse JSON -> DTO (simple, no bean needed)
            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
            om.configure(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            FoodItemInputDTO inputDTO = om.readValue(foodItemInputDTOJson, FoodItemInputDTO.class);

            // Attach file (may be null)
            inputDTO.setImageFile(imageFile);

            FoodItemDTO created = foodItemService.createFoodItem(inputDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid foodItemInputDTO JSON"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Create failed."));
        }
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateFoodItem(
            @PathVariable Long id,
            @RequestPart("foodItemInputDTO") String foodItemInputDTOJson,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
            om.configure(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            FoodItemInputDTO inputDTO = om.readValue(foodItemInputDTOJson, FoodItemInputDTO.class);

            // Always pass file (may be null)
            inputDTO.setImageFile(imageFile);

            FoodItemDTO updated = foodItemService.updateFoodItem(id, inputDTO);
            return ResponseEntity.ok(updated);

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid foodItemInputDTO JSON"));
        } catch (balancebite.errorHandling.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Update failed."));
        }
    }


    /**
     * Updates a FoodItem by ID using a JSON body.
     * If you need file upload for images, switch to the multipart variant below.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFoodItem(@PathVariable Long id,
                                            @RequestBody @Valid FoodItemInputDTO inputDTO) {
        log.info("Updating food item with ID: {}", id);
        try {
            FoodItemDTO updated = foodItemService.updateFoodItem(id, inputDTO);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            log.warn("Food item not found for update with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error while updating food item with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred."));
        }
    }


    /**
     * Endpoint to fetch and save a single FoodItem by FDC ID.
     * Calls the FoodItemService to retrieve and save the food item.
     *
     * @param fdcId The FDC ID of the food item to fetch.
     * @return A ResponseEntity with details of the saved food item or an error message.
     */
    @GetMapping("/fetch/{fdcId:[0-9]+}")
    public ResponseEntity<?> fetchFoodItem(@PathVariable String fdcId) {
        log.info("Fetching food item with FDC ID: {}", fdcId);
        try {
            UsdaFoodResponseDTO response = foodItemService.fetchAndSaveFoodItem(fdcId);
            log.info("Successfully fetched and saved food item with FDC ID: {}", fdcId);

            // Create a response with details of the saved item.
            Map<String, String> responseBody = Map.of(
                    "message", "Food item fetched and saved successfully",
                    "fdcId", String.valueOf(response.getFdcId()),
                    "name", response.getDescription()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
        } catch (EntityAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint to fetch and save multiple FoodItems by a list of FDC IDs.
     * Calls the FoodItemService to retrieve and save the food items asynchronously.
     *
     * @param fdcIds The list of FDC IDs of the food items to fetch.
     * @return A CompletableFuture containing a ResponseEntity with the list of saved items.
     */
    @PostMapping("/bulk-fetch-items")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> fetchAllFoodItems(@RequestBody List<String> fdcIds) {
        log.info("Fetching multiple food items with FDC IDs: {}", fdcIds);
        return foodItemService.fetchAndSaveAllFoodItems(fdcIds)
                .thenApply(result -> {
                    log.info("Bulk fetch and save completed for food items.");
                    return ResponseEntity.status(HttpStatus.CREATED).body(result);
                })
                .exceptionally(e -> {
                    log.error("Error during bulk fetch and save: {}", e.getMessage(), e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "An unexpected error occurred during bulk fetch."));
                });
    }

    /**
     * Endpoint to retrieve a single FoodItem by its ID from the database.
     *
     * @param id The ID of the food item to retrieve.
     * @return A ResponseEntity with the corresponding FoodItemDTO or a NOT_FOUND (404) status if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFoodItemById(@PathVariable Long id) {
        log.info("Retrieving food item with ID: {}", id);
        try {
            FoodItemDTO foodItem = foodItemService.getFoodItemById(id);
            return ResponseEntity.ok(foodItem);
        } catch (EntityNotFoundException e) {
            log.warn("Food item not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during retrieval for food item ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint to retrieve all FoodItems from the database.
     *
     * @return A ResponseEntity with a list of all FoodItemDTOs or NO_CONTENT (204) status if none found.
     */
    @GetMapping
    public ResponseEntity<?> getAllFoodItems() {
        log.info("Retrieving all food items from the database.");
        List<FoodItemDTO> foodItems = foodItemService.getAllFoodItems();
        if (foodItems.isEmpty()) {
            log.info("No food items found.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(foodItems);
    }

    /**
     * Endpoint to delete a FoodItem by its ID.
     * Calls the FoodItemService to delete the food item from the database.
     *
     * @param id The ID of the food item to delete.
     * @return A ResponseEntity indicating the result of the delete operation, either NO_CONTENT (204) if deleted or NOT_FOUND (404) if not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFoodItemById(@PathVariable Long id) {
        log.info("Deleting food item with ID: {}", id);
        try {
            foodItemService.deleteFoodItemById(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.warn("Food item not found for deletion with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Food item with ID " + id + " not found."));
        } catch (Exception e) {
            log.error("Unexpected error during deletion for food item ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint to retrieve FoodItems by a prefix of their name.
     *
     * @param prefix The prefix to search for.
     * @return A ResponseEntity with the list of FoodItemDTOs that match the criteria.
     */
    @GetMapping("/search-by-name")
    public ResponseEntity<?> getFoodItemsByNamePrefix(@RequestParam String prefix) {
        log.info("Fetching food items with names starting with: {}", prefix);
        try {
            List<FoodItemDTO> foodItems = foodItemService.getFoodItemsByNamePrefix(prefix);
            return ResponseEntity.ok(foodItems);
        } catch (EntityNotFoundException e) {
            log.warn("No food items found with names starting with: {}", prefix);
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while fetching food items with prefix: {}", prefix, e);
            return ResponseEntity.status(500).body("An unexpected error occurred.");
        }
    }

    /**
     * Endpoint to retrieve only the IDs and names of all FoodItems.
     * This is optimized for search functionality where full food item details are not needed.
     *
     * @return A ResponseEntity containing a list of FoodItemDTOs with only ID and name fields.
     */
    @GetMapping("/names")
    public ResponseEntity<?> getAllFoodItemNames() {
        log.info("Fetching all food item names and IDs.");
        List<FoodItemNameDTO> foodItemNames = foodItemService.getAllFoodItemNames();

        if (foodItemNames.isEmpty()) {
            log.info("No food item names found.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(foodItemNames);
    }

    /**
     * Endpoint to retrieve FoodItems by their food source.
     *
     * @param source The food source (e.g., ALBERT_HEIJN, JUMBO) as enum name.
     * @return A ResponseEntity with the list of matching FoodItemDTOs.
     */
    @GetMapping("/by-source")
    public ResponseEntity<?> getFoodItemsByFoodSource(@RequestParam("source") String source) {
        log.info("Fetching food items with food source: {}", source);
        try {
            FoodSource foodSource = FoodSource.valueOf(source.toUpperCase());
            List<FoodItemDTO> foodItems = foodItemService.getFoodItemsByFoodSource(foodSource);
            if (foodItems.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(foodItems);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid food source: {}", source);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid food source: " + source));
        } catch (Exception e) {
            log.error("Error while fetching food items by source: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint to retrieve all predefined food sources.
     *
     * @return A list of all FoodSource enum values.
     */
    @GetMapping("/sources")
    public ResponseEntity<?> getAllFoodSources() {
        log.info("Fetching all predefined food sources.");
        return ResponseEntity.ok(FoodSource.values());
    }

    @GetMapping("/by-category")
    public ResponseEntity<?> getFoodItemsByCategory(@RequestParam("category") String category) {
        log.info("Fetching food items with category: {}", category);
        try {
            FoodCategory foodCategory = FoodCategory.valueOf(category.toUpperCase());
            List<FoodItemDTO> foodItems = foodItemService.getFoodItemsByCategory(foodCategory);
            if (foodItems.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(foodItems);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid food category: {}", category);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid food category: " + category));
        } catch (Exception e) {
            log.error("Error while fetching food items by category: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Searches items by substring (case-insensitive).
     * Example: /fooditems/search?q=spinazie
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchFoodItems(@RequestParam("q") String q) {
        log.info("Searching food items containing: {}", q);
        try {
            List<FoodItemDTO> foodItems = foodItemService.searchByNameSubstring(q);
            return ResponseEntity.ok(foodItems);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error while searching food items: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred."));
        }
    }


    @GetMapping("/promoted-by-source")
    public ResponseEntity<?> getPromotedBySource(@RequestParam String source) {
        try {
            var list = foodItemService
                    .getFoodItemsByFoodSource(FoodSource.valueOf(source.toUpperCase()))
                    .stream()
                    .filter(FoodItemDTO::isPromoted)
                    .toList();

            return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid food source: " + source));
        }
    }

    @GetMapping("/promoted-by-category")
    public ResponseEntity<?> getPromotedByCategory(@RequestParam String category) {
        try {
            var list = foodItemService
                    .getFoodItemsByCategory(FoodCategory.valueOf(category.toUpperCase()))
                    .stream()
                    .filter(FoodItemDTO::isPromoted)
                    .toList();

            return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid food category: " + category));
        }
    }

}

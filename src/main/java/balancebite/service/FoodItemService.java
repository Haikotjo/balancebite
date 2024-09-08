package balancebite.service;

import balancebite.dto.fooditem.FoodItemDTO;
import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.exception.UsdaApiException;
import balancebite.mapper.FoodItemMapper;
import balancebite.model.FoodItem;
import balancebite.model.NutrientInfo;
import balancebite.repository.FoodItemRepository;
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

    private final FoodItemRepository foodItemRepository;
    private final UsdaApiService usdaApiService;

    /**
     * Constructor for dependency injection.
     *
     * @param foodItemRepository Repository for storing and retrieving FoodItems.
     * @param usdaApiService Service for interacting with the USDA API.
     */
    public FoodItemService(FoodItemRepository foodItemRepository, UsdaApiService usdaApiService) {
        this.foodItemRepository = foodItemRepository;
        this.usdaApiService = usdaApiService;
    }

    /**
     * Fetches food data from the USDA API by FDC ID and saves it as a FoodItem.
     * If the food item already exists in the database, it will not be added again.
     *
     * @param fdcId The FDC ID of the food item to fetch.
     * @return True if the food item was newly created and saved, false if it already existed.
     */
    public boolean fetchAndSaveFoodItem(String fdcId) {
        try {
            UsdaFoodResponseDTO response = usdaApiService.getFoodData(fdcId);

            if (response != null && response.getFoodNutrients() != null && response.getDescription() != null && !response.getDescription().isEmpty()) {
                if (foodItemRepository.existsByName(response.getDescription())) {
                    return false; // Food item already exists
                }

                // Extract the primary portion from the response if available
                String portionDescription = null;
                double gramWeight = 0;
                if (response.getFoodPortions() != null && !response.getFoodPortions().isEmpty()) {
                    UsdaFoodResponseDTO.FoodPortionDTO portion = response.getFoodPortions().get(0);
                    portionDescription = portion.getAmount() + " " + (portion.getModifier() != null ? portion.getModifier() : portion.getMeasureUnit().getName());
                    gramWeight = portion.getGramWeight();
                }

                FoodItem foodItem = new FoodItem(
                        response.getDescription(),
                        response.getFoodNutrients().stream()
                                .map(n -> new NutrientInfo(
                                        n.getNutrient().getName(),
                                        n.getAmount(),
                                        n.getUnitName(),
                                        n.getNutrient().getNutrientId()
                                ))
                                .collect(Collectors.toList()),
                        portionDescription, // Add portion description
                        gramWeight // Add gram weight
                );
                foodItemRepository.save(foodItem);
                return true; // Food item successfully saved
            }
        } catch (UsdaApiException e) {
            System.err.println("Error fetching food data from USDA API: " + e.getMessage());
        }
        return false; // Failure to save food item
    }

    /**
     * Fetches multiple food items from the USDA API by a list of FDC IDs and saves them.
     * If a food item already exists in the database, it will not be added again.
     *
     * @param fdcIds List of FDC IDs for the food items to fetch.
     * @return A list of descriptions of the food items that were successfully saved.
     */
    public List<String> fetchAndSaveAllFoodItems(List<String> fdcIds) {
        List<String> successfullySavedIds = new ArrayList<>();
        try {
            List<UsdaFoodResponseDTO> responses = usdaApiService.getMultipleFoodData(fdcIds);

            for (UsdaFoodResponseDTO response : responses) {
                if (response.getFoodNutrients() != null && response.getDescription() != null && !response.getDescription().isEmpty()) {
                    if (!foodItemRepository.existsByName(response.getDescription())) {

                        // Extract the primary portion from the response if available
                        String portionDescription = null;
                        double gramWeight = 0;
                        if (response.getFoodPortions() != null && !response.getFoodPortions().isEmpty()) {
                            UsdaFoodResponseDTO.FoodPortionDTO portion = response.getFoodPortions().get(0);
                            portionDescription = portion.getAmount() + " " + (portion.getModifier() != null ? portion.getModifier() : portion.getMeasureUnit().getName());
                            gramWeight = portion.getGramWeight();
                        }

                        FoodItem foodItem = new FoodItem(
                                response.getDescription(),
                                response.getFoodNutrients().stream()
                                        .map(n -> new NutrientInfo(
                                                n.getNutrient().getName(),
                                                n.getAmount(),
                                                n.getUnitName(),
                                                n.getNutrient().getNutrientId()
                                        ))
                                        .collect(Collectors.toList()),
                                portionDescription, // Add portion description
                                gramWeight // Add gram weight
                        );
                        foodItemRepository.save(foodItem);
                        successfullySavedIds.add(response.getDescription());
                    }
                }
            }
        } catch (UsdaApiException e) {
            System.err.println("Error fetching food data from USDA API: " + e.getMessage());
        }
        return successfullySavedIds;
    }

    /**
     * Retrieves a single FoodItem by its ID from the database.
     *
     * @param id The ID of the food item to retrieve.
     * @return The corresponding FoodItemDTO, or null if not found.
     */
    public FoodItemDTO getFoodItemById(Long id) {
        Optional<FoodItem> foodItemOptional = foodItemRepository.findById(id);
        return foodItemOptional.map(FoodItemMapper::toDTO).orElse(null);
    }

    /**
     * Retrieves all FoodItems from the database.
     *
     * @return A list of all FoodItemDTOs in the database.
     */
    public List<FoodItemDTO> getAllFoodItems() {
        List<FoodItem> foodItems = foodItemRepository.findAll();
        return foodItems.stream()
                .map(FoodItemMapper::toDTO)
                .collect(Collectors.toList());
    }
}

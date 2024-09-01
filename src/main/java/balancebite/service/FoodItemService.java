package balancebite.service;

import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.model.FoodItem;
import balancebite.model.NutrientInfo;
import balancebite.repository.FoodItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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
     *
     * @param fdcId The FDC ID of the food item to fetch.
     * @return True if the food item was newly created and saved, false if it already existed.
     */
    public boolean fetchAndSaveFoodItem(String fdcId) {
        try {
            UsdaFoodResponseDTO response = usdaApiService.getFoodData(fdcId);

            if (response != null && response.getFoodNutrients() != null) {
                if (foodItemRepository.existsByName(response.getDescription())) {
                    return false;
                }

                FoodItem foodItem = new FoodItem(response.getDescription(),
                        response.getFoodNutrients().stream()
                                .map(n -> new NutrientInfo(
                                        n.getNutrient().getName(),
                                        n.getAmount(),
                                        n.getUnitName(),
                                        n.getNutrient().getNutrientId()
                                ))
                                .collect(Collectors.toList()));
                foodItemRepository.save(foodItem);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Fetches multiple food items from the USDA API by a list of FDC IDs and saves them.
     *
     * @param fdcIds List of FDC IDs for the food items to fetch.
     */
    public void fetchAndSaveAllFoodItems(List<String> fdcIds) {
        try {
            List<UsdaFoodResponseDTO> responses = usdaApiService.getMultipleFoodData(fdcIds);

            for (UsdaFoodResponseDTO response : responses) {
                if (response.getFoodNutrients() != null) {
                    if (!foodItemRepository.existsByName(response.getDescription())) {
                        FoodItem foodItem = new FoodItem(response.getDescription(),
                                response.getFoodNutrients().stream()
                                        .map(n -> new NutrientInfo(
                                                n.getNutrient().getName(),
                                                n.getAmount(),
                                                n.getUnitName(),
                                                n.getNutrient().getNutrientId()
                                        ))
                                        .collect(Collectors.toList()));
                        foodItemRepository.save(foodItem);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package balancebite.service;

import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.model.FoodItem;
import balancebite.model.NutrientInfo;
import balancebite.repository.FoodItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodItemService {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private UsdaApiService usdaApiService;

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

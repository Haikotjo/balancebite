package balancebite.mapper;

import balancebite.dto.FoodItemDTO;
import balancebite.dto.FoodItemInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.model.FoodItem;
import balancebite.model.NutrientInfo;

import java.util.List;
import java.util.stream.Collectors;

public class FoodItemMapper {

    public static FoodItem toEntity(FoodItemInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        List<NutrientInfo> nutrients = inputDTO.getNutrients().stream()
                .map(n -> new NutrientInfo(n.getNutrientName(), n.getValue(), n.getUnitName()))
                .collect(Collectors.toList());
        return new FoodItem(inputDTO.getName(), nutrients);
    }

    public static FoodItemDTO toDTO(FoodItem foodItem) {
        if (foodItem == null) {
            return null;
        }
        List<NutrientInfoDTO> nutrients = foodItem.getNutrients().stream()
                .map(n -> new NutrientInfoDTO(n.getNutrientName(), n.getValue(), n.getUnitName()))
                .collect(Collectors.toList());
        return new FoodItemDTO(foodItem.getId(), foodItem.getName(), nutrients);
    }
}

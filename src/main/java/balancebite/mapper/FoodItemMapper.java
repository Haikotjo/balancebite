package balancebite.mapper;

import balancebite.dto.fooditem.FoodItemDTO;
import balancebite.dto.fooditem.FoodItemInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.model.FoodItem;
import balancebite.model.NutrientInfo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between FoodItem entities and their corresponding DTOs.
 * Provides methods to map between FoodItem, FoodItemDTO, and FoodItemInputDTO.
 */
@Component
public class FoodItemMapper {

    /**
     * Converts a FoodItemInputDTO to a FoodItem entity.
     *
     * @param inputDTO The FoodItemInputDTO to convert.
     * @return The corresponding FoodItem entity, or null if the inputDTO is null.
     */
    public FoodItem toEntity(FoodItemInputDTO inputDTO) {
        return Optional.ofNullable(inputDTO)
                .map(dto -> {
                    FoodItem foodItem = new FoodItem(dto.getName(),dto.getFdcId(), dto.getPortionDescription(), dto.getGramWeight());
                    List<NutrientInfo> nutrients = dto.getNutrients() != null ?
                            dto.getNutrients().stream()
                                    .map(n -> new NutrientInfo(n.getNutrientName(), n.getValue(), n.getUnitName(), n.getNutrientId()))
                                    .collect(Collectors.toList()) : List.of();
                    foodItem.setNutrients(nutrients);
                    return foodItem;
                })
                .orElse(null);
    }

    /**
     * Converts a FoodItem entity to a FoodItemDTO.
     *
     * @param foodItem The FoodItem entity to convert.
     * @return The corresponding FoodItemDTO, or null if the foodItem is null.
     */
    public FoodItemDTO toDTO(FoodItem foodItem) {
        if (foodItem == null) {
            return null;
        }
        return new FoodItemDTO(
                foodItem.getId(),
                foodItem.getName(),
                foodItem.getFdcId(),
                foodItem.getNutrients() != null ?
                        foodItem.getNutrients().stream()
                                .map(n -> new NutrientInfoDTO(n.getNutrientName(), n.getValue(), n.getUnitName(), n.getNutrientId()))
                                .collect(Collectors.toList()) : List.of(),
                foodItem.getPortionDescription(),
                foodItem.getGramWeight()
        );
    }
}

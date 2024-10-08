package balancebite.mapper;

import balancebite.dto.fooditem.FoodItemDTO;
import balancebite.dto.fooditem.FoodItemInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.model.FoodItem;
import balancebite.model.NutrientInfo;
import org.springframework.stereotype.Component;

import java.util.List;
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
        if (inputDTO == null) {
            return null;
        }
        List<NutrientInfo> nutrients = inputDTO.getNutrients().stream()
                .map(n -> new NutrientInfo(n.getNutrientName(), n.getValue(), n.getUnitName(), n.getNutrientId()))
                .collect(Collectors.toList());
        return new FoodItem(
                inputDTO.getName(),
                nutrients,
                inputDTO.getPortionDescription(), // Map portion description
                inputDTO.getGramWeight() // Map gram weight
        );
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
        List<NutrientInfoDTO> nutrients = foodItem.getNutrients().stream()
                .map(n -> new NutrientInfoDTO(n.getNutrientName(), n.getValue(), n.getUnitName(), n.getNutrientId()))
                .collect(Collectors.toList());
        return new FoodItemDTO(
                foodItem.getId(),
                foodItem.getName(),
                nutrients,
                foodItem.getPortionDescription(), // Map portion description
                foodItem.getGramWeight() // Map gram weight
        );
    }
}

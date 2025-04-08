package balancebite.mapper;

import balancebite.dto.fooditem.FoodItemDTO;
import balancebite.dto.fooditem.FoodItemInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.model.FoodItem;
import balancebite.model.NutrientInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(FoodItemMapper.class);

    /**
     * Converts a FoodItemInputDTO to a FoodItem entity.
     *
     * @param inputDTO The FoodItemInputDTO to convert.
     * @return The corresponding FoodItem entity, or null if the inputDTO is null.
     */
    public FoodItem toEntity(FoodItemInputDTO inputDTO) {
        log.info("Converting FoodItemInputDTO to FoodItem entity.");
        return Optional.ofNullable(inputDTO)
                .map(dto -> {
                    log.debug("Mapping fields from FoodItemInputDTO to FoodItem.");
                    FoodItem foodItem = new FoodItem(
                            dto.getName(),
                            dto.getFdcId(),
                            dto.getPortionDescription(),
                            dto.getGramWeight(),
                            dto.getSource()
                    );
                    List<NutrientInfo> nutrients = dto.getNutrients() != null ?
                            dto.getNutrients().stream()
                                    .map(n -> new NutrientInfo(n.getNutrientName(), n.getValue(), n.getUnitName(), n.getNutrientId()))
                                    .collect(Collectors.toList()) : List.of();
                    foodItem.setNutrients(nutrients);

                    log.debug("Finished mapping FoodItemInputDTO to FoodItem entity: {}", foodItem);
                    return foodItem;
                })
                .orElseGet(() -> {
                    log.warn("Received null FoodItemInputDTO, returning null for FoodItem.");
                    return null;
                });
    }

    /**
     * Converts a FoodItem entity to a FoodItemDTO.
     *
     * @param foodItem The FoodItem entity to convert.
     * @return The corresponding FoodItemDTO, or null if the foodItem is null.
     */
    public FoodItemDTO toDTO(FoodItem foodItem) {
        log.info("Converting FoodItem entity to FoodItemDTO.");
        if (foodItem == null) {
            log.warn("Received null FoodItem entity, returning null for FoodItemDTO.");
            return null;
        }
        FoodItemDTO dto = new FoodItemDTO(
                foodItem.getId(),
                foodItem.getName(),
                foodItem.getFdcId(),
                foodItem.getNutrients() != null ?
                        foodItem.getNutrients().stream()
                                .map(n -> new NutrientInfoDTO(n.getNutrientName(), n.getValue(), n.getUnitName(), n.getNutrientId()))
                                .collect(Collectors.toList()) : List.of(),
                foodItem.getPortionDescription(),
                foodItem.getGramWeight(),
                foodItem.getSource()
        );

        log.debug("Finished mapping FoodItem entity to FoodItemDTO: {}", dto);
        return dto;
    }
}

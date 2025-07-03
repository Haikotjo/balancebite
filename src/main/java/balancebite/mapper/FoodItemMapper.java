package balancebite.mapper;

import balancebite.dto.fooditem.FoodItemDTO;
import balancebite.dto.fooditem.FoodItemInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.model.foodItem.FoodItem;
import balancebite.model.NutrientInfo;
import balancebite.model.foodItem.PromotedFoodItem;
import balancebite.service.fooditem.PromotedFoodItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between FoodItem entities and their corresponding DTOs.
 * Provides methods to map between FoodItem, FoodItemDTO, and FoodItemInputDTO.
 */
@Component
public class FoodItemMapper {

    private final PromotedFoodItemService promotedFoodItemService;

    public FoodItemMapper(PromotedFoodItemService promotedFoodItemService) {
        this.promotedFoodItemService = promotedFoodItemService;
    }

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
                            dto.getSource(),
                            dto.getFoodSource()
                    );
                    List<NutrientInfo> nutrients = dto.getNutrients() != null ?
                            dto.getNutrients().stream()
                                    .map(n -> new NutrientInfo(n.getNutrientName(), n.getValue(), n.getUnitName(), n.getNutrientId()))
                                    .collect(Collectors.toList()) : List.of();
                    foodItem.setNutrients(nutrients);
                    foodItem.setFoodCategory(dto.getFoodCategory());

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
        Optional<PromotedFoodItem> promotion = promotedFoodItemService.getActivePromotion(foodItem.getId());

        boolean promoted = promotion.isPresent();
        LocalDateTime startDate = promotion.map(PromotedFoodItem::getStartDate).orElse(null);
        LocalDateTime endDate = promotion.map(PromotedFoodItem::getEndDate).orElse(null);

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
                foodItem.getSource(),
                foodItem.getFoodSource(),
                promoted,
                startDate,
                endDate,
                foodItem.getFoodCategory()
        );


        log.debug("Finished mapping FoodItem entity to FoodItemDTO: {}", dto);
        return dto;
    }
}
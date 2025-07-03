package balancebite.mapper;

import balancebite.dto.fooditem.PromotedFoodItemDTO;
import balancebite.dto.fooditem.PromotedFoodItemInputDTO;
import balancebite.model.foodItem.FoodItem;
import balancebite.model.foodItem.PromotedFoodItem;
import org.springframework.stereotype.Component;

@Component
public class PromotedFoodItemMapper {

    /**
     * Maps input DTO to PromotedFoodItem entity.
     */
    public PromotedFoodItem toEntity(PromotedFoodItemInputDTO dto, FoodItem foodItem) {
        PromotedFoodItem entity = new PromotedFoodItem();
        entity.setFoodItem(foodItem);
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        return entity;
    }

    /**
     * Maps entity to output DTO.
     */
    public PromotedFoodItemDTO toDTO(PromotedFoodItem entity) {
        return new PromotedFoodItemDTO(
                entity.getId(),
                entity.getFoodItem().getId(),
                entity.getFoodItem().getName(),
                entity.getStartDate(),
                entity.getEndDate()
        );
    }
}

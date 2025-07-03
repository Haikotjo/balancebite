package balancebite.service.interfaces.fooditem;

import balancebite.dto.fooditem.PromotedFoodItemInputDTO;
import balancebite.model.foodItem.PromotedFoodItem;

public interface IPromotedFoodItemService {
    PromotedFoodItem createPromotion(PromotedFoodItemInputDTO inputDTO);
    void deletePromotion(Long promotionId);
}

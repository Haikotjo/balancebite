package balancebite.service.fooditem;

import balancebite.dto.fooditem.PromotedFoodItemInputDTO;
import balancebite.errorHandling.EntityNotFoundException;
import balancebite.model.foodItem.FoodItem;
import balancebite.model.foodItem.PromotedFoodItem;
import balancebite.repository.FoodItemRepository;
import balancebite.repository.PromotedFoodItemRepository;
import balancebite.service.interfaces.fooditem.IPromotedFoodItemService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PromotedFoodItemService implements IPromotedFoodItemService {

    private final PromotedFoodItemRepository promotedFoodItemRepository;
    private final FoodItemRepository foodItemRepository;

    public PromotedFoodItemService(PromotedFoodItemRepository promotedFoodItemRepository,
                                       FoodItemRepository foodItemRepository) {
        this.promotedFoodItemRepository = promotedFoodItemRepository;
        this.foodItemRepository = foodItemRepository;
    }

    @Override
    public PromotedFoodItem createPromotion(PromotedFoodItemInputDTO inputDTO) {
        FoodItem foodItem = foodItemRepository.findById(inputDTO.getFoodItemId())
                .orElseThrow(() -> new EntityNotFoundException("FoodItem not found"));

        // Check of er al een promotie is voor dit foodItem
        promotedFoodItemRepository.findByFoodItemId(foodItem.getId())
                .ifPresent(p -> {
                    throw new IllegalStateException("This food item is already promoted");
                });

        LocalDateTime startDate = inputDTO.getStartDate();
        LocalDateTime endDate = startDate.plusDays(6); // incl. startdag = 7 dagen totaal

        PromotedFoodItem promotion = new PromotedFoodItem();
        promotion.setFoodItem(foodItem);
        promotion.setStartDate(startDate);
        promotion.setEndDate(endDate);

        return promotedFoodItemRepository.save(promotion);
    }

    @Override
    public void deletePromotion(Long promotionId) {
        if (!promotedFoodItemRepository.existsById(promotionId)) {
            throw new EntityNotFoundException("Promotion not found");
        }
        promotedFoodItemRepository.deleteById(promotionId);
    }

    public Optional<PromotedFoodItem> getActivePromotion(Long foodItemId) {
        LocalDateTime now = LocalDateTime.now();
        return promotedFoodItemRepository.findByFoodItemId(foodItemId)
                .filter(promotion ->
                        (promotion.getStartDate().isBefore(now) || promotion.getStartDate().isEqual(now)) &&
                                (promotion.getEndDate().isAfter(now) || promotion.getEndDate().isEqual(now))
                );
    }
}

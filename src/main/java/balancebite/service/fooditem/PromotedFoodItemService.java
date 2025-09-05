package balancebite.service.fooditem;

import balancebite.dto.fooditem.PromotedFoodItemInputDTO;
import balancebite.errorHandling.EntityNotFoundException;
import balancebite.mapper.PromotedFoodItemMapper;
import balancebite.model.foodItem.FoodItem;
import balancebite.model.foodItem.PromotedFoodItem;
import balancebite.repository.FoodItemRepository;
import balancebite.repository.PromotedFoodItemRepository;
import balancebite.service.interfaces.fooditem.IPromotedFoodItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PromotedFoodItemService implements IPromotedFoodItemService {

    private final PromotedFoodItemRepository promotedFoodItemRepository;
    private final FoodItemRepository foodItemRepository;
    private final PromotedFoodItemMapper promotedFoodItemMapper;

    public PromotedFoodItemService(PromotedFoodItemRepository promotedFoodItemRepository,
                                   FoodItemRepository foodItemRepository,
                                   PromotedFoodItemMapper promotedFoodItemMapper) {
        this.promotedFoodItemRepository = promotedFoodItemRepository;
        this.foodItemRepository = foodItemRepository;
        this.promotedFoodItemMapper = promotedFoodItemMapper;
    }

    /**
     * Creates a promotion for a given FoodItem.
     * - Validates that no promotion exists yet for this FoodItem.
     * - Uses mapper to copy fields incl. promoPrice/salePercentage/saleDescription.
     * - Sets endDate = startDate + 6 days if not provided (7 days window).
     */
    @Override
    @Transactional
    public PromotedFoodItem createPromotion(PromotedFoodItemInputDTO inputDTO) {
        // Load target food item or fail
        FoodItem foodItem = foodItemRepository.findById(inputDTO.getFoodItemId())
                .orElseThrow(() -> new EntityNotFoundException("FoodItem not found"));

        // Ensure no existing promotion for this item
        promotedFoodItemRepository.findByFoodItemId(foodItem.getId())
                .ifPresent(p -> { throw new IllegalStateException("This food item is already promoted"); });

        // Map fields
        PromotedFoodItem promotion = promotedFoodItemMapper.toEntity(inputDTO, foodItem);

        // Default endDate → 7 days window
        if (promotion.getEndDate() == null) {
            promotion.setEndDate(promotion.getStartDate().plusDays(6));
        }

        // Optional: sanity check that window is valid (defense-in-depth)
        if (promotion.getStartDate() == null || promotion.getEndDate() == null
                || !promotion.getStartDate().isBefore(promotion.getEndDate())) {
            throw new IllegalArgumentException("Invalid promotion window");
        }

        return promotedFoodItemRepository.save(promotion);
    }

    /**
     * Updates an existing promotion by ID.
     * - Keeps the same FoodItem; updates dates/price/percentage/description.
     * - If endDate is null, re-apply default window (start + 6 days).
     */
    @Transactional
    public PromotedFoodItem updatePromotion(Long promotionId, PromotedFoodItemInputDTO inputDTO) {
        PromotedFoodItem existing = promotedFoodItemRepository.findById(promotionId)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found"));

        // Never switch the foodItem on update to avoid conflicts
        if (!existing.getFoodItem().getId().equals(inputDTO.getFoodItemId())) {
            throw new IllegalArgumentException("foodItemId mismatch for update");
        }

        // Update core fields
        existing.setStartDate(inputDTO.getStartDate());
        existing.setEndDate(inputDTO.getEndDate() != null
                ? inputDTO.getEndDate()
                : inputDTO.getStartDate().plusDays(6));
        existing.setPromoPrice(inputDTO.getPromoPrice());
        existing.setSalePercentage(inputDTO.getSalePercentage());
        existing.setSaleDescription(inputDTO.getSaleDescription());

        if (existing.getStartDate() == null || existing.getEndDate() == null
                || !existing.getStartDate().isBefore(existing.getEndDate())) {
            throw new IllegalArgumentException("Invalid promotion window");
        }

        return promotedFoodItemRepository.save(existing);
    }

    @Override
    @Transactional
    public void deletePromotion(Long promotionId) {
        if (!promotedFoodItemRepository.existsById(promotionId)) {
            throw new EntityNotFoundException("Promotion not found");
        }
        promotedFoodItemRepository.deleteById(promotionId);
    }

    /**
     * Returns an active promotion for the given FoodItem ID (now within [start,end]).
     */
    public Optional<PromotedFoodItem> getActivePromotion(Long foodItemId) {
        LocalDateTime now = LocalDateTime.now();
        return promotedFoodItemRepository.findByFoodItemId(foodItemId)
                .filter(p -> ( !p.getStartDate().isAfter(now) ) && ( !p.getEndDate().isBefore(now) ));
    }
}

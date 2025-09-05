package balancebite.mapper;

import balancebite.dto.fooditem.PromotedFoodItemDTO;
import balancebite.dto.fooditem.PromotedFoodItemInputDTO;
import balancebite.model.foodItem.FoodItem;
import balancebite.model.foodItem.PromotedFoodItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Component
public class PromotedFoodItemMapper {

    // --- Public API ---

    /** Map input DTO to entity (no calculations here). */
    public PromotedFoodItem toEntity(PromotedFoodItemInputDTO dto, FoodItem foodItem) {
        PromotedFoodItem entity = new PromotedFoodItem();
        entity.setFoodItem(Objects.requireNonNull(foodItem, "foodItem must not be null"));
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setPromoPrice(dto.getPromoPrice());
        entity.setSalePercentage(dto.getSalePercentage());
        entity.setSaleDescription(trimToNull(dto.getSaleDescription()));
        return entity;
    }

    /** Map entity to output DTO, including calculated effectivePrice and pricePer100g. */
    public PromotedFoodItemDTO toDTO(PromotedFoodItem entity) {
        FoodItem fi = entity.getFoodItem();

        // Calculate effectivePrice using promoPrice OR salePercentage
        BigDecimal effectivePrice = computeEffectivePrice(
                fi != null ? fi.getPrice() : null,
                entity.getPromoPrice(),
                entity.getSalePercentage()
        );

        // Calculate price per 100g if we have both grams and effectivePrice
        BigDecimal pricePer100g = computePricePer100g(
                effectivePrice,
                fi != null ? fi.getGrams() : null
        );

        return new PromotedFoodItemDTO(
                entity.getId(),
                fi != null ? fi.getId() : null,
                fi != null ? fi.getName() : null,
                entity.getStartDate(),
                entity.getEndDate(),
                effectivePrice,                 // calculated
                entity.getPromoPrice(),         // raw promo price (optional)
                entity.getSalePercentage(),     // raw sale % (optional)
                entity.getSaleDescription(),    // optional label
                pricePer100g                    // calculated
        );
    }

    // --- Helpers ---

    /**
     * Compute effective price:
     * - If promoPrice != null, use it
     * - Else if salePercentage != null and basePrice != null, apply percentage
     * - Else return null
     */
    private BigDecimal computeEffectivePrice(BigDecimal basePrice,
                                             BigDecimal promoPrice,
                                             Integer salePercentage) {
        if (promoPrice != null) {
            return scaleMoney(promoPrice);
        }
        if (basePrice == null || salePercentage == null) {
            return null;
        }
        // effective = basePrice * (100 - pct) / 100
        BigDecimal pctLeft = BigDecimal.valueOf(100 - salePercentage);
        return scaleMoney(basePrice.multiply(pctLeft).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
    }

    /**
     * Compute price per 100g using effectivePrice and grams:
     * pricePer100g = effectivePrice * 100 / grams
     */
    private BigDecimal computePricePer100g(BigDecimal effectivePrice, BigDecimal grams) {
        if (effectivePrice == null || grams == null || grams.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        return effectivePrice
                .multiply(BigDecimal.valueOf(100))
                .divide(grams, 2, RoundingMode.HALF_UP);
    }

    /** Normalize money values to scale(2). */
    private BigDecimal scaleMoney(BigDecimal v) {
        return v == null ? null : v.setScale(2, RoundingMode.HALF_UP);
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}

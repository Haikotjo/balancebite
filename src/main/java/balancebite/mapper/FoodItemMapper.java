package balancebite.mapper;

import balancebite.dto.NutrientInfoDTO;
import balancebite.dto.fooditem.FoodItemDTO;
import balancebite.dto.fooditem.FoodItemInputDTO;
import balancebite.model.NutrientInfo;
import balancebite.model.foodItem.FoodItem;
import balancebite.model.foodItem.PromotedFoodItem;
import balancebite.service.fooditem.PromotedFoodItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Maps between FoodItem entities and DTOs.
 *
 * IMPORTANT:
 * - This mapper NEVER uploads files. It only copies fields.
 * - If a MultipartFile was provided on the DTO (dto.getImageFile()),
 *   your Service layer must upload it (e.g., Cloudinary via ImageHandlerService)
 *   and set entity.setImageUrl(...) BEFORE saving the entity.
 * - Direct imageUrl and base64 image are passed through if present.
 */
@Component
public class FoodItemMapper {

    private static final Logger log = LoggerFactory.getLogger(FoodItemMapper.class);

    private final PromotedFoodItemService promotedFoodItemService;

    public FoodItemMapper(PromotedFoodItemService promotedFoodItemService) {
        this.promotedFoodItemService = promotedFoodItemService;
    }

    /**
     * Convert FoodItemInputDTO to FoodItem entity.
     * - Copies basic fields and nutrients.
     * - Image precedence (NO upload here):
     *   1) imageUrl (copied as-is)
     *   2) image (base64) if you still support it
     *   3) imageFile is intentionally ignored here; service must handle upload.
     */
    public FoodItem toEntity(FoodItemInputDTO dto) {
        log.info("Converting FoodItemInputDTO to FoodItem entity.");
        if (dto == null) {
            log.warn("Received null FoodItemInputDTO, returning null for FoodItem.");
            return null;
        }

        FoodItem foodItem = new FoodItem(
                dto.getName(),
                dto.getFdcId(),
                dto.getPortionDescription(),
                dto.getGramWeight(),
                dto.getSource(),
                dto.getFoodSource()
        );

        // Nutrients: keep only entries with a value
        List<NutrientInfo> nutrients =
                dto.getNutrients() == null
                        ? Collections.emptyList()
                        : dto.getNutrients().stream()
                        .filter(n -> n.getValue() != null)
                        .map(n -> new NutrientInfo(
                                n.getNutrientName(),
                                n.getValue(),
                                n.getUnitName(),
                                n.getNutrientId()))
                        .collect(Collectors.toList());
        foodItem.setNutrients(nutrients);

        foodItem.setFoodCategory(dto.getFoodCategory());

        // ---- Image handling (pass-through, no upload) ----
        if (hasText(dto.getImageUrl())) {
            foodItem.setImageUrl(dto.getImageUrl());
        }
        if (dto.getImage() != null) {
            foodItem.setImage(dto.getImage()); // base64 if still supported
        }
        // dto.getImageFile() is intentionally ignored here.
        // --------------------------------------------------

        // Pricing & extras
        foodItem.setPrice(dto.getPrice());
        foodItem.setGrams(dto.getGrams());
        foodItem.setStoreBrand(dto.getStoreBrand());

        log.debug("Finished mapping FoodItemInputDTO to FoodItem entity: {}", foodItem);
        return foodItem;
    }

    /**
     * Convert FoodItem entity to FoodItemDTO.
     * - Adds promo flags (promoted + start/end).
     * - Computes pricePer100g = price * 100 / grams (2 decimals, HALF_UP) when possible.
     * - Passes through raw promo fields + calculated effectivePrice.
     */
    public FoodItemDTO toDTO(FoodItem foodItem) {
        log.info("Converting FoodItem entity to FoodItemDTO.");
        if (foodItem == null) {
            log.warn("Received null FoodItem entity, returning null for FoodItemDTO.");
            return null;
        }

        // Active promotion window for this item (if any)
        Optional<PromotedFoodItem> promotion = promotedFoodItemService.getActivePromotion(foodItem.getId());
        boolean promoted = promotion.isPresent();
        LocalDateTime startDate = promotion.map(PromotedFoodItem::getStartDate).orElse(null);
        LocalDateTime endDate   = promotion.map(PromotedFoodItem::getEndDate).orElse(null);

        // Compute price per 100g when both price and grams are valid
        BigDecimal pricePer100g = null;
        if (foodItem.getPrice() != null
                && foodItem.getGrams() != null
                && foodItem.getGrams().compareTo(BigDecimal.ZERO) > 0) {
            pricePer100g = foodItem.getPrice()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(foodItem.getGrams(), 2, RoundingMode.HALF_UP);
        }

        // Nutrients -> DTOs
        List<NutrientInfoDTO> nutrientDTOs =
                foodItem.getNutrients() == null
                        ? Collections.emptyList()
                        : foodItem.getNutrients().stream()
                        .map(n -> new NutrientInfoDTO(
                                n.getNutrientName(),
                                n.getValue(),
                                n.getUnitName(),
                                n.getNutrientId()))
                        .collect(Collectors.toList());

        // Raw promo fields (pass-through so UI can show them regardless of base price)
        BigDecimal promoPrice = promotion.map(PromotedFoodItem::getPromoPrice).orElse(null);
        Integer salePct       = promotion.map(PromotedFoodItem::getSalePercentage).orElse(null);
        String saleDesc       = promotion.map(PromotedFoodItem::getSaleDescription).orElse(null);

        // Calculated effective price (nullable if inputs insufficient)
        BigDecimal effectivePrice = computeEffectivePrice(foodItem.getPrice(), promoPrice, salePct);

        // Build DTO
        FoodItemDTO dto = new FoodItemDTO(
                foodItem.getId(),
                foodItem.getName(),
                foodItem.getFdcId(),
                nutrientDTOs,
                foodItem.getPortionDescription(),
                foodItem.getGramWeight(),
                foodItem.getSource(),
                foodItem.getFoodSource(),
                promoted,
                startDate,
                endDate,
                foodItem.getFoodCategory(),
                foodItem.getImage(),
                foodItem.getImageUrl(),
                foodItem.getPrice(),
                foodItem.getGrams(),
                pricePer100g,
                foodItem.getStoreBrand(),
                // NEW promo fields for UI
                promoPrice,
                salePct,
                saleDesc,
                effectivePrice
        );

        log.debug("Finished mapping FoodItem entity to FoodItemDTO: {}", dto);
        return dto;
    }

    // -------- Helpers --------
    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

    /**
     * Compute effective price:
     * - If promoPrice != null, use it.
     * - Else if basePrice != null and salePercentage != null, apply percentage.
     * - Else return null.
     */
    private static BigDecimal computeEffectivePrice(BigDecimal basePrice,
                                                    BigDecimal promoPrice,
                                                    Integer salePercentage) {
        if (promoPrice != null) {
            return promoPrice.setScale(2, RoundingMode.HALF_UP);
        }
        if (basePrice == null || salePercentage == null) {
            return null;
        }
        BigDecimal pctLeft = BigDecimal.valueOf(100 - salePercentage);
        return basePrice.multiply(pctLeft)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}

package balancebite.dto.fooditem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Output DTO for a promoted FoodItem.
 * Immutable: all fields are final, no setters.
 */
public class PromotedFoodItemDTO {

    private final Long id;
    private final Long foodItemId;
    private final String foodItemName;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final BigDecimal effectivePrice;
    private final BigDecimal promoPrice;
    private final Integer salePercentage;
    private final String saleDescription;
    private final BigDecimal pricePer100g;

    // All-args constructor (required for final fields)
    public PromotedFoodItemDTO(Long id,
                               Long foodItemId,
                               String foodItemName,
                               LocalDateTime startDate,
                               LocalDateTime endDate,
                               BigDecimal effectivePrice,
                               BigDecimal promoPrice,
                               Integer salePercentage,
                               String saleDescription,
                               BigDecimal pricePer100g) {
        this.id = id;
        this.foodItemId = foodItemId;
        this.foodItemName = foodItemName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.effectivePrice = effectivePrice;
        this.promoPrice = promoPrice;
        this.salePercentage = salePercentage;
        this.saleDescription = saleDescription;
        this.pricePer100g = pricePer100g;
    }

    // Getters only
    public Long getId() { return id; }
    public Long getFoodItemId() { return foodItemId; }
    public String getFoodItemName() { return foodItemName; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public BigDecimal getEffectivePrice() { return effectivePrice; }
    public BigDecimal getPromoPrice() { return promoPrice; }
    public Integer getSalePercentage() { return salePercentage; }
    public String getSaleDescription() { return saleDescription; }
    public BigDecimal getPricePer100g() { return pricePer100g; }
}

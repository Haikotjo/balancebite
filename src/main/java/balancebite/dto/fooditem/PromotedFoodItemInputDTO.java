package balancebite.dto.fooditem;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Input DTO for creating/updating a promoted FoodItem. */
public class PromotedFoodItemInputDTO {

    @NotNull(message = "foodItemId is required.")
    private Long foodItemId;

    @NotNull(message = "startDate is required.")
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    /** Fixed promo price (optional). */
    @Digits(integer = 12, fraction = 2)
    @DecimalMin(value = "0.00", message = "promoPrice must be ≥ 0.00")
    private BigDecimal promoPrice;

    /** Whole-number discount percent 0..100 (optional). */
    @Min(value = 0, message = "salePercentage must be ≥ 0")
    @Max(value = 100, message = "salePercentage must be ≤ 100")
    private Integer salePercentage;

    /** Optional label shown in UI. */
    @Size(max = 200, message = "saleDescription must be ≤ 200 chars")
    private String saleDescription;

    public PromotedFoodItemInputDTO() {} // needed by Spring

    // getters/setters
    public Long getFoodItemId() { return foodItemId; }
    public void setFoodItemId(Long foodItemId) { this.foodItemId = foodItemId; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public BigDecimal getPromoPrice() { return promoPrice; }
    public void setPromoPrice(BigDecimal promoPrice) { this.promoPrice = promoPrice; }
    public Integer getSalePercentage() { return salePercentage; }
    public void setSalePercentage(Integer salePercentage) { this.salePercentage = salePercentage; }
    public String getSaleDescription() { return saleDescription; }
    public void setSaleDescription(String saleDescription) { this.saleDescription = saleDescription; }

    // Bean-level checks
    @AssertTrue(message = "startDate must be before endDate.")
    public boolean isStartBeforeEnd() {
        if (startDate == null || endDate == null) return true;
        return startDate.isBefore(endDate);
    }

    @AssertTrue(message = "Provide either promoPrice OR salePercentage (exactly one).")
    public boolean isExactlyOneDiscountMechanism() {
        return (promoPrice != null) ^ (salePercentage != null);
    }
}

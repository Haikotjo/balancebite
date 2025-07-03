package balancebite.dto.fooditem;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class PromotedFoodItemInputDTO {
    @NotNull(message = "foodItemId is required")
    private Long foodItemId;

    @NotNull(message = "startDate is required")
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    // Getters and setters

    public Long getFoodItemId() {
        return foodItemId;
    }

    public void setFoodItemId(Long foodItemId) {
        this.foodItemId = foodItemId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}

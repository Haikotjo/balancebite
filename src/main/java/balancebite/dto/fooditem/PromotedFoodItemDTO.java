package balancebite.dto.fooditem;

import java.time.LocalDateTime;

public class PromotedFoodItemDTO {

    private Long id;
    private Long foodItemId;
    private String foodItemName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // Constructors
    public PromotedFoodItemDTO() {}

    public PromotedFoodItemDTO(Long id, Long foodItemId, String foodItemName, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.foodItemId = foodItemId;
        this.foodItemName = foodItemName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFoodItemId() {
        return foodItemId;
    }

    public void setFoodItemId(Long foodItemId) {
        this.foodItemId = foodItemId;
    }

    public String getFoodItemName() {
        return foodItemName;
    }

    public void setFoodItemName(String foodItemName) {
        this.foodItemName = foodItemName;
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

package balancebite.dto;

public class MealIngredientInputDTO {

    private Long foodItemId;
    private double quantity;

    // Constructors
    public MealIngredientInputDTO() {}

    public MealIngredientInputDTO(Long foodItemId, double quantity) {
        this.foodItemId = foodItemId;
        this.quantity = quantity;
    }

    // Getters
    public Long getFoodItemId() {
        return foodItemId;
    }

    public double getQuantity() {
        return quantity;
    }
}

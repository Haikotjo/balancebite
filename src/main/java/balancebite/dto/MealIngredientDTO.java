package balancebite.dto;

public class MealIngredientDTO {

    private Long id;
    private Long mealId;
    private Long foodItemId;
    private double quantity;

    // Constructors
    public MealIngredientDTO() {}

    public MealIngredientDTO(Long id, Long mealId, Long foodItemId, double quantity) {
        this.id = id;
        this.mealId = mealId;
        this.foodItemId = foodItemId;
        this.quantity = quantity;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getMealId() {
        return mealId;
    }

    public Long getFoodItemId() {
        return foodItemId;
    }

    public double getQuantity() {
        return quantity;
    }
}

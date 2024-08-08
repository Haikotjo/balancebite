package balancebite.dto;

import java.util.List;

public class MealInputDTO {

    private String name;
    private List<MealIngredientInputDTO> mealIngredients;

    // Constructors
    public MealInputDTO() {}

    public MealInputDTO(String name, List<MealIngredientInputDTO> mealIngredients) {
        this.name = name;
        this.mealIngredients = mealIngredients;
    }

    // Getters
    public String getName() {
        return name;
    }

    public List<MealIngredientInputDTO> getMealIngredients() {
        return mealIngredients;
    }
}

package balancebite.dto;

import java.util.List;

public class MealDTO {

    private Long id;
    private String name;
    private List<MealIngredientDTO> mealIngredients;

    // Constructors
    public MealDTO() {}

    public MealDTO(Long id, String name, List<MealIngredientDTO> mealIngredients) {
        this.id = id;
        this.name = name;
        this.mealIngredients = mealIngredients;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<MealIngredientDTO> getMealIngredients() {
        return mealIngredients;
    }
}

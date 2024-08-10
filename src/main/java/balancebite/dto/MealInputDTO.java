package balancebite.dto;

import java.util.List;

public class MealInputDTO {

    private String name;
    private List<MealIngredientInputDTO> mealIngredients;

    // Macronutriënten die mogelijk via input worden meegegeven
    private double proteins;
    private double carbohydrates;
    private double fats;
    private double kcals;

    // Constructors
    public MealInputDTO() {}

    public MealInputDTO(String name, List<MealIngredientInputDTO> mealIngredients) {
        this.name = name;
        this.mealIngredients = mealIngredients;
    }

    public MealInputDTO(String name, List<MealIngredientInputDTO> mealIngredients, double proteins, double carbohydrates, double fats, double kcals) {
        this.name = name;
        this.mealIngredients = mealIngredients;
        this.proteins = proteins;
        this.carbohydrates = carbohydrates;
        this.fats = fats;
        this.kcals = kcals;
    }

    // Getters
    public String getName() {
        return name;
    }

    public List<MealIngredientInputDTO> getMealIngredients() {
        return mealIngredients;
    }

    public double getProteins() {
        return proteins;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public double getFats() {
        return fats;
    }

    public double getKcals() {
        return kcals;
    }
}

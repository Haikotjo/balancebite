package balancebite.dto;

import java.util.List;

public class MealDTO {

    private Long id;
    private String name;
    private List<MealIngredientDTO> mealIngredients;

    // Macronutriënten voor de response
    private double proteins;
    private double carbohydrates;
    private double fats;
    private double kcals;

    // Constructors
    public MealDTO() {}

    public MealDTO(Long id, String name, List<MealIngredientDTO> mealIngredients) {
        this.id = id;
        this.name = name;
        this.mealIngredients = mealIngredients;
    }

    public MealDTO(Long id, String name, List<MealIngredientDTO> mealIngredients, double proteins, double carbohydrates, double fats, double kcals) {
        this.id = id;
        this.name = name;
        this.mealIngredients = mealIngredients;
        this.proteins = proteins;
        this.carbohydrates = carbohydrates;
        this.fats = fats;
        this.kcals = kcals;
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

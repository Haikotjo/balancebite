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

    // Vitaminen en Mineralen
    private VitaminsAndMineralsDTO vitaminsAndMinerals;

    // Constructors
    public MealInputDTO() {}

    public MealInputDTO(String name, List<MealIngredientInputDTO> mealIngredients) {
        this.name = name;
        this.mealIngredients = mealIngredients;
    }

    public MealInputDTO(String name, List<MealIngredientInputDTO> mealIngredients, double proteins, double carbohydrates, double fats, double kcals, VitaminsAndMineralsDTO vitaminsAndMinerals) {
        this.name = name;
        this.mealIngredients = mealIngredients;
        this.proteins = proteins;
        this.carbohydrates = carbohydrates;
        this.fats = fats;
        this.kcals = kcals;
        this.vitaminsAndMinerals = vitaminsAndMinerals;
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

    public VitaminsAndMineralsDTO getVitaminsAndMinerals() {
        return vitaminsAndMinerals;
    }

    // Setters (indien nodig)
    public void setName(String name) {
        this.name = name;
    }

    public void setMealIngredients(List<MealIngredientInputDTO> mealIngredients) {
        this.mealIngredients = mealIngredients;
    }

    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public void setKcals(double kcals) {
        this.kcals = kcals;
    }

    public void setVitaminsAndMinerals(VitaminsAndMineralsDTO vitaminsAndMinerals) {
        this.vitaminsAndMinerals = vitaminsAndMinerals;
    }
}

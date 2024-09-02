package balancebite.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) for capturing input data related to a Meal.
 * This class is used for receiving data from the client-side when creating or updating
 * a Meal in the application.
 */
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

    /**
     * Default constructor for MealInputDTO.
     */
    public MealInputDTO() {}

    /**
     * Constructor for creating a MealInputDTO with basic meal information.
     *
     * @param name the name of the meal.
     * @param mealIngredients the list of ingredients that make up the meal.
     */
    public MealInputDTO(String name, List<MealIngredientInputDTO> mealIngredients) {
        this.name = name;
        this.mealIngredients = mealIngredients;
    }

    /**
     * Constructor for creating a MealInputDTO with detailed nutritional information.
     *
     * @param name the name of the meal.
     * @param mealIngredients the list of ingredients that make up the meal.
     * @param proteins the total amount of proteins in the meal.
     * @param carbohydrates the total amount of carbohydrates in the meal.
     * @param fats the total amount of fats in the meal.
     * @param kcals the total amount of calories in the meal.
     * @param vitaminsAndMinerals the vitamins and minerals present in the meal.
     */
    public MealInputDTO(String name, List<MealIngredientInputDTO> mealIngredients, double proteins, double carbohydrates, double fats, double kcals, VitaminsAndMineralsDTO vitaminsAndMinerals) {
        this.name = name;
        this.mealIngredients = mealIngredients;
        this.proteins = proteins;
        this.carbohydrates = carbohydrates;
        this.fats = fats;
        this.kcals = kcals;
        this.vitaminsAndMinerals = vitaminsAndMinerals;
    }

    /**
     * Gets the name of the meal.
     *
     * @return the name of the meal.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the meal.
     *
     * @param name the name of the meal to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of ingredients that make up the meal.
     *
     * @return the list of meal ingredients.
     */
    public List<MealIngredientInputDTO> getMealIngredients() {
        return mealIngredients;
    }

    /**
     * Sets the list of ingredients that make up the meal.
     *
     * @param mealIngredients the list of meal ingredients to set.
     */
    public void setMealIngredients(List<MealIngredientInputDTO> mealIngredients) {
        this.mealIngredients = mealIngredients;
    }

    /**
     * Gets the total amount of proteins in the meal.
     *
     * @return the total amount of proteins in the meal.
     */
    public double getProteins() {
        return proteins;
    }

    /**
     * Sets the total amount of proteins in the meal.
     *
     * @param proteins the total amount of proteins to set.
     */
    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    /**
     * Gets the total amount of carbohydrates in the meal.
     *
     * @return the total amount of carbohydrates in the meal.
     */
    public double getCarbohydrates() {
        return carbohydrates;
    }

    /**
     * Sets the total amount of carbohydrates in the meal.
     *
     * @param carbohydrates the total amount of carbohydrates to set.
     */
    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    /**
     * Gets the total amount of fats in the meal.
     *
     * @return the total amount of fats in the meal.
     */
    public double getFats() {
        return fats;
    }

    /**
     * Sets the total amount of fats in the meal.
     *
     * @param fats the total amount of fats to set.
     */
    public void setFats(double fats) {
        this.fats = fats;
    }

    /**
     * Gets the total amount of calories in the meal.
     *
     * @return the total amount of calories in the meal.
     */
    public double getKcals() {
        return kcals;
    }

    /**
     * Sets the total amount of calories in the meal.
     *
     * @param kcals the total amount of calories to set.
     */
    public void setKcals(double kcals) {
        this.kcals = kcals;
    }

    /**
     * Gets the vitamins and minerals present in the meal.
     *
     * @return the vitamins and minerals in the meal.
     */
    public VitaminsAndMineralsDTO getVitaminsAndMinerals() {
        return vitaminsAndMinerals;
    }

    /**
     * Sets the vitamins and minerals present in the meal.
     *
     * @param vitaminsAndMinerals the vitamins and minerals to set in the meal.
     */
    public void setVitaminsAndMinerals(VitaminsAndMineralsDTO vitaminsAndMinerals) {
        this.vitaminsAndMinerals = vitaminsAndMinerals;
    }
}

package balancebite.dto;

import java.util.List;

/**
 * Data Transfer Object (DTO) for transferring Meal data between layers of the application.
 * This class contains the essential fields required for the response when a meal is created
 * or retrieved, along with a message field for success notification.
 */
public class MealDTO {

    private Long id;
    private String name;
    private List<MealIngredientDTO> mealIngredients;

    // Macronutrients for the response
    private double proteins;
    private double carbohydrates;
    private double fats;
    private double kcals;

    // Vitamins and Minerals for the response
    private VitaminsAndMineralsDTO vitaminsAndMinerals;

    // Message for success or additional information
    private String message;

    /**
     * Default constructor for MealDTO.
     */
    public MealDTO() {}

    /**
     * Constructor for creating a MealDTO with basic meal information.
     *
     * @param id              the ID of the meal.
     * @param name            the name of the meal.
     * @param mealIngredients the list of ingredients in the meal.
     */
    public MealDTO(Long id, String name, List<MealIngredientDTO> mealIngredients) {
        this.id = id;
        this.name = name;
        this.mealIngredients = mealIngredients;
    }

    /**
     * Constructor for creating a MealDTO with macronutrients and vitamins/minerals information.
     *
     * @param id                the ID of the meal.
     * @param name              the name of the meal.
     * @param mealIngredients   the list of ingredients in the meal.
     * @param proteins          the amount of proteins in the meal.
     * @param carbohydrates     the amount of carbohydrates in the meal.
     * @param fats              the amount of fats in the meal.
     * @param kcals             the amount of kilocalories in the meal.
     * @param vitaminsAndMinerals the vitamins and minerals data for the meal.
     * @param message           the message indicating the status of meal creation.
     */
    public MealDTO(Long id, String name, List<MealIngredientDTO> mealIngredients, double proteins, double carbohydrates, double fats, double kcals, VitaminsAndMineralsDTO vitaminsAndMinerals, String message) {
        this.id = id;
        this.name = name;
        this.mealIngredients = mealIngredients;
        this.proteins = proteins;
        this.carbohydrates = carbohydrates;
        this.fats = fats;
        this.kcals = kcals;
        this.vitaminsAndMinerals = vitaminsAndMinerals;
        this.message = message;
    }

    // Getters

    /**
     * Gets the ID of the meal.
     *
     * @return the ID of the meal.
     */
    public Long getId() {
        return id;
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
     * Gets the list of ingredients in the meal.
     *
     * @return the list of MealIngredientDTO.
     */
    public List<MealIngredientDTO> getMealIngredients() {
        return mealIngredients;
    }

    /**
     * Gets the total amount of proteins in the meal.
     *
     * @return the amount of proteins in grams.
     */
    public double getProteins() {
        return proteins;
    }

    /**
     * Gets the total amount of carbohydrates in the meal.
     *
     * @return the amount of carbohydrates in grams.
     */
    public double getCarbohydrates() {
        return carbohydrates;
    }

    /**
     * Gets the total amount of fats in the meal.
     *
     * @return the amount of fats in grams.
     */
    public double getFats() {
        return fats;
    }

    /**
     * Gets the total amount of kilocalories in the meal.
     *
     * @return the amount of kilocalories.
     */
    public double getKcals() {
        return kcals;
    }

    /**
     * Gets the vitamins and minerals data of the meal.
     *
     * @return the vitamins and minerals data as a VitaminsAndMineralsDTO.
     */
    public VitaminsAndMineralsDTO getVitaminsAndMinerals() {
        return vitaminsAndMinerals;
    }

    /**
     * Gets the message for the meal response.
     *
     * @return the message indicating the success or status of the meal creation.
     */
    public String getMessage() {
        return message;
    }

    // Setters

    /**
     * Sets the ID of the meal.
     *
     * @param id the ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the name of the meal.
     *
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the list of ingredients for the meal.
     *
     * @param mealIngredients the list of ingredients to set.
     */
    public void setMealIngredients(List<MealIngredientDTO> mealIngredients) {
        this.mealIngredients = mealIngredients;
    }

    /**
     * Sets the amount of proteins in the meal.
     *
     * @param proteins the amount of proteins to set.
     */
    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    /**
     * Sets the amount of carbohydrates in the meal.
     *
     * @param carbohydrates the amount of carbohydrates to set.
     */
    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    /**
     * Sets the amount of fats in the meal.
     *
     * @param fats the amount of fats to set.
     */
    public void setFats(double fats) {
        this.fats = fats;
    }

    /**
     * Sets the amount of kilocalories in the meal.
     *
     * @param kcals the amount of kilocalories to set.
     */
    public void setKcals(double kcals) {
        this.kcals = kcals;
    }

    /**
     * Sets the vitamins and minerals data for the meal.
     *
     * @param vitaminsAndMinerals the vitamins and minerals data to set.
     */
    public void setVitaminsAndMinerals(VitaminsAndMineralsDTO vitaminsAndMinerals) {
        this.vitaminsAndMinerals = vitaminsAndMinerals;
    }

    /**
     * Sets the message for the meal response.
     *
     * @param message the message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}

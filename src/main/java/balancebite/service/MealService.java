package balancebite.service;

import balancebite.dto.MealDTO;
import balancebite.dto.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.mapper.MealMapper;
import balancebite.model.FoodItem;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.VitaminsAndMinerals;
import balancebite.repository.FoodItemRepository;
import balancebite.repository.MealRepository;
import balancebite.utils.NutrientCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for managing Meal entities.
 */
@Service
public class MealService {

    private final MealRepository mealRepository;
    private final FoodItemRepository foodItemRepository;
    private final MealMapper mealMapper;

    /**
     * Constructor for MealService, using constructor injection.
     *
     * @param mealRepository the repository for managing Meal entities.
     * @param foodItemRepository the repository for managing FoodItem entities.
     * @param mealMapper the mapper for converting Meal entities to DTOs.
     */
    public MealService(MealRepository mealRepository, FoodItemRepository foodItemRepository, MealMapper mealMapper) {
        this.mealRepository = mealRepository;
        this.foodItemRepository = foodItemRepository;
        this.mealMapper = mealMapper;
    }

    /**
     * Creates a new Meal entity from the provided MealInputDTO.
     *
     * @param mealInputDTO the DTO containing the input data for creating a Meal.
     * @return the created MealDTO with a success message.
     */
    @Transactional
    public MealDTO createMeal(MealInputDTO mealInputDTO) {
        Meal meal = new Meal();
        meal.setName(mealInputDTO.getName());

        // Stel de mealIngredients in
        List<MealIngredient> mealIngredients = mealInputDTO.getMealIngredients().stream().map(inputDTO -> {
            // Zoek het FoodItem op basis van foodItemId
            FoodItem foodItem = foodItemRepository.findById(inputDTO.getFoodItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid food item ID: " + inputDTO.getFoodItemId()));

            double quantity;
            if (inputDTO.getQuantity() == null || inputDTO.getQuantity() == 0.0) {
                // Gebruik de standaardportie (gramWeight) van FoodItem als de quantity null of 0 is
                quantity = foodItem.getGramWeight();
            } else {
                // Gebruik de opgegeven hoeveelheid (quantity) als die groter is dan 0
                quantity = inputDTO.getQuantity();
            }

            // Maak een nieuwe MealIngredient aan
            return new MealIngredient(meal, foodItem, quantity);

        }).collect(Collectors.toList());

        // Add the mealIngredients to the meal
        meal.addMealIngredients(mealIngredients);

        // Calculate the nutrients using the NutrientCalculator
        Map<String, NutrientInfoDTO> nutrients = NutrientCalculator.calculateNutrients(meal);

        // Set the calculated macronutrients on the Meal object
        meal.setProteins(nutrients.getOrDefault("Proteins (g)", new NutrientInfoDTO()).getValue());
        meal.setCarbohydrates(nutrients.getOrDefault("Carbohydrates (g)", new NutrientInfoDTO()).getValue());
        meal.setFats(nutrients.getOrDefault("Fats (g)", new NutrientInfoDTO()).getValue());
        meal.setKcals(nutrients.getOrDefault("Energy (kcal)", new NutrientInfoDTO()).getValue());

        // Set the calculated vitamins and minerals on the Meal object
        VitaminsAndMinerals vitaminsAndMinerals = new VitaminsAndMinerals();
        vitaminsAndMinerals.setVitaminA(nutrients.getOrDefault("Vitamin A (µg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setVitaminC(nutrients.getOrDefault("Vitamin C (mg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setVitaminD(nutrients.getOrDefault("Vitamin D (IU)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setVitaminE(nutrients.getOrDefault("Vitamin E (mg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setVitaminK(nutrients.getOrDefault("Vitamin K (µg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setThiamin(nutrients.getOrDefault("Thiamin (mg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setRiboflavin(nutrients.getOrDefault("Riboflavin (mg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setNiacin(nutrients.getOrDefault("Niacin (mg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setVitaminB6(nutrients.getOrDefault("Vitamin B6 (mg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setFolate(nutrients.getOrDefault("Folate (µg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setVitaminB12(nutrients.getOrDefault("Vitamin B12 (µg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setPantothenicAcid(nutrients.getOrDefault("Pantothenic Acid (mg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setBiotin(nutrients.getOrDefault("Biotin (µg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setCholine(nutrients.getOrDefault("Choline (mg)", new NutrientInfoDTO()).getValue());

        vitaminsAndMinerals.setCalcium(nutrients.getOrDefault("Calcium (mg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setIron(nutrients.getOrDefault("Iron (mg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setMagnesium(nutrients.getOrDefault("Magnesium (mg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setPhosphorus(nutrients.getOrDefault("Phosphorus (mg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setPotassium(nutrients.getOrDefault("Potassium (mg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setSodium(nutrients.getOrDefault("Sodium (mg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setZinc(nutrients.getOrDefault("Zinc (mg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setCopper(nutrients.getOrDefault("Copper (mg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setManganese(nutrients.getOrDefault("Manganese (mg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setSelenium(nutrients.getOrDefault("Selenium (µg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setFluoride(nutrients.getOrDefault("Fluoride (mg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setChromium(nutrients.getOrDefault("Chromium (µg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setIodine(nutrients.getOrDefault("Iodine (µg)", new NutrientInfoDTO()).getValue());
        vitaminsAndMinerals.setMolybdenum(nutrients.getOrDefault("Molybdenum (µg)", new NutrientInfoDTO()).getValue());

        // Set the VitaminsAndMinerals object on the Meal
        meal.setVitaminsAndMinerals(vitaminsAndMinerals);

        // Save the meal to the database
        Meal savedMeal = mealRepository.save(meal);

        // Return the MealDTO with the success message
        return mealMapper.toDTO(savedMeal);
    }

    /**
     * Calculates the nutrients for a given Meal by its ID.
     *
     * @param mealId the ID of the Meal.
     * @return a Map of nutrient names to NutrientInfoDTOs.
     */
    public Map<String, NutrientInfoDTO> calculateNutrients(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid meal ID: " + mealId));
        return NutrientCalculator.calculateNutrients(meal);
    }

    /**
     * Retrieves all Meals from the repository.
     *
     * @return a List of all Meal entities.
     */
    public List<Meal> getAllMeals() {
        return mealRepository.findAll();
    }

    /**
     * Retrieves a Meal by its ID.
     *
     * @param id the ID of the Meal.
     * @return the Meal entity.
     */
    public Meal getMealById(Long id) {
        return mealRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid meal ID: " + id));
    }
}

package balancebite.service;

import balancebite.dto.MealDTO;
import balancebite.dto.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.mapper.MealMapper;
import balancebite.model.FoodItem;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.NutrientInfo;
import balancebite.repository.FoodItemRepository;
import balancebite.repository.MealRepository;
import balancebite.utils.NutrientCalculatorUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for managing Meal entities.
 * Handles the creation, retrieval, and processing of Meal entities and their related data.
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
     * This method calculates the nutrients based on the food items and quantities provided.
     *
     * @param mealInputDTO the DTO containing the input data for creating a Meal.
     * @return the created MealDTO with a success message and calculated nutrients.
     */
    @Transactional
    public MealDTO createMeal(MealInputDTO mealInputDTO) {
        Meal meal = new Meal();
        meal.setName(mealInputDTO.getName());

        List<MealIngredient> mealIngredients = mealInputDTO.getMealIngredients().stream().map(inputDTO -> {
            FoodItem foodItem = foodItemRepository.findById(inputDTO.getFoodItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid food item ID: " + inputDTO.getFoodItemId()));
            double quantity = inputDTO.getQuantity() == null || inputDTO.getQuantity() == 0.0
                    ? foodItem.getGramWeight()
                    : inputDTO.getQuantity();
            return new MealIngredient(meal, foodItem, quantity);
        }).toList();

        meal.addMealIngredients(mealIngredients);

        // Save the meal to the database
        Meal savedMeal = mealRepository.save(meal);

        return mealMapper.toDTO(savedMeal);
    }

    /**
     * Retrieves the total nutrients for a given Meal by its ID.
     *
     * @param mealId the ID of the Meal.
     * @return a map of nutrient names and their corresponding total values for the meal.
     */
    public Map<String, NutrientInfoDTO> calculateNutrients(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid meal ID: " + mealId));

        return NutrientCalculatorUtil.calculateTotalNutrients(meal.getMealIngredients());
    }

    /**
     * Retrieves the nutrients per food item for a given Meal by its ID.
     *
     * @param mealId the ID of the Meal.
     * @return a map where the key is the food item ID, and the value is the map of nutrient names and their corresponding total values.
     */
    public Map<Long, Map<String, NutrientInfoDTO>> calculateNutrientsPerFoodItem(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid meal ID: " + mealId));

        return NutrientCalculatorUtil.calculateNutrientsPerFoodItem(meal.getMealIngredients());
    }

    /**
     * Retrieves all Meals from the repository.
     *
     * @return a list of all Meal entities.
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

package balancebite.service;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.mapper.MealMapper;
import balancebite.model.FoodItem;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.repository.FoodItemRepository;
import balancebite.repository.MealRepository;
import balancebite.utils.NutrientCalculatorUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * @param mealRepository     the repository for managing Meal entities.
     * @param foodItemRepository the repository for managing FoodItem entities.
     * @param mealMapper         the mapper for converting Meal entities to DTOs.
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

        return mealMapper.toDTO(savedMeal);  // Use mapper
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
     * Retrieves only the macronutrients (such as proteins, carbohydrates, fats, and energy)
     * for a given Meal by its ID. This includes total fats as well as specific types of fatty acids.
     *
     * @param mealId the ID of the Meal.
     * @return a map of macronutrient names and their corresponding values for the meal,
     * with fats grouped into a separate section.
     */
    public Map<String, Object> getMacronutrients(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid meal ID: " + mealId));

        // Calculate all nutrients for the meal
        Map<String, NutrientInfoDTO> allNutrients = NutrientCalculatorUtil.calculateTotalNutrients(meal.getMealIngredients());

        // Define macronutrient names (energy, protein, carbohydrates, and fats)
        String[] macronutrientNames = {
                "Energy kcal",
                "Protein g",
                "Carbohydrate, by difference g"
        };

        // Define fat-related nutrient names
        String[] fatNames = {
                "Total lipid (fat) g",  // Total fats
                "Fatty acids, total saturated g",  // Saturated fats
                "Fatty acids, total monounsaturated g",  // Monounsaturated fats
                "Fatty acids, total polyunsaturated g"  // Polyunsaturated fats
        };

        // Initialize the map to store the macronutrients
        Map<String, Object> macronutrients = new HashMap<>();

        // Add the main macronutrients (excluding fats)
        for (String macro : macronutrientNames) {
            if (allNutrients.containsKey(macro)) {
                macronutrients.put(macro, allNutrients.get(macro).getValue());
            }
        }

        // Add the fat-related nutrients into a separate section within the response
        Map<String, Double> fatSection = new HashMap<>();
        for (String fat : fatNames) {
            if (allNutrients.containsKey(fat)) {
                fatSection.put(fat, allNutrients.get(fat).getValue());
            }
        }

        // Add the fat section to the macronutrients map
        macronutrients.put("Fat", fatSection);

        return macronutrients;
    }

    /**
     * Retrieves all Meals from the repository.
     *
     * @return a list of MealDTOs.
     */
    public List<MealDTO> getAllMeals() {
        List<Meal> meals = mealRepository.findAll();
        return meals.stream().map(mealMapper::toDTO).toList();  // Use mapper
    }

    /**
     * Retrieves a Meal by its ID.
     *
     * @param id the ID of the Meal.
     * @return the MealDTO.
     */
    public MealDTO getMealById(Long id) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid meal ID: " + id));
        return mealMapper.toDTO(meal);  // Use mapper
    }

    /**
     * Retrieves only the macronutrients (such as proteins, carbohydrates, fats, and energy)
     * for each food item in a given Meal by its ID.
     *
     * @param mealId the ID of the Meal.
     * @return a map where the key is the food item ID, and the value is a map of macronutrient names and their corresponding values.
     */
    public Map<Long, Map<String, Object>> getMacronutrientsPerFoodItem(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid meal ID: " + mealId));

        // Retrieve all nutrients per food item
        Map<Long, Map<String, NutrientInfoDTO>> allNutrientsPerFoodItem = NutrientCalculatorUtil.calculateNutrientsPerFoodItem(meal.getMealIngredients());

        // Define macronutrient names (energy, protein, carbohydrates, and fats)
        String[] macronutrientNames = {
                "Energy kcal",
                "Protein g",
                "Carbohydrate, by difference g"
        };

        // Define fat-related nutrient names
        String[] fatNames = {
                "Total lipid (fat) g",  // Total fats
                "Fatty acids, total saturated g",  // Saturated fats
                "Fatty acids, total monounsaturated g",  // Monounsaturated fats
                "Fatty acids, total polyunsaturated g"  // Polyunsaturated fats
        };

        // Initialize the map to store macronutrients per food item
        Map<Long, Map<String, Object>> macronutrientsPerFoodItem = new HashMap<>();

        // Loop over each food item and filter macronutrients
        for (Map.Entry<Long, Map<String, NutrientInfoDTO>> entry : allNutrientsPerFoodItem.entrySet()) {
            Long foodItemId = entry.getKey();
            Map<String, NutrientInfoDTO> nutrients = entry.getValue();

            Map<String, Object> macronutrients = new HashMap<>();
            Map<String, Double> fatSection = new HashMap<>();

            // Filter and add main macronutrients (excluding fats)
            for (String macro : macronutrientNames) {
                if (nutrients.containsKey(macro)) {
                    macronutrients.put(macro, nutrients.get(macro).getValue());
                }
            }

            // Filter and add fat-related nutrients into a separate section
            for (String fat : fatNames) {
                if (nutrients.containsKey(fat)) {
                    fatSection.put(fat, nutrients.get(fat).getValue());
                }
            }

            macronutrients.put("Fat", fatSection);
            macronutrientsPerFoodItem.put(foodItemId, macronutrients);
        }

        return macronutrientsPerFoodItem;
    }

    /**
     * Updates an existing Meal entity with new information.
     * This method updates the meal's name and ingredients based on the provided MealInputDTO.
     * After updating the meal, a success message is generated to indicate the changes.
     *
     * @param id the ID of the meal to be updated
     * @param mealInputDTO the DTO containing the updated meal information
     * @return the updated MealDTO containing the new meal data with a success message
     * @throws RuntimeException if the meal with the given ID is not found
     * @throws IllegalArgumentException if any food item ID in the ingredients is invalid
     */
    @Transactional
    public MealDTO updateMeal(Long id, MealInputDTO mealInputDTO) {
        // Fetch the existing meal by ID
        Meal existingMeal = mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meal not found with id " + id));

        // Update the meal's name
        existingMeal.setName(mealInputDTO.getName());

        // Clear the current meal ingredients so we can update them
        existingMeal.getMealIngredients().clear();

        // Map the updated meal ingredients from the input DTO
        List<MealIngredient> updatedIngredients = mealInputDTO.getMealIngredients().stream().map(inputDTO -> {
            FoodItem foodItem = foodItemRepository.findById(inputDTO.getFoodItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid food item ID: " + inputDTO.getFoodItemId()));
            double quantity = inputDTO.getQuantity() == null || inputDTO.getQuantity() == 0.0
                    ? foodItem.getGramWeight()
                    : inputDTO.getQuantity();
            return new MealIngredient(existingMeal, foodItem, quantity);
        }).toList();

        // Add the new or updated ingredients to the meal
        existingMeal.addMealIngredients(updatedIngredients);

        // Save the updated meal in the database
        Meal savedMeal = mealRepository.save(existingMeal);

        // Convert the updated meal to a DTO and return it with a success message for the update
        return mealMapper.toUpdatedDTO(savedMeal);
    }
}
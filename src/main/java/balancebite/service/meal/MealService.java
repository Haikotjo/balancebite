package balancebite.service.meal;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.mapper.MealIngredientMapper;
import balancebite.mapper.MealMapper;
import balancebite.model.meal.Meal;
import balancebite.repository.FoodItemRepository;
import balancebite.repository.MealRepository;
import balancebite.repository.UserRepository;
import balancebite.service.interfaces.meal.IMealService;
import balancebite.utils.NutrientCalculatorUtil;
import balancebite.utils.CheckForDuplicateTemplateMealUtil;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service class for managing Meal entities.
 * Handles the creation, retrieval, updating, and processing of Meal entities and their related data.
 */
@Service
public class MealService implements IMealService {

    private static final Logger log = LoggerFactory.getLogger(MealService.class);

    private final MealRepository mealRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;
    private final MealMapper mealMapper;
    private final MealIngredientMapper mealIngredientMapper;
    private final CheckForDuplicateTemplateMealUtil checkForDuplicateTemplateMeal;

    /**
     * Constructor for MealService, using constructor injection.
     *
     * @param mealRepository     the repository for managing Meal entities.
     * @param foodItemRepository the repository for managing FoodItem entities.
     * @param userRepository     the repository for managing User entities.
     * @param mealMapper         the mapper for converting Meal entities to DTOs.
     */
    public MealService(MealRepository mealRepository, FoodItemRepository foodItemRepository, UserRepository userRepository, MealMapper mealMapper, MealIngredientMapper mealIngredientMapper, CheckForDuplicateTemplateMealUtil checkForDuplicateTemplateMeal) {
        this.mealRepository = mealRepository;
        this.foodItemRepository = foodItemRepository;
        this.userRepository = userRepository;
        this.mealMapper = mealMapper;
        this.mealIngredientMapper = mealIngredientMapper;
        this.checkForDuplicateTemplateMeal = checkForDuplicateTemplateMeal;
    }

    /**
     * Retrieves all template meals and applies optional sorting.
     *
     * Meals can be sorted based on nutrients (calories, protein, fat, carbs) or
     * based on the first alphabetically sorted food item name in the meal ingredients.
     *
     * @param sortBy The field to sort by ("calories", "protein", "fat", "carbs", or "foodItem").
     * @param sortOrder The sorting order ("asc" for ascending, "desc" for descending).
     * @return A list of MealDTOs representing all template meals, sorted as requested.
     */
    @Override
    @Transactional(readOnly = true)
    public List<MealDTO> getAllMeals(String sortBy, String sortOrder) {
        log.info("Retrieving all template meals with sorting on: {} in {} order.", sortBy, sortOrder);

        // Haal meals op met filtering
        List<Meal> meals = mealRepository.findAllTemplateMeals();

        // Nutrient sorting gebeurt hier (omdat nutrienten berekend moeten worden)
        if ("calories".equalsIgnoreCase(sortBy) ||
                "protein".equalsIgnoreCase(sortBy) ||
                "fat".equalsIgnoreCase(sortBy) ||
                "carbs".equalsIgnoreCase(sortBy)) {

            Map<Long, Double> mealNutrientValues = new HashMap<>();
            for (Meal meal : meals) {
                double nutrientValue = calculateNutrients(meal.getId())
                        .getOrDefault(sortBy, new NutrientInfoDTO(sortBy, 0.0, ""))
                        .getValue();
                mealNutrientValues.put(meal.getId(), nutrientValue);
            }

            meals.sort(Comparator.comparing(meal -> mealNutrientValues.getOrDefault(meal.getId(), 0.0)));

            if ("desc".equalsIgnoreCase(sortOrder)) {
                Collections.reverse(meals);
            }
        }

        // Sorting op food item name (mealIngredients)
        else if ("foodItem".equalsIgnoreCase(sortBy)) {
            meals.sort(Comparator.comparing(meal ->
                    meal.getMealIngredients().stream()
                            .map(mi -> mi.getFoodItem().getName())
                            .sorted()
                            .findFirst()
                            .orElse("")
            ));

            if ("desc".equalsIgnoreCase(sortOrder)) {
                Collections.reverse(meals);
            }
        }

        log.info("Successfully retrieved and sorted {} meals.", meals.size());
        return meals.stream().map(mealMapper::toDTO).toList();
    }

    /**
     * Retrieves a Meal by its ID, only if it is a template.
     *
     * @param id The ID of the Meal.
     * @return The MealDTO.
     * @throws EntityNotFoundException If the meal with the given ID is not found,
     *                                 or if the meal is not a template.
     */
    @Override
    @Transactional(readOnly = true)
    public MealDTO getMealById(Long id) {
        log.info("Attempting to retrieve template meal with ID: {}", id);

        // Fetch the meal from the repository
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + id));

        // Ensure the meal is a template
        if (!meal.isTemplate()) {
            log.warn("Meal with ID {} is not marked as a template.", id);
            throw new EntityNotFoundException("Meal not found or not a template.");
        }

        // Map the Meal entity to a MealDTO
        MealDTO mealDTO = mealMapper.toDTO(meal);

        log.info("Successfully retrieved template meal with ID: {}", id);
        return mealDTO;
    }

    /**
     * Retrieves the total nutrients for a given Meal by its ID.
     *
     * @param mealId the ID of the Meal.
     * @return a map of nutrient names and their corresponding total values for the meal.
     * @throws EntityNotFoundException if the meal with the given ID is not found.
     */
    @Override
    public Map<String, NutrientInfoDTO> calculateNutrients(Long mealId) {
        log.info("Starting total nutrient calculation for meal with ID: {}", mealId);

        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> {
                    log.warn("Meal not found with ID: {}", mealId);
                    return new EntityNotFoundException("Meal not found with ID: " + mealId);
                });

        log.debug("Meal with ID {} contains {} ingredients.", mealId, meal.getMealIngredients().size());
        Map<String, NutrientInfoDTO> totalNutrients = NutrientCalculatorUtil.calculateTotalNutrients(meal.getMealIngredients());
        log.info("Total nutrient calculation completed for meal ID: {}. Total nutrients: {}", mealId, totalNutrients);
        return totalNutrients;
    }

    /**
     * Retrieves the nutrients per food item for a given Meal by its ID.
     *
     * @param mealId the ID of the Meal.
     * @return a map of food item IDs to nutrient maps, where each map contains nutrient names and their values.
     * @throws EntityNotFoundException if the meal with the given ID is not found.
     */
    @Override
    public Map<Long, Map<String, NutrientInfoDTO>> calculateNutrientsPerFoodItem(Long mealId) {
        log.info("Starting nutrient calculation per food item for meal with ID: {}", mealId);

        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> {
                    log.warn("Meal not found with ID: {}", mealId);
                    return new EntityNotFoundException("Meal not found with ID: " + mealId);
                });

        log.debug("Meal with ID {} contains {} ingredients.", mealId, meal.getMealIngredients().size());
        Map<Long, Map<String, NutrientInfoDTO>> nutrientsPerFoodItem =
                NutrientCalculatorUtil.calculateNutrientsPerFoodItem(meal.getMealIngredients());
        log.info("Nutrient calculation per food item completed for meal ID: {}.", mealId);
        return nutrientsPerFoodItem;
    }
}

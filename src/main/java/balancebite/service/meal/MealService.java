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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
     * Retrieves paginated and sorted template meals with optional filtering.
     *
     * Users can filter meals by cuisine, diet, meal type, and food items.
     * Meals can be sorted by name, total calories, protein, fat, or carbs.
     * Results are paginated.
     *
     * @param cuisine Optional filter for meal cuisine.
     * @param diet Optional filter for meal diet.
     * @param mealType Optional filter for meal type (BREAKFAST, LUNCH, etc.).
     * @param foodItems List of food items to filter meals by (e.g., "Banana", "Peas").
     * @param sortBy Sorting field (calories, protein, fat, carbs, name).
     * @param sortOrder Sorting order ("asc" for ascending, "desc" for descending).
     * @param pageable Pageable object for pagination and sorting.
     * @return A paginated and sorted list of MealDTOs that match the filters.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MealDTO> getAllMeals(
            String cuisine,
            String diet,
            String mealType,
            List<String> foodItems,
            String sortBy,
            String sortOrder,
            Pageable pageable
    ) {
        log.info("Retrieving paginated template meals with filters and sorting.");

        // Retrieve all template meals
        List<Meal> meals = mealRepository.findAllTemplateMeals();

        // ✅ **Filtering on cuisine, diet, and mealType**
        if (cuisine != null) {
            meals.removeIf(meal -> !meal.getCuisine().toString().equalsIgnoreCase(cuisine));
        }
        if (diet != null) {
            meals.removeIf(meal -> !meal.getDiet().toString().equalsIgnoreCase(diet));
        }
        if (mealType != null) {
            meals.removeIf(meal -> !meal.getMealType().toString().equalsIgnoreCase(mealType));
        }

        // ✅ **Filtering on food items (must contain at least one of the selected food items)**
        if (foodItems != null && !foodItems.isEmpty()) {
            meals.removeIf(meal -> foodItems.stream().noneMatch(item ->
                    Arrays.asList(meal.getFoodItemsString().split(" \\| ")).contains(item)
            ));
        }

        // ✅ **Sorting**
        Comparator<Meal> comparator = switch (sortBy != null ? sortBy.toLowerCase() : "") {
            case "calories" -> Comparator.comparing(Meal::getTotalCalories);
            case "protein" -> Comparator.comparing(Meal::getTotalProtein);
            case "fat" -> Comparator.comparing(Meal::getTotalFat);
            case "carbs" -> Comparator.comparing(Meal::getTotalCarbs);
            default -> Comparator.comparing(Meal::getName); // Default: sort by name
        };

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }
        meals.sort(comparator);

        // ✅ **Pagination**
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Meal> pagedMeals;

        if (meals.size() < startItem) {
            pagedMeals = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, meals.size());
            pagedMeals = meals.subList(startItem, toIndex);
        }

        log.info("Returning {} meals after filtering, sorting, and pagination.", pagedMeals.size());
        return new PageImpl<>(pagedMeals.stream().map(mealMapper::toDTO).toList(), pageable, meals.size());
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

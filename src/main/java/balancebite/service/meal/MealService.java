package balancebite.service.meal;

import balancebite.dto.fooditem.FoodItemNameDTO;
import balancebite.dto.meal.MealDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.dto.meal.MealNameDTO;
import balancebite.mapper.MealIngredientMapper;
import balancebite.mapper.MealMapper;
import balancebite.model.meal.Meal;
import balancebite.repository.FoodItemRepository;
import balancebite.repository.MealRepository;
import balancebite.repository.UserRepository;
import balancebite.service.diet.PublicDietPlanService;
import balancebite.service.interfaces.meal.IMealService;
import balancebite.utils.NutrientCalculatorUtil;
import balancebite.utils.CheckForDuplicateTemplateMealUtil;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing Meal entities.
 * Handles the creation, retrieval, updating, and processing of Meal entities and their related data.
 */
@Service
public class MealService implements IMealService {

    private static final Logger log = LoggerFactory.getLogger(PublicDietPlanService.class);

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
     * @param cuisines Optional filter for meal cuisine.
     * @param diets Optional filter for meal diet.
     * @param mealTypes Optional filter for meal type (BREAKFAST, LUNCH, etc.).
     * @param foodItems List of food items to filter meals by (e.g., "Banana", "Peas").
     * @param sortBy Sorting field (calories, protein, fat, carbs, name).
     * @param sortOrder Sorting order ("asc" for ascending, "desc" for descending).
     * @param pageable Pageable object for pagination and sorting.
     * @param creatorId
     * @return A paginated and sorted list of MealDTOs that match the filters.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MealDTO> getAllMeals(
            List<String> cuisines,
            List<String> diets,
            List<String> mealTypes,
            List<String> foodItems,
            String sortBy,
            String sortOrder,
            Pageable pageable,
            Long creatorId
    ) {
        log.info("Retrieving paginated template meals with filters and sorting.");

        // Retrieve all template meals
        List<Meal> meals = mealRepository.findAllTemplateMeals();

        meals.removeIf(Meal::isPrivate);

        // ✅ **Filtering on cuisine, diet, and mealType**
        if (cuisines != null && !cuisines.isEmpty()) {
            Set<String> cuisineSet = cuisines.stream().map(String::toUpperCase).collect(Collectors.toSet());
            meals.removeIf(meal -> meal.getCuisines().stream()
                    .map(Enum::name)
                    .noneMatch(cuisineSet::contains));
        }

        // ✅ Filter on creatorId
        if (creatorId != null) {
            meals.removeIf(meal -> meal.getCreatedBy() == null || !meal.getCreatedBy().getId().equals(creatorId));
        }

        if (diets != null && !diets.isEmpty()) {
            Set<String> dietSet = diets.stream().map(String::toUpperCase).collect(Collectors.toSet());
            meals.removeIf(meal -> meal.getDiets().stream()
                    .map(Enum::name)
                    .noneMatch(dietSet::contains));
        }

        if (mealTypes != null && !mealTypes.isEmpty()) {
            Set<String> mealTypeSet = mealTypes.stream().map(String::toUpperCase).collect(Collectors.toSet());
            meals.removeIf(meal -> meal.getMealTypes().stream()
                    .map(Enum::name)
                    .noneMatch(mealTypeSet::contains));
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
            case "savecount" -> Comparator.comparing(Meal::getSaveCount);
            case "weeklysavecount" -> Comparator.comparing(Meal::getWeeklySaveCount);
            case "monthlysavecount" -> Comparator.comparing(Meal::getMonthlySaveCount);
            default -> Comparator.comparing(Meal::getName);
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
     * Retrieves a Meal by its ID.
     *
     * @param id The ID of the Meal.
     * @return The MealDTO.
     * @throws EntityNotFoundException If the meal with the given ID is not found.
     */
    @Override
    @Transactional(readOnly = true)
    public MealDTO getMealById(Long id) {
        log.info("Attempting to retrieve meal with ID: {}", id);

        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + id));

        if (meal.isPrivate()) {
            log.warn("Attempt to access private meal with ID: {}", id);
            throw new AccessDeniedException("This meal is private.");
        }

        MealDTO mealDTO = mealMapper.toDTO(meal);
        log.info("Successfully retrieved meal with ID: {}", id);
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

    /**
     * Retrieves a list of all MealItems, returning only their ID and name.
     *
     * @return A list of MealNameDTOs containing only ID and name.
     */
    @Override
    public List<MealNameDTO> getAllMealNames() {
        log.info("Fetching all food item names and IDs.");
        return mealRepository.findAllMealNames();
    }
}

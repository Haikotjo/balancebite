package balancebite.service.meal;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.dto.meal.MealNameDTO;
import balancebite.mapper.MealIngredientMapper;
import balancebite.mapper.MealMapper;
import balancebite.model.foodItem.FoodSource;
import balancebite.model.meal.Meal;
import balancebite.model.meal.references.Cuisine;
import balancebite.model.meal.references.Diet;
import balancebite.model.meal.references.MealType;
import balancebite.model.user.User;
import balancebite.model.user.UserRole;
import balancebite.repository.FoodItemRepository;
import balancebite.repository.MealRepository;
import balancebite.repository.SharedMealAccessRepository;
import balancebite.repository.UserRepository;
import balancebite.security.SecurityUtils;
import balancebite.service.diet.PublicDietPlanService;
import balancebite.service.interfaces.meal.IMealService;
import balancebite.specification.MealSpecifications;
import balancebite.utils.NutrientCalculatorUtil;
import balancebite.utils.CheckForDuplicateTemplateMealUtil;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import static balancebite.utils.QueryUtils.buildSort;
import static balancebite.utils.QueryUtils.parseEnumList;

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
    private final SharedMealAccessRepository sharedMealAccessRepository;

    /**
     * Constructor for MealService, using constructor injection.
     *
     * @param mealRepository     the repository for managing Meal entities.
     * @param foodItemRepository the repository for managing FoodItem entities.
     * @param userRepository     the repository for managing User entities.
     * @param mealMapper         the mapper for converting Meal entities to DTOs.
     */
    public MealService(MealRepository mealRepository, FoodItemRepository foodItemRepository, UserRepository userRepository, MealMapper mealMapper, MealIngredientMapper mealIngredientMapper, CheckForDuplicateTemplateMealUtil checkForDuplicateTemplateMeal, SharedMealAccessRepository sharedMealAccessRepository) {
        this.mealRepository = mealRepository;
        this.foodItemRepository = foodItemRepository;
        this.userRepository = userRepository;
        this.mealMapper = mealMapper;
        this.mealIngredientMapper = mealIngredientMapper;
        this.checkForDuplicateTemplateMeal = checkForDuplicateTemplateMeal;
        this.sharedMealAccessRepository = sharedMealAccessRepository;
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
            List<String> cuisines, List<String> diets, List<String> mealTypes,
            List<String> foodItems, String sortBy, String sortOrder,
            Pageable pageable, Long creatorId, Double minCalories,
            Double maxCalories, Double minProtein, Double maxProtein,
            Double minCarbs, Double maxCarbs, Double minFat, Double maxFat,
            String foodSource, String currentUsername
    ) {
        User currentUser = null;
        if (currentUsername != null) {
            currentUser = userRepository.findByUserName(currentUsername).orElse(null);
        }
        Long userId = (currentUser != null) ? currentUser.getId() : null;

        Specification<Meal> spec = Specification.where(MealSpecifications.isTemplateMeal())
                .and(MealSpecifications.isVisibleToUser(userId));

        if (foodSource != null && !foodSource.isBlank()) {
            try {
                spec = spec.and(MealSpecifications.hasFoodSource(FoodSource.valueOf(foodSource.toUpperCase())));
            } catch (IllegalArgumentException e) {
                log.warn("Invalid foodSource: {}", foodSource);
            }
        }

        if (creatorId != null) spec = spec.and(MealSpecifications.createdByUser(creatorId));

        List<Cuisine> cuisineEnums = parseEnumList(cuisines, Cuisine.class, "cuisine");
        if (cuisines != null && !cuisines.isEmpty() && cuisineEnums.isEmpty()) return Page.empty(pageable);
        if (!cuisineEnums.isEmpty()) spec = spec.and(MealSpecifications.hasCuisineIn(cuisineEnums));

        List<Diet> dietEnums = parseEnumList(diets, Diet.class, "diet");
        if (diets != null && !diets.isEmpty() && dietEnums.isEmpty()) return Page.empty(pageable);
        if (!dietEnums.isEmpty()) spec = spec.and(MealSpecifications.hasDietIn(dietEnums));

        List<MealType> mealTypeEnums = parseEnumList(mealTypes, MealType.class, "mealType");
        if (mealTypes != null && !mealTypes.isEmpty() && mealTypeEnums.isEmpty()) return Page.empty(pageable);
        if (!mealTypeEnums.isEmpty()) spec = spec.and(MealSpecifications.hasMealTypeIn(mealTypeEnums));

        if (foodItems != null && !foodItems.isEmpty()) spec = spec.and(MealSpecifications.hasAnyFoodItem(foodItems));
        if (minCalories != null) spec = spec.and(MealSpecifications.totalCaloriesMin(minCalories));
        if (maxCalories != null) spec = spec.and(MealSpecifications.totalCaloriesMax(maxCalories));
        if (minProtein != null) spec = spec.and(MealSpecifications.totalProteinMin(minProtein));
        if (maxProtein != null) spec = spec.and(MealSpecifications.totalProteinMax(maxProtein));
        if (minCarbs != null) spec = spec.and(MealSpecifications.totalCarbsMin(minCarbs));
        if (maxCarbs != null) spec = spec.and(MealSpecifications.totalCarbsMax(maxCarbs));
        if (minFat != null) spec = spec.and(MealSpecifications.totalFatMin(minFat));
        if (maxFat != null) spec = spec.and(MealSpecifications.totalFatMax(maxFat));

        Sort sort = buildSort(sortBy, sortOrder, pageable);
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Meal> templateMeals = mealRepository.findAll(spec, sortedPageable);

        // No user -> return templates
        if (userId == null) {
            return templateMeals.map(mealMapper::toDTO);
        }

        // User present -> swap templates with user copies (if exists)
        List<Long> templateIds = templateMeals.getContent().stream().map(Meal::getId).toList();
        if (templateIds.isEmpty()) {
            return templateMeals.map(mealMapper::toDTO);
        }

        List<Meal> userCopies = mealRepository.findUserCopiesForTemplates(userId, templateIds);

        Map<Long, Meal> copyByOriginalId = userCopies.stream()
                .filter(c -> c.getOriginalMealId() != null)
                .collect(Collectors.toMap(Meal::getOriginalMealId, c -> c, (a, b) -> a));

        return templateMeals.map(template -> {
            Meal result = copyByOriginalId.getOrDefault(template.getId(), template);
            return mealMapper.toDTO(result);
        });
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
            User currentUser = getCurrentUserOrThrow();
            boolean isOwner = meal.getCreatedBy().getId().equals(currentUser.getId());
            boolean isSharedByEmail = sharedMealAccessRepository.existsByMealIdAndEmail(id, currentUser.getEmail());
            boolean isSharedByUserId = sharedMealAccessRepository.existsByMealIdAndUserId(id, currentUser.getId());
            boolean isAdmin = currentUser.getRoles().stream()
                    .anyMatch(role -> role.getRolename() == UserRole.ADMIN);

            if (!isOwner && !isSharedByEmail && !isSharedByUserId && !isAdmin) {
                log.warn("Access denied for user {} to private meal ID: {}", currentUser.getId(), id);
                throw new AccessDeniedException("This meal is private.");
            }
        }

        return mealMapper.toDTO(meal);
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

    private User getCurrentUserOrThrow() {
        Long userId = SecurityUtils.getCurrentAuthenticatedUserId();
        if (userId == null) {
            throw new AccessDeniedException("No authenticated user.");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }

}

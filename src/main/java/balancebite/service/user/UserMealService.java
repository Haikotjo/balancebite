package balancebite.service.user;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.DuplicateMealException;
import balancebite.errorHandling.InvalidFoodItemException;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.mapper.MealIngredientMapper;
import balancebite.mapper.MealMapper;
import balancebite.mapper.UserMapper;
import balancebite.model.meal.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.user.User;
import balancebite.repository.MealRepository;
import balancebite.repository.UserRepository;
import balancebite.security.JwtService;
import balancebite.service.FileStorageService;
import balancebite.service.interfaces.user.IUserMealService;
import balancebite.utils.CheckForDuplicateTemplateMealUtil;
import balancebite.utils.UserUpdateHelper;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing the relationship between users and meals.
 * This includes adding and removing meals to/from a user's list of meals.
 */
@Service
@Transactional
public class UserMealService implements IUserMealService {

    private static final Logger log = LoggerFactory.getLogger(UserMealService.class);

    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final UserMapper userMapper;
    private final MealMapper mealMapper;
    private final MealIngredientMapper mealIngredientMapper;
    private final CheckForDuplicateTemplateMealUtil checkForDuplicateTemplateMeal;
    private final UserUpdateHelper userUpdateHelper;
    private final JwtService jwtService;
    private final FileStorageService fileStorageService;

    public UserMealService(UserRepository userRepository, MealRepository mealRepository, UserMapper userMapper,
                           MealMapper mealMapper, MealIngredientMapper mealIngredientMapper,
                           CheckForDuplicateTemplateMealUtil checkForDuplicateTemplateMeal, UserUpdateHelper userUpdateHelper, JwtService jwtService, FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.userMapper = userMapper;
        this.mealMapper = mealMapper;
        this.mealIngredientMapper = mealIngredientMapper;
        this.checkForDuplicateTemplateMeal = checkForDuplicateTemplateMeal;
        this.userUpdateHelper = userUpdateHelper;
        this.jwtService = jwtService;
        this.fileStorageService = fileStorageService;
    }
    /**
     * Creates a new Meal entity for a specific user based on the provided MealInputDTO.
     * Converts the input DTO to a Meal entity, validates the meal, associates it with the user,
     * updates nutrient calculations, persists the entity, and returns the resulting DTO.
     *
     * @param mealInputDTO The DTO containing the input data for creating the Meal.
     * @param userId       The ID of the user to whom the meal will be associated.
     * @return The created MealDTO with the persisted meal information.
     * @throws UserNotFoundException    If the user with the specified ID does not exist.
     * @throws DuplicateMealException   If a template meal with the same ingredients already exists.
     * @throws InvalidFoodItemException If any food item in the meal is invalid.
     */
    @Override
    @Transactional
    public MealDTO createMealForUser(MealInputDTO mealInputDTO, Long userId) {
        log.info("Attempting to create a new meal for user ID: {}", userId);

        // Convert DTO to Meal entity
        Meal meal = mealMapper.toEntity(mealInputDTO);

        // Set meal type, cuisine, and diet if provided
        meal.setMealType(mealInputDTO.getMealType());
        meal.setCuisine(mealInputDTO.getCuisine());
        meal.setDiet(mealInputDTO.getDiet());

        // Process image from the DTO only if not already handled in the mapper
        if (meal.getImageUrl() == null && mealInputDTO.getImageFile() != null && !mealInputDTO.getImageFile().isEmpty()) {
            String imageUrl = fileStorageService.saveFile(mealInputDTO.getImageFile());
            meal.setImageUrl(imageUrl);
        }

        // Validate and check for duplicate ingredients
        List<Long> foodItemIds = meal.getMealIngredients().stream()
                .map(ingredient -> ingredient.getFoodItem().getId())
                .collect(Collectors.toList());
        checkForDuplicateTemplateMeal.checkForDuplicateTemplateMeal(foodItemIds, null);

        // Retrieve user or throw exception
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });

        // Associate meal with user
        meal.setCreatedBy(user);
        meal.incrementUserCount();
        user.getMeals().add(meal);

        // 🔥 **BELANGRIJK:** Update de voedingswaarden vóór opslaan!
        meal.updateNutrients();

        // Save meal and user
        Meal savedMeal = mealRepository.save(meal);
        userRepository.save(user);
        log.info("Successfully created meal for user ID: {}", userId);

        // Return the saved meal as a DTO
        return mealMapper.toDTO(savedMeal);
    }

    /**
     * Updates a user's meal by creating a modified version of an existing meal.
     * This method allows users to make adjustments to meals while keeping the original intact.
     *
     * @param userId      The ID of the user requesting the update.
     * @param mealId      The ID of the meal to be updated.
     * @param mealInputDTO The DTO containing the updated meal data.
     * @return The updated MealDTO.
     * @throws UserNotFoundException If the user with the specified ID does not exist.
     * @throws MealNotFoundException If the meal with the specified ID does not exist.
     */
    @Override
    @Transactional
    public MealDTO updateUserMeal(Long userId, Long mealId, MealInputDTO mealInputDTO) {
        log.info("Creating a modified version of meal ID: {} for user ID: {}", mealId, userId);

        // Retrieve the user or throw an exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Retrieve the original meal or throw an exception if not found
        Meal originalMeal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Original meal not found."));

        // Create a new meal entity based on the existing meal with modifications
        Meal updatedMeal = new Meal();
        updatedMeal.setName(mealInputDTO.getName() != null ? mealInputDTO.getName() : originalMeal.getName());
        updatedMeal.setMealDescription(mealInputDTO.getMealDescription() != null ? mealInputDTO.getMealDescription() : originalMeal.getMealDescription());
        updatedMeal.setCreatedBy(originalMeal.getCreatedBy());
        updatedMeal.setAdjustedBy(user);
        updatedMeal.setIsTemplate(false);

        // Set MealType, Cuisine, and Diet only if provided; otherwise, retain the original values
        updatedMeal.setMealType(mealInputDTO.getMealType() != null ? mealInputDTO.getMealType() : originalMeal.getMealType());
        updatedMeal.setCuisine(mealInputDTO.getCuisine() != null ? mealInputDTO.getCuisine() : originalMeal.getCuisine());
        updatedMeal.setDiet(mealInputDTO.getDiet() != null ? mealInputDTO.getDiet() : originalMeal.getDiet());

        // Process meal ingredients and associate them with the updated meal
        mealInputDTO.getMealIngredients().forEach(inputIngredient -> {
            MealIngredient ingredient = mealIngredientMapper.toEntity(inputIngredient, updatedMeal);
            updatedMeal.addMealIngredient(ingredient);
        });

        // Save the updated meal and associate it with the user
        mealRepository.save(updatedMeal);
        user.getMeals().add(updatedMeal);
        userRepository.save(user);

        log.info("Updated meal created and linked to user ID: {}", userId);
        return mealMapper.toDTO(updatedMeal);
    }

    /**
     * Links an existing meal to a user without modifying the meal.
     * This method allows users to add meals to their personal meal list.
     *
     * @param userId The ID of the user to whom the meal will be linked.
     * @param mealId The ID of the meal to be added to the user's list.
     * @return The updated UserDTO reflecting the linked meal.
     * @throws UserNotFoundException If the user with the specified ID does not exist.
     * @throws MealNotFoundException If the meal with the specified ID does not exist.
     * @throws DuplicateMealException If the user already has the specified meal linked.
     */
    @Override
    @Transactional
    public UserDTO addMealToUser(Long userId, Long mealId) {
        log.info("Linking meal ID: {} to user ID: {}", mealId, userId);

        // Retrieve the user or throw an exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Retrieve the meal or throw an exception if not found
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal not found with ID: " + mealId));

        // Check if the user already has this meal linked
        if (user.getMeals().contains(meal)) {
            throw new DuplicateMealException("User already has this meal linked.");
        }

        // Link the meal to the user and save the updated user entity
        user.getMeals().add(meal);
        userRepository.save(user);

        log.info("Meal ID: {} successfully linked to user ID: {}", mealId, userId);
        return userMapper.toDTO(user);
    }

    /**
     * Retrieves paginated and sorted meals saved by a specific user with optional filtering.
     *
     * Users can filter meals by cuisine, diet, meal type, and food items.
     * Meals can be sorted by name, total calories, protein, fat, or carbs.
     * Results are paginated.
     *
     * @param userId The ID of the user whose saved meals are to be retrieved.
     * @param cuisine Optional filter for meal cuisine.
     * @param diet Optional filter for meal diet.
     * @param mealType Optional filter for meal type (BREAKFAST, LUNCH, etc.).
     * @param foodItems Optional list of food items to filter meals by (e.g., "Banana", "Peas").
     * @param sortBy Sorting field (calories, protein, fat, carbs, name).
     * @param sortOrder Sorting order ("asc" for ascending, "desc" for descending).
     * @param pageable Pageable object for pagination and sorting.
     * @return A paginated and sorted list of MealDTOs that match the filters.
     */
    @Transactional(readOnly = true)
    public Page<MealDTO> getAllMealsForUser(
            Long userId,
            String cuisine,
            String diet,
            String mealType,
            List<String> foodItems,
            String sortBy,
            String sortOrder,
            Pageable pageable
    ) {
        log.info("Retrieving paginated user meals for user ID: {} with filters and sorting.", userId);

        // Fetch user and their saved meals
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        List<Meal> meals = new ArrayList<>(user.getMeals());

        // ✅ **Apply filters**
        if (cuisine != null) {
            meals.removeIf(meal -> !meal.getCuisine().toString().equalsIgnoreCase(cuisine));
        }
        if (diet != null) {
            meals.removeIf(meal -> !meal.getDiet().toString().equalsIgnoreCase(diet));
        }
        if (mealType != null) {
            meals.removeIf(meal -> !meal.getMealType().toString().equalsIgnoreCase(mealType));
        }
        if (foodItems != null && !foodItems.isEmpty()) {
            meals.removeIf(meal -> foodItems.stream().noneMatch(item ->
                    Arrays.asList(meal.getFoodItemsString().split(" \\| ")).contains(item)
            ));
        }

        // ✅ **Apply sorting**
        Comparator<Meal> comparator = switch (sortBy != null ? sortBy.toLowerCase() : "") {
            case "calories" -> Comparator.comparing(Meal::getTotalCalories);
            case "protein" -> Comparator.comparing(Meal::getTotalProtein);
            case "fat" -> Comparator.comparing(Meal::getTotalFat);
            case "carbs" -> Comparator.comparing(Meal::getTotalCarbs);
            default -> Comparator.comparing(Meal::getName);
        };

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }
        meals.sort(comparator);

        // ✅ **Apply pagination**
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

        log.info("Returning {} meals for user ID: {} after filtering, sorting, and pagination.", pagedMeals.size(), userId);
        return new PageImpl<>(pagedMeals.stream().map(mealMapper::toDTO).toList(), pageable, meals.size());
    }


    /**
     * Retrieves paginated and sorted meals created by a specific user with optional filtering.
     *
     * Users can filter meals by cuisine, diet, meal type, and food items.
     * Meals can be sorted by name, total calories, protein, fat, or carbs.
     * Results are paginated.
     *
     * @param userId The ID of the user whose created meals are to be retrieved.
     * @param cuisine Optional filter for meal cuisine.
     * @param diet Optional filter for meal diet.
     * @param mealType Optional filter for meal type (BREAKFAST, LUNCH, etc.).
     * @param foodItems Optional list of food items to filter meals by (e.g., "Banana", "Peas").
     * @param sortBy Sorting field (calories, protein, fat, carbs, name).
     * @param sortOrder Sorting order ("asc" for ascending, "desc" for descending).
     * @param pageable Pageable object for pagination and sorting.
     * @return A paginated and sorted list of MealDTOs that match the filters.
     */
    @Transactional(readOnly = true)
    public Page<MealDTO> getMealsCreatedByUser(
            Long userId,
            String cuisine,
            String diet,
            String mealType,
            List<String> foodItems,
            String sortBy,
            String sortOrder,
            Pageable pageable
    ) {
        log.info("Retrieving paginated meals created by user ID: {} with filters and sorting.", userId);

        // Fetch meals where createdBy matches the user ID
        List<Meal> createdMeals = mealRepository.findByCreatedBy_Id(userId);

        // ✅ **Apply filters**
        if (cuisine != null) {
            createdMeals.removeIf(meal -> !meal.getCuisine().toString().equalsIgnoreCase(cuisine));
        }
        if (diet != null) {
            createdMeals.removeIf(meal -> !meal.getDiet().toString().equalsIgnoreCase(diet));
        }
        if (mealType != null) {
            createdMeals.removeIf(meal -> !meal.getMealType().toString().equalsIgnoreCase(mealType));
        }
        if (foodItems != null && !foodItems.isEmpty()) {
            createdMeals.removeIf(meal -> foodItems.stream().noneMatch(item ->
                    Arrays.asList(meal.getFoodItemsString().split(" \\| ")).contains(item)
            ));
        }

        // ✅ **Apply sorting**
        Comparator<Meal> comparator = switch (sortBy != null ? sortBy.toLowerCase() : "") {
            case "calories" -> Comparator.comparing(Meal::getTotalCalories);
            case "protein" -> Comparator.comparing(Meal::getTotalProtein);
            case "fat" -> Comparator.comparing(Meal::getTotalFat);
            case "carbs" -> Comparator.comparing(Meal::getTotalCarbs);
            default -> Comparator.comparing(Meal::getName);
        };

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }
        createdMeals.sort(comparator);

        // ✅ **Apply pagination**
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Meal> pagedMeals;

        if (createdMeals.size() < startItem) {
            pagedMeals = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, createdMeals.size());
            pagedMeals = createdMeals.subList(startItem, toIndex);
        }

        log.info("Returning {} meals created by user ID: {} after filtering, sorting, and pagination.", pagedMeals.size(), userId);
        return new PageImpl<>(pagedMeals.stream().map(mealMapper::toDTO).toList(), pageable, createdMeals.size());
    }


    /**
     * Retrieves a Meal by its ID, only if it belongs to the specified user.
     *
     * @param id     The ID of the Meal.
     * @param userId The ID of the authenticated user.
     * @return The MealDTO.
     * @throws EntityNotFoundException If the meal with the given ID is not found,
     *                                 or if the meal does not belong to the user.
     */
    @Override
    @Transactional(readOnly = true)
    public MealDTO getUserMealById(Long id, Long userId) {
        log.info("Attempting to retrieve meal with ID: {} for user ID: {}", id, userId);

        // Fetch the authenticated user using the helper
        User user = userUpdateHelper.fetchUserById(userId);

        // Fetch the meal from the repository
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + id));

        // Check if the meal belongs to the user
        if (!user.getMeals().contains(meal)) {
            log.warn("Meal with ID {} does not belong to user ID: {}", id, userId);
            throw new EntityNotFoundException("Meal not found or does not belong to the user.");
        }

        // Map the Meal entity to a MealDTO
        MealDTO mealDTO = mealMapper.toDTO(meal);

        log.info("Successfully retrieved meal with ID: {} for user ID: {}", id, userId);
        return mealDTO;
    }

    /**
     * Unlinks a meal from a user's personal meal list.
     * This method removes the association between a user and a meal without deleting the meal itself.
     *
     * @param userId The ID of the user from whom the meal will be removed.
     * @param mealId The ID of the meal to be unlinked from the user's list.
     * @return The updated UserDTO reflecting the removal of the meal.
     * @throws UserNotFoundException If the user with the specified ID does not exist.
     * @throws MealNotFoundException If the meal is not found in the user's list.
     */
    @Override
    @Transactional
    public UserDTO removeMealFromUser(Long userId, Long mealId) {
        log.info("Unlinking meal ID {} from user ID {}", mealId, userId);

        // Retrieve the user or throw an exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Find the meal in the user's list or throw an exception if not found
        Meal meal = user.getMeals().stream()
                .filter(m -> m.getId().equals(mealId))
                .findFirst()
                .orElseThrow(() -> new MealNotFoundException("Meal not found in user's list."));

        // Remove the meal from the user's meal list and save the updated user entity
        user.getMeals().remove(meal);
        userRepository.save(user);

        log.info("Meal ID {} successfully unlinked from user ID {}", mealId, userId);
        return userMapper.toDTO(user);
    }
}

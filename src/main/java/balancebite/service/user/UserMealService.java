package balancebite.service.user;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.mealingredient.MealIngredientInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.*;
import balancebite.mapper.MealIngredientMapper;
import balancebite.mapper.MealMapper;
import balancebite.mapper.UserMapper;
import balancebite.model.diet.DietDay;
import balancebite.model.meal.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.meal.SavedMeal;
import balancebite.model.user.Role;
import balancebite.model.user.User;
import balancebite.repository.*;
import balancebite.model.user.UserRole;
import balancebite.service.CloudinaryService;
import balancebite.service.interfaces.user.IUserMealService;
import balancebite.service.util.ImageHandlerService;
import balancebite.utils.CheckForDuplicateTemplateMealUtil;
import balancebite.utils.UserUpdateHelper;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
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
    private final DietDayRepository dietDayRepository;
    private final UserMapper userMapper;
    private final MealMapper mealMapper;
    private final MealIngredientMapper mealIngredientMapper;
    private final CheckForDuplicateTemplateMealUtil checkForDuplicateTemplateMeal;
    private final UserUpdateHelper userUpdateHelper;
    private final SavedMealRepository savedMealRepository;
    private final CloudinaryService cloudinaryService;
    private final ImageHandlerService imageHandlerService;

    public UserMealService(UserRepository userRepository,
                           MealRepository mealRepository,
                           DietDayRepository dietDayRepository,
                           UserMapper userMapper,
                           MealMapper mealMapper,
                           MealIngredientMapper mealIngredientMapper,
                           CheckForDuplicateTemplateMealUtil checkForDuplicateTemplateMeal,
                           UserUpdateHelper userUpdateHelper,
                           SavedMealRepository savedMealRepository,
                           CloudinaryService cloudinaryService,
                           ImageHandlerService imageHandlerService) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.dietDayRepository = dietDayRepository;
        this.userMapper = userMapper;
        this.mealMapper = mealMapper;
        this.mealIngredientMapper = mealIngredientMapper;
        this.checkForDuplicateTemplateMeal = checkForDuplicateTemplateMeal;
        this.userUpdateHelper = userUpdateHelper;
        this.savedMealRepository = savedMealRepository;
        this.cloudinaryService = cloudinaryService;
        this.imageHandlerService = imageHandlerService;
    }

    @Override
    @Transactional
    public MealDTO createMealForUser(MealInputDTO mealInputDTO, Long userId) {
        log.info("Attempting to create a new meal for user ID: {}", userId);

        // --- 1) Enforce image source exclusivity: at most ONE of (imageFile | imageUrl | image base64) ---
        int sources = 0;
        if (mealInputDTO.getImageFile() != null && !mealInputDTO.getImageFile().isEmpty()) sources++;
        if (mealInputDTO.getImageUrl() != null && !mealInputDTO.getImageUrl().isBlank())  sources++;
        if (mealInputDTO.getImage() != null && !mealInputDTO.getImage().isBlank())        sources++;
        if (sources > 1) {
            log.error("Multiple image sources provided (imageFile/imageUrl/image). Only one is allowed.");
            throw new IllegalArgumentException("Provide exactly one of: imageFile, imageUrl, or image (base64).");
        }

        // --- 2) Map DTO -> Entity (mapper does NOT upload files; only copies simple fields) ---
        Meal meal = mealMapper.toEntity(mealInputDTO);

        // NOTE: mealTypes, cuisines, diets, preparationTime are already set by the mapper.
        // Do NOT set them again here to avoid inconsistencies.

        // Set versioning timestamp for this create
        meal.setVersion(LocalDateTime.now());

        // --- 3) Image handling via Cloudinary (create flow => currentUrl = null) ---
        // Upload if imageFile present, otherwise accept direct URL, otherwise null.
        String finalUrl = imageHandlerService.handleImage(
                null,                               // no current image on create
                mealInputDTO.getImageFile(),        // may be null/empty
                mealInputDTO.getImageUrl(),         // may be null/blank
                true                                // isCreate = true
        );
        meal.setImageUrl(finalUrl);

        // If we have a final URL, do not keep base64; else keep base64 only if it was the single provided source
        if (finalUrl != null) {
            meal.setImage(null);
        } else {
            meal.setImage((sources == 1 && mealInputDTO.getImage() != null && !mealInputDTO.getImage().isBlank())
                    ? mealInputDTO.getImage()
                    : null);
        }

        // --- 4) Validate ingredients: no duplicates by (name + quantity) ---
        Set<String> uniqueFoodItems = new HashSet<>();
        for (MealIngredient ingredient : meal.getMealIngredients()) {
            // Fallback to stored name if FoodItem is not loaded
            String itemName = ingredient.getFoodItem() != null
                    ? ingredient.getFoodItem().getName()
                    : ingredient.getFoodItemName();
            if (itemName == null) {
                log.error("Ingredient without a valid FoodItem or name encountered.");
                throw new InvalidFoodItemException("Ingredient must reference a valid FoodItem or provide a name.");
            }
            String key = itemName + "-" + ingredient.getQuantity();
            if (!uniqueFoodItems.add(key)) {
                throw new InvalidFoodItemException("Duplicate food item with same quantity: "
                        + itemName + " (" + ingredient.getQuantity() + ")");
            }
        }

        // --- 5) Duplicate template check by FoodItem IDs (order-insensitive) ---
        List<Long> foodItemIds = meal.getMealIngredients().stream()
                .map(ingredient -> ingredient.getFoodItem().getId())
                .collect(Collectors.toList());
        checkForDuplicateTemplateMeal.checkForDuplicateTemplateMeal(foodItemIds, null);

        // --- 6) Fetch user and associate ---
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });
        meal.setCreatedBy(user);
        user.getMeals().add(meal);

        // --- 7) Compute aggregated nutrients before persisting ---
        meal.updateNutrients();

        // --- 8) Role-based flag: restaurant users produce restricted meals ---
        boolean isRestricted = user.getRoles().stream()
                .map(Role::getRolename)
                .anyMatch(role -> role == UserRole.RESTAURANT);
        if (isRestricted) {
            meal.setRestricted(true);
        }

        // --- 9) Persist and return DTO ---
        Meal savedMeal = mealRepository.save(meal);
        user.getSavedMeals().add(savedMeal);
        userRepository.save(user);

        log.info("Successfully created meal {} for user ID: {}", savedMeal.getId(), userId);
        return mealMapper.toDTO(savedMeal);
    }


    /**
     * Adds an existing meal to a user's list of meals.
     *
     * The copied meal will:
     * - Receive a new unique ID
     * - Retain the original meal's ID in `originalMealId`
     * - Be marked as a non-template (`isTemplate = false`)
     * - Keep the original creator (`createdBy`)
     * - Be linked to the specified user as `adjustedBy`
     *
     * If the original meal is a template created by the user, it is linked directly without duplication.
     *
     * @param mealId The ID of the meal to copy.
     * @param userId The ID of the user who is adding the meal.
     * @return The updated UserDTO including the new meal.
     * @throws UserNotFoundException  If the user does not exist.
     * @throws MealNotFoundException  If the meal does not exist.
     * @throws DuplicateMealException If the user already has a copy of this meal.
     */
    @Override
    @Transactional
    public UserDTO addMealToUser(Long userId, Long mealId) {
        // Step 1: Retrieve user and original meal
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        Meal originalMeal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal not found with ID: " + mealId));
        if (originalMeal.isRestricted()) {
            throw new AccessDeniedException("You cannot add a restricted meal.");
        }

        // Check if user already has this meal (original or copy)
        boolean mealExists = user.getMeals().stream().anyMatch(
                m -> (m.getOriginalMealId() != null && m.getOriginalMealId().equals(mealId))
                        || m.getId().equals(mealId)
        );

        // Step 2: Directly relink if the user is the original creator of a template meal
        if (originalMeal.isTemplate() && originalMeal.getCreatedBy().getId().equals(userId)) {
            user.getMeals().add(originalMeal);
            userRepository.save(user);

            // Register a save and update save count
            SavedMeal record = new SavedMeal();
            record.setMeal(originalMeal);
            savedMealRepository.save(record);

            long total = savedMealRepository.countByMeal(originalMeal);
            originalMeal.setSaveCount(total);
            mealRepository.saveAndFlush(originalMeal);

            // Reload user to ensure updated meal save count is included
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
            return userMapper.toDTO(user);
        }

        // Step 3: Check if a copy already exists
        if (mealExists) {
            throw new DuplicateMealException("Meal copy already exists in user's list.");
        }

        // Step 4: Create a new copy of the meal
        Meal copy = new Meal();
        copy.setName(originalMeal.getName());
        copy.setMealDescription(originalMeal.getMealDescription());
        copy.setImage(originalMeal.getImage());
        copy.setImageUrl(originalMeal.getImageUrl());
        copy.setPreparationTime(originalMeal.getPreparationTime());
        copy.setCuisines(new HashSet<>(originalMeal.getCuisines()));
        copy.setDiets(new HashSet<>(originalMeal.getDiets()));
        copy.setMealTypes(new HashSet<>(originalMeal.getMealTypes()));
        copy.setOriginalMealId(mealId);
        copy.setIsTemplate(false);
        copy.setCreatedBy(originalMeal.getCreatedBy());
        copy.setAdjustedBy(user);
        copy.setVersion(LocalDateTime.now());

        // Copy ingredients
        for (MealIngredient ing : originalMeal.getMealIngredients()) {
            MealIngredient newIng = new MealIngredient();
            newIng.setFoodItem(ing.getFoodItem());
            newIng.setQuantity(ing.getQuantity());
            copy.addMealIngredient(newIng);
        }

        // Debug: print copied ingredients and nutrients
        for (MealIngredient ing : copy.getMealIngredients()) {
            System.out.println("- " + ing.getFoodItem().getName() + " x " + ing.getQuantity());
            if (ing.getFoodItem().getNutrients() != null) {
                ing.getFoodItem().getNutrients().forEach(n ->
                        System.out.println("  🔸 " + n.getNutrientName() + ": " + n.getValue())
                );
            }
        }

        // Update nutrient totals for the new meal
        copy.updateNutrients();

        // Save new meal and link to user
        mealRepository.save(copy);
        user.getMeals().add(copy);
        userRepository.save(user);

        // Register a save for the original meal and update save count
        SavedMeal record2 = new SavedMeal();
        record2.setMeal(originalMeal);
        savedMealRepository.save(record2);

        long total2 = savedMealRepository.countByMeal(originalMeal);
        originalMeal.setSaveCount(total2);
        mealRepository.saveAndFlush(originalMeal);

        // Reload user to reflect updates
        user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional
    public MealDTO updateUserMeal(Long userId, Long mealId, MealInputDTO mealInputDTO) {
        log.info("Updating existing meal ID: {} for user ID: {}", mealId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal not found with ID: " + mealId));

        // Ownership check (allow creator OR previous adjuster)
        Long adjustedById = meal.getAdjustedBy() != null ? meal.getAdjustedBy().getId() : null;
        if (!meal.getCreatedBy().getId().equals(userId) && (adjustedById == null || !adjustedById.equals(userId))) {
            throw new SecurityException("User is not allowed to update this meal.");
        }

        // Template duplicate check (if applicable)
        if (meal.isTemplate() && mealInputDTO.getMealIngredients() != null) {
            List<Long> foodItemIds = mealInputDTO.getMealIngredients().stream()
                    .map(MealIngredientInputDTO::getFoodItemId)
                    .toList();
            checkForDuplicateTemplateMeal.checkForDuplicateTemplateMeal(foodItemIds, mealId);
        }

        // --- Image input exclusivity: at most one of (file | url | base64) ---
        int sources = 0;
        if (mealInputDTO.getImageFile() != null && !mealInputDTO.getImageFile().isEmpty()) sources++;
        if (mealInputDTO.getImageUrl() != null && !mealInputDTO.getImageUrl().isBlank())  sources++;
        if (mealInputDTO.getImage() != null && !mealInputDTO.getImage().isBlank())        sources++;
        if (sources > 1) {
            throw new IllegalArgumentException("Provide exactly one of: imageFile, imageUrl, or image (base64).");
        }

        // --- Update simple fields (only when provided) ---
        if (mealInputDTO.getName() != null)               meal.setName(mealInputDTO.getName());
        if (mealInputDTO.getMealDescription() != null)    meal.setMealDescription(mealInputDTO.getMealDescription());
        if (mealInputDTO.getMealTypes() != null)          meal.setMealTypes(mealInputDTO.getMealTypes());
        if (mealInputDTO.getCuisines() != null)           meal.setCuisines(mealInputDTO.getCuisines());
        if (mealInputDTO.getDiets() != null)              meal.setDiets(mealInputDTO.getDiets());

        if (mealInputDTO.getPreparationTime() != null) {
            meal.setPreparationTime(
                    mealInputDTO.getPreparationTime().isBlank()
                            ? null
                            : Duration.parse(mealInputDTO.getPreparationTime())
            );
        }

        meal.setAdjustedBy(user);
        meal.setVersion(LocalDateTime.now());

        // --- Image handling (create=false so handler may delete old when switching/clearing) ---
        String newFinalUrl = imageHandlerService.handleImage(
                meal.getImageUrl(),                 // currentUrl
                mealInputDTO.getImageFile(),        // new file (may be null/empty)
                mealInputDTO.getImageUrl(),         // new direct URL (may be null/blank)
                false                               // update flow
        );
        meal.setImageUrl(newFinalUrl);

        // Keep base64 only if it is the single provided source and no URL resulted
        if (newFinalUrl != null) {
            meal.setImage(null); // URL wins -> do not store base64
        } else {
            meal.setImage((sources == 1 && mealInputDTO.getImage() != null && !mealInputDTO.getImage().isBlank())
                    ? mealInputDTO.getImage()
                    : null);
        }

        // --- Replace ingredients entirely (current approach) ---
        meal.getMealIngredients().clear();
        mealInputDTO.getMealIngredients().forEach(inputIngredient -> {
            MealIngredient ingredient = mealIngredientMapper.toEntity(inputIngredient, meal);
            meal.addMealIngredient(ingredient);
        });

        // Optional: validate duplicates (name + quantity) similar to create-flow

        meal.updateNutrients();

        Meal saved = mealRepository.save(meal);
        return mealMapper.toDTO(saved);
    }


    /**
     * Updates the privacy status of a specific Meal.
     *
     * This method sets the {@code isPrivate} field of the Meal entity to the provided value.
     * It is typically used to toggle the visibility of a meal (e.g., public vs private).
     *
     * @param userId The ID of the user to whom the meal is to be added.
     * @param mealId    The ID of the meal to update.
     * @param isPrivate {@code true} to make the meal private, {@code false} to make it public.
     * @throws MealNotFoundException if no meal is found with the given ID.
     */
    @Override
    public void updateMealPrivacy(Long userId, Long mealId, boolean isPrivate) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal not found with ID: " + mealId));

        // Verifieer of de user dit meal mag aanpassen
        if (!meal.getCreatedBy().getId().equals(userId) &&
                (meal.getAdjustedBy() == null || !meal.getAdjustedBy().getId().equals(userId))) {
            throw new SecurityException("User is not allowed to update this meal.");
        }

        // Extra check: zit de meal in een dieet?
        if (isPrivate) {
            List<String> dietNames = dietDayRepository.findDietNamesByMeal(meal);
            if (!dietNames.isEmpty()) {
                throw new MealInDietException("This meal is part of one or more diets and cannot be set to private.", dietNames);
            }
        }


        meal.setPrivate(isPrivate);
        mealRepository.save(meal);
    }

    @Override
    @Transactional
    public void updateMealRestriction(Long userId, Long mealId, boolean isRestricted) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal not found with ID: " + mealId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        boolean hasPermission = user.getRoles().stream()
                .map(Role::getRolename)
                .anyMatch(role -> role == UserRole.RESTAURANT || role == UserRole.DIETITIAN);

        if (!hasPermission) {
            throw new SecurityException("Only RESTAURANT or DIETITIAN users can update restriction status.");
        }

        meal.setRestricted(isRestricted);
        mealRepository.save(meal);
    }

    /**
     * Retrieves paginated and sorted meals saved by a specific user with optional filtering.
     *
     * Users can filter meals by cuisine, diet, meal type, and food items.
     * Meals can be sorted by name, total calories, protein, fat, or carbs.
     * Results are paginated.
     *
     * @param userId The ID of the user whose saved meals are to be retrieved.
     * @param cuisines Optional filter for meal cuisine.
     * @param diets Optional filter for meal diet.
     * @param mealTypes Optional filter for meal type (BREAKFAST, LUNCH, etc.).
     * @param foodItems Optional list of food items to filter meals by (e.g., "Banana", "Peas").
     * @param sortBy Sorting field (calories, protein, fat, carbs, name).
     * @param sortOrder Sorting order ("asc" for ascending, "desc" for descending).
     * @param pageable Pageable object for pagination and sorting.
     * @return A paginated and sorted list of MealDTOs that match the filters.
     */
    @Transactional(readOnly = true)
    public Page<MealDTO> getAllMealsForUser(
            Long userId,
            List<String> cuisines,
            List<String> diets,
            List<String> mealTypes,
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
        if (cuisines != null && !cuisines.isEmpty()) {
            meals.removeIf(meal ->
                    meal.getCuisines().stream().noneMatch(c -> cuisines.contains(c.name()))
            );
        }

        if (diets != null && !diets.isEmpty()) {
            meals.removeIf(meal ->
                    meal.getDiets().stream().noneMatch(d -> diets.contains(d.name()))
            );
        }

        if (mealTypes != null && !mealTypes.isEmpty()) {
            meals.removeIf(meal ->
                    meal.getMealTypes().stream().noneMatch(mt -> mealTypes.contains(mt.name()))
            );
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
            case "savecount"        -> Comparator.comparing(Meal::getSaveCount);
            case "weeklysavecount"  -> Comparator.comparing(Meal::getWeeklySaveCount);
            case "monthlysavecount" -> Comparator.comparing(Meal::getMonthlySaveCount);
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
     * @param cuisines Optional filter for meal cuisine.
     * @param diets Optional filter for meal diet.
     * @param mealTypes Optional filter for meal type (BREAKFAST, LUNCH, etc.).
     * @param foodItems Optional list of food items to filter meals by (e.g., "Banana", "Peas").
     * @param sortBy Sorting field (calories, protein, fat, carbs, name).
     * @param sortOrder Sorting order ("asc" for ascending, "desc" for descending).
     * @param pageable Pageable object for pagination and sorting.
     * @return A paginated and sorted list of MealDTOs that match the filters.
     */
    @Transactional(readOnly = true)
    public Page<MealDTO> getMealsCreatedByUser(
            Long userId,
            List<String> cuisines,
            List<String> diets,
            List<String> mealTypes,
            List<String> foodItems,
            String sortBy,
            String sortOrder,
            Pageable pageable
    ) {
        log.info("Retrieving paginated meals created by user ID: {} with filters and sorting.", userId);

        // Fetch meals where createdBy matches the user ID
        List<Meal> createdMeals = mealRepository.findByCreatedBy_Id(userId);

        createdMeals.removeIf(meal ->
                meal.getAdjustedBy() != null && !meal.getAdjustedBy().getId().equals(userId)
        );

        // ✅ **Apply filters**
        if (cuisines != null && !cuisines.isEmpty()) {
            createdMeals.removeIf(meal ->
                    meal.getCuisines().stream().noneMatch(c -> cuisines.contains(c.name()))
            );
        }

// ✅ Filter op diets (meerdere toegelaten)
        if (diets != null && !diets.isEmpty()) {
            createdMeals.removeIf(meal ->
                    meal.getDiets().stream().noneMatch(d -> diets.contains(d.name()))
            );
        }

// ✅ Filter op mealTypes (meerdere toegelaten)
        if (mealTypes != null && !mealTypes.isEmpty()) {
            createdMeals.removeIf(meal ->
                    meal.getMealTypes().stream().noneMatch(mt -> mealTypes.contains(mt.name()))
            );
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
            case "savecount"        -> Comparator.comparing(Meal::getSaveCount);
            case "weeklysavecount"  -> Comparator.comparing(Meal::getWeeklySaveCount);
            case "monthlysavecount" -> Comparator.comparing(Meal::getMonthlySaveCount);
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

    @Override
    @Transactional
    public UserDTO removeMealFromUser(Long userId, Long mealId) {
        log.info("Unlinking or deleting meal ID {} from user ID {}", mealId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Meal meal = user.getMeals().stream()
                .filter(m -> m.getId().equals(mealId))
                .findFirst()
                .orElseThrow(() -> new MealNotFoundException("Meal not found in user's list."));

        if (!meal.isTemplate()) {
            // ❗ Check if it's still used in a DietDay
            List<DietDay> daysWithMeal = dietDayRepository.findByMealsContainingWithDietFetched(meal);
            if (!daysWithMeal.isEmpty()) {
                Set<String> dietNames = daysWithMeal.stream()
                        .map(d -> d.getDiet().getName())
                        .collect(Collectors.toSet());

                String joinedNames = String.join(", ", dietNames);
                log.warn("Meal ID {} is still used in diets: {}", mealId, joinedNames);
                List<Map<String, Object>> diets = daysWithMeal.stream()
                        .map(DietDay::getDiet)
                        .distinct()
                        .map(diet -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("id", diet.getId());
                            map.put("name", diet.getName());
                            return map;
                        })
                        .toList();

                log.warn("Meal ID {} is still used in diets: {}", mealId, diets);

                throw new MealStillInUseException(
                        "This meal is still used in the following diets.",
                        diets
                );
            }

            log.info("Meal ID {} is NOT a template, deleting it permanently.", mealId);
            user.getMeals().remove(meal);
            mealRepository.delete(meal);

            if (meal.getOriginalMealId() != null) {
                savedMealRepository.deleteLatestByMealId(meal.getOriginalMealId());
                Meal original = mealRepository.findById(meal.getOriginalMealId())
                        .orElseThrow(() -> new MealNotFoundException("Original meal not found"));
                long totalSaves = savedMealRepository.countByMeal(original);
                original.setSaveCount(totalSaves);
                mealRepository.saveAndFlush(original);
            }
        } else {
            log.info("Meal ID {} is a template, unlinking from user.", mealId);
            user.getMeals().remove(meal);
        }

        userRepository.save(user);
        log.info("Meal ID {} successfully processed for user ID {}", mealId, userId);

        return userMapper.toDTO(user);
    }

    @Override
    @Transactional
    public void forceRemoveMealFromUser(Long userId, Long mealId) {
        log.info("Force-removal of meal ID {} from user ID {}", mealId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Meal meal = user.getMeals().stream()
                .filter(m -> m.getId().equals(mealId))
                .findFirst()
                .orElseThrow(() -> new MealNotFoundException("Meal not found in user's list."));

        // ✅ Verwijder meal uit alle DietDays waarin hij voorkomt
        List<DietDay> daysWithMeal = dietDayRepository.findByMealsContainingWithDietFetched(meal);
        for (DietDay day : daysWithMeal) {
            day.getMeals().remove(meal);
        }
        dietDayRepository.saveAll(daysWithMeal); // belangrijk!

        if (!meal.isTemplate()) {
            log.info("Meal ID {} is NOT a template, deleting it permanently.", mealId);
            user.getMeals().remove(meal);
            mealRepository.delete(meal);

            if (meal.getOriginalMealId() != null) {
                savedMealRepository.deleteLatestByMealId(meal.getOriginalMealId());
                Meal original = mealRepository.findById(meal.getOriginalMealId())
                        .orElseThrow(() -> new MealNotFoundException("Original meal not found"));
                long totalSaves = savedMealRepository.countByMeal(original);
                original.setSaveCount(totalSaves);
                mealRepository.saveAndFlush(original);
            }
        } else {
            log.info("Meal ID {} is a template, unlinking from user.", mealId);
            user.getMeals().remove(meal);
        }

        userRepository.save(user);
        log.info("Force-removal completed for meal ID {} and user ID {}", mealId, userId);
    }

    @Override
    @Transactional
    public void cancelMeal(Long userId, Long mealId) {
        log.info("Cancelling meal ID {} for user ID {}", mealId, userId);

        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal not found: " + mealId));

        // Ownership check
        if (!meal.getCreatedBy().getId().equals(userId)) {
            throw new AccessDeniedException("You can only cancel your own meal.");
        }

        // Haal meal uit alle DietDays
        List<DietDay> daysWithMeal = dietDayRepository.findByMealsContainingWithDietFetched(meal);
        for (DietDay day : daysWithMeal) {
            day.getMeals().remove(meal);
        }
        if (!daysWithMeal.isEmpty()) {
            dietDayRepository.saveAll(daysWithMeal);
        }

        // Verwijder links bij user
        User owner = meal.getCreatedBy();
        owner.getMeals().remove(meal);
        owner.getSavedMeals().remove(meal);

        // Image cleanup
        if (meal.getImageUrl() != null && !meal.getImageUrl().isBlank()) {
            try {
                imageHandlerService.deleteImage(meal.getImageUrl());
            } catch (Exception ex) {
                log.warn("Image cleanup failed for meal {}: {}", mealId, ex.getMessage());
            }
        }

        // Helemaal weggooien
        mealRepository.delete(meal);
        userRepository.save(owner);

        log.info("Meal ID {} fully cancelled and deleted for user ID {}", mealId, userId);
    }

}

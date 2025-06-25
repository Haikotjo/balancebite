package balancebite.service.user;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.*;
import balancebite.mapper.MealIngredientMapper;
import balancebite.mapper.MealMapper;
import balancebite.mapper.UserMapper;
import balancebite.model.diet.DietDay;
import balancebite.model.meal.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.meal.SavedMeal;
import balancebite.model.user.User;
import balancebite.repository.*;
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
    private final FileStorageService fileStorageService;

    private final SavedMealRepository savedMealRepository;

    public UserMealService(UserRepository userRepository,
                           MealRepository mealRepository,
                           DietDayRepository dietDayRepository,
                           UserMapper userMapper,
                           MealMapper mealMapper,
                           MealIngredientMapper mealIngredientMapper,
                           CheckForDuplicateTemplateMealUtil checkForDuplicateTemplateMeal,
                           UserUpdateHelper userUpdateHelper,
                           FileStorageService fileStorageService,
                           SavedMealRepository savedMealRepository) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.dietDayRepository = dietDayRepository;
        this.userMapper = userMapper;
        this.mealMapper = mealMapper;
        this.mealIngredientMapper = mealIngredientMapper;
        this.checkForDuplicateTemplateMeal = checkForDuplicateTemplateMeal;
        this.userUpdateHelper = userUpdateHelper;
        this.fileStorageService = fileStorageService;
        this.savedMealRepository = savedMealRepository;
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
        meal.setMealTypes(mealInputDTO.getMealTypes());
        meal.setCuisines(mealInputDTO.getCuisines());
        meal.setDiets(mealInputDTO.getDiets());

        // Process image from the DTO only if not already handled in the mapper
        if (meal.getImageUrl() == null && mealInputDTO.getImageFile() != null && !mealInputDTO.getImageFile().isEmpty()) {
            String imageUrl = fileStorageService.saveFile(mealInputDTO.getImageFile());
            meal.setImageUrl(imageUrl);
        }

        meal.setPreparationTime(
                mealInputDTO.getPreparationTime() != null && !mealInputDTO.getPreparationTime().isBlank()
                        ? Duration.parse(mealInputDTO.getPreparationTime())
                        : null
        );

        meal.setVersion(LocalDateTime.now());

        // Ensure no duplicate food items (same name AND same quantity)
        Set<String> uniqueFoodItems = new HashSet<>();
        for (MealIngredient ingredient : meal.getMealIngredients()) {
            String key = ingredient.getFoodItem().getName() + "-" + ingredient.getQuantity(); // Combine name + quantity
            if (!uniqueFoodItems.add(key)) {
                throw new InvalidFoodItemException("Duplicate food item with same quantity found: " + ingredient.getFoodItem().getName()
                        + " (" + ingredient.getQuantity() + ")");
            }
        }

        // Validate and check for duplicate ingredients in template meals
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
        user.getMeals().add(meal);

        // 🔥 **BELANGRIJK:** Update de voedingswaarden vóór opslaan!
        meal.updateNutrients();

        // Save meal and user
        Meal savedMeal = mealRepository.save(meal);
        user.getSavedMeals().add(savedMeal);
        userRepository.save(user);
        log.info("Successfully created meal for user ID: {}", userId);

        // Return the saved meal as a DTO
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

    /**
     * Updates a user's meal by overwriting the existing meal.
     * This method allows users to modify their own meals, including updating ingredients and image.
     *
     * @param userId        The ID of the user requesting the update.
     * @param mealId        The ID of the meal to be updated.
     * @param mealInputDTO  The DTO containing the updated meal data.
     * @return              The updated MealDTO.
     * @throws UserNotFoundException If the user with the specified ID does not exist.
     * @throws MealNotFoundException If the meal with the specified ID does not exist.
     */
    @Override
    @Transactional
    public MealDTO updateUserMeal(Long userId, Long mealId, MealInputDTO mealInputDTO) {
        log.info("Updating existing meal ID: {} for user ID: {}", mealId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal not found with ID: " + mealId));

        if (!meal.getCreatedBy().getId().equals(userId) && !meal.getAdjustedBy().getId().equals(userId)) {
            throw new SecurityException("User is not allowed to update this meal.");
        }

        meal.setName(mealInputDTO.getName());
        meal.setMealDescription(mealInputDTO.getMealDescription());
        meal.setMealTypes(mealInputDTO.getMealTypes());
        meal.setCuisines(mealInputDTO.getCuisines());
        meal.setDiets(mealInputDTO.getDiets());
        if (mealInputDTO.getPreparationTime() != null && !mealInputDTO.getPreparationTime().isBlank()) {
            meal.setPreparationTime(Duration.parse(mealInputDTO.getPreparationTime()));
        } else {
            meal.setPreparationTime(null);
        }
        meal.setAdjustedBy(user);
        meal.setVersion(LocalDateTime.now());

        // 🔥 Nieuwe afbeelding uploaden
        if (mealInputDTO.getImageFile() != null && !mealInputDTO.getImageFile().isEmpty()) {
            log.info("📷 New image file detected: {}", mealInputDTO.getImageFile().getOriginalFilename());
            if (meal.getImageUrl() != null) {
                fileStorageService.deleteFileByUrl(meal.getImageUrl());
            }
            String imageUrl = fileStorageService.saveFile(mealInputDTO.getImageFile());
            meal.setImageUrl(imageUrl);
            log.info("✅ New image URL set on meal: {}", imageUrl);
        }

        // 🧼 Verwijder afbeelding als user oude verwijderd heeft en geen nieuwe gaf
        if ((mealInputDTO.getImageFile() == null || mealInputDTO.getImageFile().isEmpty())
                && (mealInputDTO.getImageUrl() == null || mealInputDTO.getImageUrl().isBlank())
                && meal.getImageUrl() != null) {
            log.info("🧼 Removing image because frontend cleared it and no new file was provided.");
            fileStorageService.deleteFileByUrl(meal.getImageUrl());
            meal.setImageUrl(null);
        }

        if ((mealInputDTO.getImageFile() == null || mealInputDTO.getImageFile().isEmpty())
                && mealInputDTO.getImageUrl() != null && !mealInputDTO.getImageUrl().isBlank()) {
            log.info("🖼 Using image URL directly: {}", mealInputDTO.getImageUrl());
            meal.setImageUrl(mealInputDTO.getImageUrl());
        }

        meal.getMealIngredients().clear();
        mealInputDTO.getMealIngredients().forEach(inputIngredient -> {
            MealIngredient ingredient = mealIngredientMapper.toEntity(inputIngredient, meal);
            meal.addMealIngredient(ingredient);
        });

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

        createdMeals.removeIf(meal -> meal.getAdjustedBy() != null);

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Meal meal = user.getMeals().stream()
                .filter(m -> m.getId().equals(mealId))
                .findFirst()
                .orElseThrow(() -> new MealNotFoundException("Meal not found in user's list"));

        List<DietDay> daysWithMeal = dietDayRepository.findByMealsContainingWithDietFetched(meal);
        for (DietDay day : daysWithMeal) {
            day.getMeals().remove(meal);
        }

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

        userRepository.save(user);
    }
}

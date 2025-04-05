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
        meal.setMealTypes(mealInputDTO.getMealTypes());
        meal.setCuisines(mealInputDTO.getCuisines());
        meal.setDiets(mealInputDTO.getDiets());

        // Process image from the DTO only if not already handled in the mapper
        if (meal.getImageUrl() == null && mealInputDTO.getImageFile() != null && !mealInputDTO.getImageFile().isEmpty()) {
            String imageUrl = fileStorageService.saveFile(mealInputDTO.getImageFile());
            meal.setImageUrl(imageUrl);
        }

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
     * Adds an existing meal to a user's list of meals.
     *
     * The copied meal will:
     * - Have a new unique ID
     * - Retain the original meal's ID in `originalMealId`
     * - Be marked as a non-template (`isTemplate = false`)
     * - Have the original creator preserved (`createdBy` remains the same)
     * - Be assigned to the specified user (`adjustedBy = userId`)
     *
     * @param mealId The ID of the meal to be copied.
     * @param userId The ID of the user to whom the meal will be assigned.
     * @return The updated UserDTO reflecting the newly added meal.
     * @throws UserNotFoundException  If the user is not found.
     * @throws MealNotFoundException  If the meal to copy is not found.
     * @throws DuplicateMealException If the user already has a copy of the meal.
     */
    @Override
    @Transactional
    public UserDTO addMealToUser(Long userId, Long mealId) {
        log.info("Creating a personalized copy of meal ID: {} for user ID: {}", mealId, userId);

        // Retrieve the user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Retrieve the original meal
        Meal originalMeal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal not found with ID: " + mealId));

// Check if the user already has a meal with the same originalMealId OR the exact same meal ID
        boolean mealExists = user.getMeals().stream()
                .anyMatch(meal -> (meal.getOriginalMealId() != null && meal.getOriginalMealId().equals(mealId))
                        || meal.getId().equals(mealId));

        // ✅ Check of de meal door de gebruiker zelf is gemaakt en nog steeds in de database staat
        if (originalMeal.getCreatedBy().getId().equals(user.getId()) && originalMeal.isTemplate()) {
            log.info("User {} originally created this meal. Restoring link instead of copying.", userId);

            // Voeg de originele meal opnieuw toe aan de gebruiker zonder een kopie te maken
            user.getMeals().add(originalMeal);
            userRepository.save(user);

            return userMapper.toDTO(user);
        }


        if (mealExists) {
            log.warn("User ID {} already has a meal copy of original meal ID {}", userId, mealId);
            throw new DuplicateMealException("Meal copy already exists in user's list.");
        }


        // Create a copy of the meal
        Meal mealCopy = new Meal();
        mealCopy.setName(originalMeal.getName());
        mealCopy.setMealDescription(originalMeal.getMealDescription());
        mealCopy.setImage(originalMeal.getImage());
        mealCopy.setImageUrl(originalMeal.getImageUrl());
        mealCopy.setCuisines(new HashSet<>(originalMeal.getCuisines()));
        mealCopy.setDiets(new HashSet<>(originalMeal.getDiets()));
        mealCopy.setMealTypes(new HashSet<>(originalMeal.getMealTypes()));

        // Set originalMealId and mark as non-template
        mealCopy.setOriginalMealId(mealId);
        mealCopy.setIsTemplate(false);

        // Keep the original creator but assign the specified user as the adjuster
        mealCopy.setCreatedBy(originalMeal.getCreatedBy());
        mealCopy.setAdjustedBy(user);

        // Copy meal ingredients and associate them with the new meal
        List<MealIngredient> copiedIngredients = originalMeal.getMealIngredients().stream()
                .map(ingredient -> {
                    MealIngredient newIngredient = new MealIngredient();
                    newIngredient.setFoodItem(ingredient.getFoodItem());
                    newIngredient.setQuantity(ingredient.getQuantity());
                    newIngredient.setMeal(mealCopy); // Associate with new meal
                    return newIngredient;
                })
                .collect(Collectors.toList());

        mealCopy.addMealIngredients(copiedIngredients);

        // Set the initial version timestamp for tracking updates
        mealCopy.setVersion(LocalDateTime.now());

        // Recalculate nutrients instead of setting manually
        mealCopy.updateNutrients();

        // Save the copied meal
        mealRepository.save(mealCopy);

        // Link the copied meal to the user and save changes
        user.getMeals().add(mealCopy);
        userRepository.save(user);

        log.info("Successfully created and linked a meal copy with ID: {} for user ID: {}", mealCopy.getId(), userId);

        // Return the updated user DTO
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

        // Retrieve the user or throw an exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Find the meal in the user's list or throw an exception if not found
        Meal meal = user.getMeals().stream()
                .filter(m -> m.getId().equals(mealId))
                .findFirst()
                .orElseThrow(() -> new MealNotFoundException("Meal not found in user's list."));

        // If isTemplate = false remove meal completely
        if (!meal.isTemplate()) {
            log.info("Meal ID {} is NOT a template, deleting it permanently.", mealId);
            user.getMeals().remove(meal);
            mealRepository.delete(meal);
        } else {
            // If isTemplate = true, unlink
            log.info("Meal ID {} is a template, unlinking from user.", mealId);
            user.getMeals().remove(meal);
        }

        userRepository.save(user);
        log.info("Meal ID {} successfully processed for user ID {}", mealId, userId);

        return userMapper.toDTO(user);
    }

}

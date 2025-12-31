package balancebite.service.meal;

import balancebite.dto.CloudinaryUploadResult;
import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.mealingredient.MealIngredientInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.DuplicateMealException;
import balancebite.errorHandling.InvalidFoodItemException;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.mapper.MealIngredientMapper;
import balancebite.mapper.MealMapper;
import balancebite.mapper.UserMapper;
import balancebite.model.diet.DietDay;
import balancebite.model.meal.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.meal.SavedMeal;
import balancebite.model.meal.mealImage.MealImage;
import balancebite.model.user.Role;
import balancebite.model.user.User;
import balancebite.model.user.UserRole;
import balancebite.repository.*;
import balancebite.service.CloudinaryService;
import balancebite.service.interfaces.meal.IMealAdminService;
import balancebite.service.user.UserMealService;
import balancebite.utils.CheckForDuplicateTemplateMealUtil;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for managing Meal entities for ADMIN Users.
 * Handles the creation, retrieval, updating, and processing of Meal entities and their related data.
 */
@Service
public class MealAdminService implements IMealAdminService {

    private static final Logger log = LoggerFactory.getLogger(MealService.class);

    private final MealRepository mealRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;
    private final MealMapper mealMapper;
    private final UserMapper userMapper;
    private final MealIngredientMapper mealIngredientMapper;
    private final CheckForDuplicateTemplateMealUtil checkForDuplicateTemplateMeal;
    private final DietDayRepository dietDayRepository;
    private final SavedMealRepository savedMealRepository;
    private final CloudinaryService cloudinaryService;
    private final UserMealService userMealService;


    /**
     * Constructor for MealAdminService, using constructor injection.
     *
     * @param mealRepository     the repository for managing Meal entities.
     * @param foodItemRepository the repository for managing FoodItem entities.
     * @param userRepository     the repository for managing User entities.
     * @param mealMapper         the mapper for converting Meal entities to DTOs.
     */
    public MealAdminService(MealRepository mealRepository,
                            FoodItemRepository foodItemRepository,
                            UserRepository userRepository,
                            MealMapper mealMapper,
                            UserMapper userMapper,
                            MealIngredientMapper mealIngredientMapper,
                            CheckForDuplicateTemplateMealUtil checkForDuplicateTemplateMeal,
                            SavedMealRepository savedMealRepository,
                            DietDayRepository dietDayRepository,
                            CloudinaryService cloudinaryService,
                            UserMealService userMealService) {
        this.mealRepository = mealRepository;
        this.foodItemRepository = foodItemRepository;
        this.userRepository = userRepository;
        this.mealMapper = mealMapper;
        this.userMapper = userMapper;
        this.mealIngredientMapper = mealIngredientMapper;
        this.checkForDuplicateTemplateMeal = checkForDuplicateTemplateMeal;
        this.savedMealRepository = savedMealRepository;
        this.dietDayRepository = dietDayRepository;
        this.cloudinaryService = cloudinaryService;
        this.userMealService = userMealService;
    }

    /**
     * Creates a new Meal entity based on the provided MealInputDTO and associates it with a specific User if provided.
     * If no userId is provided, the meal is assigned to the authenticated User creating it (admin or chef).
     *
     * @param mealInputDTO The DTO containing the input data for creating a Meal.
     * @param authenticatedUserId The ID of the authenticated user extracted from the token.
     * @param userId       Optional: The ID of the user to associate the meal with.
     * @return The created MealDTO with the persisted meal information.
     * @throws InvalidFoodItemException if any food item in the input is invalid.
     * @throws DuplicateMealException   if a template meal with the same ingredients already exists.
     * @throws EntityNotFoundException  if the user with the provided ID is not found.
     */
    @Override
    @Transactional
    public MealDTO createMealForAdmin(MealInputDTO mealInputDTO, Long authenticatedUserId, Long userId) {
        log.info("Attempting to create a new meal (admin) with name: {}", mealInputDTO.getName());

        // --- 2) Map DTO -> Entity (mapper does NOT upload files; only copies simple fields) ---
        Meal meal = mealMapper.toEntity(mealInputDTO);

        // NOTE: mealTypes, cuisines, diets, preparationTime are already set by the mapper.
        // Do NOT set them again here to avoid inconsistencies.

        // Set versioning timestamp for this create
        meal.setVersion(LocalDateTime.now());

        // --- 3) Images handling via Cloudinary (multiple files + primaryIndex) ---
        List<MultipartFile> files = mealInputDTO.getImageFiles();
        Integer primaryIndex = mealInputDTO.getPrimaryIndex();

        if (files != null && !files.isEmpty()) {
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                if (file == null || file.isEmpty()) continue;

                CloudinaryUploadResult upload = cloudinaryService.uploadFile(file);

                MealImage image = new MealImage(meal, upload.getImageUrl(), upload.getPublicId());
                image.setOrderIndex(i);

                boolean isPrimary =
                        primaryIndex != null
                                && primaryIndex >= 0
                                && primaryIndex < files.size()
                                && i == primaryIndex;

                image.setPrimary(isPrimary);

                meal.addImage(image);
            }
        }

        // --- 4) Validate ingredients: no duplicates by (name + quantity) ---
        Set<String> uniqueFoodItems = new HashSet<>();
        for (MealIngredient ingredient : meal.getMealIngredients()) {
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

        // --- 6) Fetch target user and associate (ADMIN DIFFERENCE) ---
        Long targetUserId = (userId != null) ? userId : authenticatedUserId;

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", targetUserId);
                    return new UserNotFoundException("User not found with ID: " + targetUserId);
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

        log.info("Successfully created meal {} for target user ID: {}", savedMeal.getId(), targetUserId);
        return mealMapper.toDTO(savedMeal);
    }

    @Override
    @Transactional
    public MealDTO updateMealForAdmin(Long authenticatedUserId, Long mealId, MealInputDTO mealInputDTO) {
        log.info("Updating meal (admin) mealId={}, authenticatedUserId={}", mealId, authenticatedUserId);

        User adminUser = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + authenticatedUserId));

        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal not found with ID: " + mealId));

        // Template duplicate check (if applicable)
        if (meal.isTemplate() && mealInputDTO.getMealIngredients() != null) {
            List<Long> foodItemIds = mealInputDTO.getMealIngredients().stream()
                    .map(MealIngredientInputDTO::getFoodItemId)
                    .toList();
            checkForDuplicateTemplateMeal.checkForDuplicateTemplateMeal(foodItemIds, mealId);
        }

        // --- Update simple fields (only when provided) ---
        if (mealInputDTO.getName() != null)            meal.setName(mealInputDTO.getName());
        if (mealInputDTO.getMealDescription() != null) meal.setMealDescription(mealInputDTO.getMealDescription());
        if (mealInputDTO.getMealTypes() != null)       meal.setMealTypes(mealInputDTO.getMealTypes());
        if (mealInputDTO.getCuisines() != null)        meal.setCuisines(mealInputDTO.getCuisines());
        if (mealInputDTO.getDiets() != null)           meal.setDiets(mealInputDTO.getDiets());
        if (mealInputDTO.getVideoUrl() != null)            meal.setVideoUrl(mealInputDTO.getVideoUrl());
        if (mealInputDTO.getSourceUrl() != null)           meal.setSourceUrl(mealInputDTO.getSourceUrl());
        if (mealInputDTO.getPreparationVideoUrl() != null) meal.setPreparationVideoUrl(mealInputDTO.getPreparationVideoUrl());
        if (mealInputDTO.getMealPreparation() != null)     meal.setMealPreparation(mealInputDTO.getMealPreparation());

        if (mealInputDTO.getPreparationTime() != null) {
            meal.setPreparationTime(
                    mealInputDTO.getPreparationTime().isBlank()
                            ? null
                            : Duration.parse(mealInputDTO.getPreparationTime())
            );
        }

        // Audit
        meal.setAdjustedBy(adminUser);
        meal.setVersion(LocalDateTime.now());

        // --- Images update (multi-image) ---
        List<Long> keepIds = mealInputDTO.getKeepImageIds();

// Only touch existing images if the client explicitly provided keepImageIds
        if (keepIds != null) {
            for (MealImage img : new ArrayList<>(meal.getImages())) {
                if (!keepIds.contains(img.getId())) {
                    cloudinaryService.deleteFileIfUnused(img.getPublicId());
                    meal.getImages().remove(img); // orphanRemoval deletes DB row
                }
            }
        }


        List<MultipartFile> files = mealInputDTO.getImageFiles();
        List<Integer> replaceSlots = mealInputDTO.getReplaceOrderIndexes();

        if (files != null && !files.isEmpty()) {
            if (replaceSlots == null || replaceSlots.size() != files.size()) {
                throw new IllegalArgumentException("replaceOrderIndexes must match imageFiles size.");
            }

            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                if (file == null || file.isEmpty()) continue;

                int slot = replaceSlots.get(i);
                if (slot < 0 || slot > 4) {
                    throw new IllegalArgumentException("replaceOrderIndexes must be in range 0..4");
                }

                MealImage existing = meal.getImages().stream()
                        .filter(img -> img.getOrderIndex() == slot)
                        .findFirst()
                        .orElse(null);

                if (existing != null) {
                    cloudinaryService.deleteFileIfUnused(existing.getPublicId());
                    meal.getImages().remove(existing);
                }

                CloudinaryUploadResult upload = cloudinaryService.uploadFile(file);

                MealImage newImg = new MealImage(meal, upload.getImageUrl(), upload.getPublicId());
                newImg.setOrderIndex(slot);
                meal.addImage(newImg);
            }
        }

        Integer primaryIndex = mealInputDTO.getPrimaryIndex();
        if (primaryIndex != null) {
            meal.getImages().forEach(img -> img.setPrimary(img.getOrderIndex() == primaryIndex));
        }

        // --- Replace ingredients entirely ---
        meal.getMealIngredients().clear();
        if (mealInputDTO.getMealIngredients() != null) {
            mealInputDTO.getMealIngredients().forEach(inputIngredient -> {
                MealIngredient ingredient = mealIngredientMapper.toEntity(inputIngredient, meal);
                meal.addMealIngredient(ingredient);
            });
        }

        meal.updateNutrients();

        Meal saved = mealRepository.save(meal);
        return mealMapper.toDTO(saved);
    }

    /**
     * Adds a copy of an existing meal to a user's list of meals.
     * This allows users to customize meals in their own lists without affecting other users' copies of the same meal.
     * ADMIN or CHEF roles can assign meals to other users.
     *
     * @param targetUserId The ID of the user to whom the meal will be assigned.
     * @param mealId The ID of the meal to be copied and added to the user.
     * @return UserDTO The updated user information with the added meal.
     * @throws UserNotFoundException  If the user is not found.
     * @throws MealNotFoundException  If the meal is not found.
     * @throws DuplicateMealException If an identical meal already exists in the user's list.
     */
    @Override
    @Transactional
    public UserDTO addMealToUserAsAdmin(Long targetUserId, Long mealId, Long adminUserId) {
        // Alleen voor autorisatie/audit; targetUserId is de echte “aan wie voeg je toe”
        User adminUser = userRepository.findById(adminUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + adminUserId));

        // Optional: role check (als je dit al via Security config doet, kun je dit weglaten)
        boolean isAdmin = adminUser.getRoles().stream()
                .map(Role::getRolename)
                .anyMatch(r -> r == UserRole.ADMIN || r == UserRole.CHEF);
        if (!isAdmin) {
            throw new SecurityException("Not allowed.");
        }

        return userMealService.addMealToUser(targetUserId, mealId);
    }

    /**
     * Retrieves all Meals from the repository, regardless of their isTemplate value.
     *
     * @return A list of MealDTOs representing all meals, or an empty list if no meals are found.
     */
    @Override
    @Transactional(readOnly = true)
    public List<MealDTO> getAllMeals() {
        log.info("Retrieving all meals from the system, regardless of template status.");
        List<Meal> allMeals = mealRepository.findAll(); // Fetch all meals from the repository
        if (allMeals.isEmpty()) {
            log.info("No meals found in the system.");
        } else {
            log.info("Found {} meals in the system.", allMeals.size());
        }
        return allMeals.stream().map(mealMapper::toDTO).toList();
    }

    /**
     * Retrieves a Meal entity by its ID.
     * This method is intended for Admin users and allows retrieval of any meal,
     * regardless of ownership or template status.
     *
     * @param id The ID of the Meal to retrieve.
     * @return The MealDTO representing the meal.
     * @throws EntityNotFoundException If the meal with the given ID does not exist.
     */
    @Override
    @Transactional(readOnly = true)
    public MealDTO getMealById(Long id) {
        log.info("Admin attempting to retrieve meal with ID: {}", id);

        // Fetch the meal from the repository
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + id));

        // Map the Meal entity to a MealDTO
        MealDTO mealDTO = mealMapper.toDTO(meal);

        log.info("Admin successfully retrieved meal with ID: {}", id);
        return mealDTO;
    }


    /**
     * Deletes a specific meal from the repository.
     *
     * @param mealId The ID of the meal to be deleted.
     * @throws EntityNotFoundException if the meal with the given ID is not found.
     */
    @Override
    @Transactional
    public void deleteMeal(Long mealId) {
        log.info("Attempting to delete meal with ID: {}", mealId);

        // Retrieve the meal or throw an exception if not found
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + mealId));

        // Loop through users associated with the meal and remove the association
        List<User> associatedUsers = userRepository.findAllByMealsContaining(meal);
        for (User user : associatedUsers) {
            log.info("Removing association between User ID: {} and Meal ID: {}", user.getId(), meal.getId());
            user.getMeals().remove(meal);
        }

        List<User> usersWithSavedMeal = userRepository.findAllBySavedMealsContaining(meal);
        for (User user : usersWithSavedMeal) {
            log.info("Removing saved meal association for User ID: {} and Meal ID: {}", user.getId(), meal.getId());
            user.getSavedMeals().remove(meal);
        }
        userRepository.saveAll(usersWithSavedMeal);

        // Save updated users back to the database to ensure association is removed
        userRepository.saveAll(associatedUsers);

        List<DietDay> dietDaysWithMeal = dietDayRepository.findAllByMealsContaining(meal);
        for (DietDay day : dietDaysWithMeal) {
            log.info("Removing meal from DietDay ID: {}", day.getId());
            day.getMeals().remove(meal);
        }
        dietDayRepository.saveAll(dietDaysWithMeal);

        mealRepository.deleteFromSavedMeal(mealId);
        mealRepository.flush();
        for (MealImage img : new ArrayList<>(meal.getImages())) {
            cloudinaryService.deleteFileIfUnused(img.getPublicId());
        }

        // Delete the meal after cleaning up the relationships
        mealRepository.delete(meal);
        log.info("Successfully deleted meal with ID: {}", mealId);
    }
}

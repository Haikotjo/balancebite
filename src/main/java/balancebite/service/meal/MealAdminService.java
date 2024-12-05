package balancebite.service.meal;

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
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.user.User;
import balancebite.repository.FoodItemRepository;
import balancebite.repository.MealRepository;
import balancebite.repository.UserRepository;
import balancebite.service.interfaces.meal.IMealAdminService;
import balancebite.utils.CheckForDuplicateTemplateMealUtil;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    /**
     * Constructor for MealAdminService, using constructor injection.
     *
     * @param mealRepository     the repository for managing Meal entities.
     * @param foodItemRepository the repository for managing FoodItem entities.
     * @param userRepository     the repository for managing User entities.
     * @param mealMapper         the mapper for converting Meal entities to DTOs.
     */
    public MealAdminService(MealRepository mealRepository, FoodItemRepository foodItemRepository, UserRepository userRepository, MealMapper mealMapper, UserMapper userMapper, MealIngredientMapper mealIngredientMapper, CheckForDuplicateTemplateMealUtil checkForDuplicateTemplateMeal) {
        this.mealRepository = mealRepository;
        this.foodItemRepository = foodItemRepository;
        this.userRepository = userRepository;
        this.mealMapper = mealMapper;
        this.userMapper = userMapper;
        this.mealIngredientMapper = mealIngredientMapper;
        this.checkForDuplicateTemplateMeal = checkForDuplicateTemplateMeal;
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
        log.info("Attempting to create a new meal with name: {}", mealInputDTO.getName());

        // Validate that both image and imageUrl are not provided simultaneously
        if (mealInputDTO.getImage() != null && mealInputDTO.getImageUrl() != null) {
            log.error("Both image and imageUrl provided. Only one of them is allowed.");
            throw new IllegalArgumentException("You can only provide either an image or an imageUrl, not both.");
        }

        // Convert input DTO to Meal entity
        Meal meal = mealMapper.toEntity(mealInputDTO);

        // Handle image and imageUrl logic
        if (mealInputDTO.getImage() != null) {
            log.info("Using uploaded image for the meal.");
            meal.setImage(mealInputDTO.getImage());
        } else if (mealInputDTO.getImageUrl() != null) {
            log.info("Using provided image URL for the meal.");
            meal.setImageUrl(mealInputDTO.getImageUrl());
        } else {
            log.info("No image or imageUrl provided for the meal.");
        }

        // Collect all FoodItem IDs from the Meal's ingredients
        List<Long> foodItemIds = meal.getMealIngredients().stream()
                .map(mi -> mi.getFoodItem().getId())
                .collect(Collectors.toList());
        log.debug("Collected food item IDs for duplicate check: {}", foodItemIds);

        // Validate each FoodItem ID to ensure it exists in the database
        for (Long foodItemId : foodItemIds) {
            if (!foodItemRepository.existsById(foodItemId)) {
                log.error("Invalid food item ID: {}", foodItemId);
                throw new InvalidFoodItemException("Invalid food item ID: " + foodItemId);
            }
        }

        // Use CheckForDuplicateTemplateMealUtil to check for duplicate template meals
        checkForDuplicateTemplateMeal.checkForDuplicateTemplateMeal(foodItemIds, null);

        // Determine which user to associate the meal with
        Long targetUserId = (userId != null) ? userId : authenticatedUserId;
        log.info("Associating meal with user ID: {}", targetUserId);

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + targetUserId));

        // Associate meal with the determined user
        meal.setCreatedBy(user);
        user.getMeals().add(meal);

        // Save user and meal to persist the relationship
        userRepository.save(user);
        Meal savedMeal = mealRepository.save(meal);

        log.info("Successfully created a new meal with ID: {} for user ID: {}", savedMeal.getId(), targetUserId);

        // Convert saved Meal entity to DTO for the response
        return mealMapper.toDTO(savedMeal);
    }


    /**
     * Updates an existing Meal entity with new information.
     * If the meal is a template (isTemplate = true), it checks to ensure no duplicate ingredient lists.
     * The user relationship remains unchanged during this update.
     *
     * @param id           the ID of the meal to be updated.
     * @param mealInputDTO the DTO containing the updated meal information.
     * @return the updated MealDTO containing the new meal data.
     * @throws EntityNotFoundException if the meal with the given ID is not found.
     * @throws InvalidFoodItemException if any food item ID in the ingredients is invalid.
     * @throws DuplicateMealException   if updating would create a duplicate template meal.
     */
    @Override
    @Transactional
    public MealDTO updateMeal(Long id, MealInputDTO mealInputDTO) {
        log.info("Updating meal with ID: {}", id);
        Meal existingMeal = mealRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + id));

        // Duplicate check for template meals (isTemplate = true)
        if (existingMeal.isTemplate() && mealInputDTO.getMealIngredients() != null) {
            List<Long> foodItemIds = mealInputDTO.getMealIngredients().stream()
                    .map(MealIngredientInputDTO::getFoodItemId)
                    .toList();

            // Use CheckForDuplicateTemplateMealUtil to check for duplicate template meals, passing the current meal ID
            checkForDuplicateTemplateMeal.checkForDuplicateTemplateMeal(foodItemIds, id);
        }

        // Proceed with updating the meal fields
        if (mealInputDTO.getName() != null) {
            existingMeal.setName(mealInputDTO.getName());
        }
        if (mealInputDTO.getMealDescription() != null) {
            existingMeal.setMealDescription(mealInputDTO.getMealDescription());
        }

        if (mealInputDTO.getMealIngredients() != null) {
            // Clear existing ingredients only if we have a new list to replace them with
            existingMeal.getMealIngredients().clear();
            List<MealIngredient> updatedIngredients = mealInputDTO.getMealIngredients().stream()
                    .map(inputDTO -> mealIngredientMapper.toEntity(inputDTO, existingMeal))
                    .toList();
            existingMeal.addMealIngredients(updatedIngredients);
        }

        Meal savedMeal = mealRepository.save(existingMeal);
        log.info("Successfully updated meal with ID: {}", id);
        return mealMapper.toDTO(savedMeal);
    }

    /**
     * Adds a copy of an existing meal to a user's list of meals.
     * This allows users to customize meals in their own lists without affecting other users' copies of the same meal.
     * ADMIN or CHEF roles can assign meals to other users.
     *
     * @param userId The ID of the user to whom the meal will be assigned.
     * @param mealId The ID of the meal to be copied and added to the user.
     * @return UserDTO The updated user information with the added meal.
     * @throws UserNotFoundException  If the user is not found.
     * @throws MealNotFoundException  If the meal is not found.
     * @throws DuplicateMealException If an identical meal already exists in the user's list.
     */
    @Override
    @Transactional
    public UserDTO addMealToUser(Long userId, Long mealId) {
        log.info("Adding meal with ID: {} to user with ID: {}", mealId, userId);

        // Retrieve user and meal
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        Meal originalMeal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal not found with ID: " + mealId));

        // Check for duplicates
        List<Meal> duplicateMeals = mealRepository.findUserMealsWithExactIngredients(mealId, userId);
        if (!duplicateMeals.isEmpty()) {
            log.warn("Duplicate meal detected for user ID: {} with meal ID: {}", userId, mealId);
            throw new DuplicateMealException("Meal already exists in user's list.");
        }

        // Create a copy
        Meal mealCopy = balancebite.util.MealCopyUtil.createMealCopy(originalMeal, user);
        mealRepository.save(mealCopy);

        // Update user and save
        user.getMeals().add(mealCopy);
        userRepository.save(user);
        log.info("Successfully added meal with ID: {} to user ID: {}", mealId, userId);

        return userMapper.toDTO(user);
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

        // Save updated users back to the database to ensure association is removed
        userRepository.saveAll(associatedUsers);

        // Delete the meal after cleaning up the relationships
        mealRepository.delete(meal);
        log.info("Successfully deleted meal with ID: {}", mealId);
    }
}

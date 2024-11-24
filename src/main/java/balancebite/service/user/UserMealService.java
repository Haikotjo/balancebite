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
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.user.User;
import balancebite.repository.MealRepository;
import balancebite.repository.UserRepository;
import balancebite.service.interfaces.IUserMealService;
import balancebite.utils.CheckForDuplicateTemplateMealUtil;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    public UserMealService(UserRepository userRepository, MealRepository mealRepository, UserMapper userMapper,
                           MealMapper mealMapper, MealIngredientMapper mealIngredientMapper,
                           CheckForDuplicateTemplateMealUtil checkForDuplicateTemplateMeal) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.userMapper = userMapper;
        this.mealMapper = mealMapper;
        this.mealIngredientMapper = mealIngredientMapper;
        this.checkForDuplicateTemplateMeal = checkForDuplicateTemplateMeal;
    }

    /**
     * Creates a new Meal entity for a specific user based on the provided MealInputDTO.
     * This method converts the input DTO to a Meal entity, associates it with a user, persists it, and then converts the result back to a DTO.
     *
     * @param mealInputDTO The DTO containing the input data for creating a Meal.
     * @param userId       The ID of the user to whom the meal will be associated.
     * @return The created MealDTO with the persisted meal information.
     * @throws InvalidFoodItemException if any food item in the input is invalid.
     * @throws EntityNotFoundException  if the user cannot be found.
     * @throws DuplicateMealException   if a template meal with the same ingredients already exists.
     */
    @Override
    @Transactional
    public MealDTO createMealForUser(MealInputDTO mealInputDTO, Long userId) {
        log.info("Attempting to create a new meal for user ID: {}", userId);

        // Convert DTO to Meal entity
        Meal meal = mealMapper.toEntity(mealInputDTO);
        log.debug("Meal converted from DTO: {}", meal);

        // Validate and check for duplicate ingredients
        List<Long> foodItemIds = meal.getMealIngredients().stream()
                .map(ingredient -> ingredient.getFoodItem().getId())
                .collect(Collectors.toList());
        log.debug("Collected food item IDs for duplicate check: {}", foodItemIds);

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

        // Save meal and user
        Meal savedMeal = mealRepository.save(meal);
        userRepository.save(user);
        log.info("Successfully created meal for user ID: {}", userId);

        return mealMapper.toDTO(savedMeal);
    }

    /**
     * Updates an existing Meal entity for a specific user.
     * Only meals in the user's list can be updated, with appropriate checks based on the template status.
     *
     * @param userId       The ID of the user whose meal is to be updated.
     * @param mealId       The ID of the meal to be updated.
     * @param mealInputDTO The new details of the meal.
     * @return The updated MealDTO with the new meal data.
     * @throws EntityNotFoundException if the user or meal cannot be found.
     * @throws InvalidFoodItemException if any food item ID in the ingredients is invalid.
     * @throws DuplicateMealException   if updating would create a duplicate template meal.
     */
    @Override
    @Transactional
    public MealDTO updateUserMeal(Long userId, Long mealId, MealInputDTO mealInputDTO) {
        log.info("Updating meal with ID: {} for user ID: {}", mealId, userId);

        // Retrieve user and meal
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", userId);
                    return new UserNotFoundException("User not found with ID: " + userId);
                });

        Meal existingMeal = user.getMeals().stream()
                .filter(meal -> meal.getId().equals(mealId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Meal with ID {} not found in user's list", mealId);
                    return new MealNotFoundException("Meal not found with ID: " + mealId);
                });

        log.debug("Existing meal retrieved: {}", existingMeal);

        // Update fields
        if (mealInputDTO.getName() != null) {
            existingMeal.setName(mealInputDTO.getName());
        }
        if (mealInputDTO.getMealDescription() != null) {
            existingMeal.setMealDescription(mealInputDTO.getMealDescription());
        }
        if (mealInputDTO.getMealIngredients() != null) {
            existingMeal.getMealIngredients().clear();
            mealInputDTO.getMealIngredients().stream()
                    .map(inputDTO -> mealIngredientMapper.toEntity(inputDTO, existingMeal))
                    .forEach(existingMeal::addMealIngredient);
        }

        Meal savedMeal = mealRepository.save(existingMeal);
        log.info("Successfully updated meal with ID: {} for user ID: {}", mealId, userId);

        return mealMapper.toDTO(savedMeal);
    }

    /**
     * Adds a copy of an existing meal to a user's list of meals. This allows users to customize
     * meals in their own lists without affecting other users' copies of the same meal.
     *
     * @param userId The ID of the user to whom the meal will be added.
     * @param mealId The ID of the meal to be copied and added to the user.
     * @return UserDTO The updated user information with the added meal.
     * @throws UserNotFoundException If the user is not found.
     * @throws MealNotFoundException If the meal is not found.
     * @throws DuplicateMealException If an identical meal already exists in the user's list.
     */
    @Override
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
        Meal mealCopy = createMealCopy(originalMeal, user);
        mealRepository.save(mealCopy);

        // Update user and save
        user.getMeals().add(mealCopy);
        userRepository.save(user);
        log.info("Successfully added meal with ID: {} to user with ID: {}", mealId, userId);

        return userMapper.toDTO(user);
    }

    /**
     * Creates a deep copy of a meal for a specific user.
     * Preserves the original creator while associating the copy with the new user.
     *
     * @param originalMeal The original Meal entity to copy.
     * @param user         The User entity to associate with the copied Meal.
     * @return A new Meal entity that is a copy of the original Meal.
     */
    private Meal createMealCopy(Meal originalMeal, User user) {
        Meal mealCopy = new Meal();
        mealCopy.setName(originalMeal.getName());
        mealCopy.setMealDescription(originalMeal.getMealDescription());
        mealCopy.setCreatedBy(originalMeal.getCreatedBy());
        mealCopy.setAdjustedBy(user);
        mealCopy.setIsTemplate(false);
        originalMeal.getMealIngredients().forEach(ingredient -> {
            MealIngredient copiedIngredient = new MealIngredient();
            copiedIngredient.setFoodItem(ingredient.getFoodItem());
            copiedIngredient.setQuantity(ingredient.getQuantity());
            mealCopy.addMealIngredient(copiedIngredient);
        });
        log.debug("Meal copy created: {}", mealCopy);
        return mealCopy;
    }

    /**
     * Retrieves all meals for a specific user by user ID.
     *
     * @param userId The ID of the user whose meals are to be retrieved.
     * @return A list of MealDTOs representing the user's meals, or an empty list if no meals are found.
     */
    @Transactional(readOnly = true)
    public List<MealDTO> getAllMealsForUser(Long userId) {
        log.info("Retrieving all meals for user ID: {}", userId);

        // Find the user by ID and fetch their meals
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Map the user's meals to MealDTOs
        return user.getMeals().stream().map(mealMapper::toDTO).toList();
    }

    public List<MealDTO> getMealsCreatedByUser(Long userId) {
        return mealRepository.findByCreatedBy_Id(userId)
                .stream()
                .map(mealMapper::toDTO)
                .collect(Collectors.toList());
    }


    /**
     * Removes a specific meal from a user's list of meals.
     *
     * @param userId The ID of the user from whom the meal will be removed.
     * @param mealId The ID of the meal to be removed.
     * @return UserDTO The updated user information without the removed meal.
     * @throws UserNotFoundException If the user is not found.
     * @throws MealNotFoundException If the meal is not found in the user's list.
     */
    @Override
    public UserDTO removeMealFromUser(Long userId, Long mealId) {
        log.info("Attempting to remove meal from user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Meal meal = user.getMeals().stream()
                .filter(m -> m.getId().equals(mealId))
                .findFirst()
                .orElseThrow(() -> new MealNotFoundException("The meal with ID " + mealId + " is not part of the user's meal list."));

        user.getMeals().remove(meal);
        User updatedUser = userRepository.save(user);

        log.info("Successfully removed meal from user with ID: {}", userId);
        return userMapper.toDTO(updatedUser);
    }
}

package balancebite.service.user;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.DuplicateMealException;
import balancebite.errorHandling.InvalidFoodItemException;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.mapper.MealMapper;
import balancebite.mapper.UserMapper;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.User;
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
    private final CheckForDuplicateTemplateMealUtil checkForDuplicateTemplateMeal;

    public UserMealService(UserRepository userRepository, MealRepository mealRepository, UserMapper userMapper, MealMapper mealMapper, CheckForDuplicateTemplateMealUtil checkForDuplicateTemplateMeal) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.userMapper = userMapper;
        this.mealMapper = mealMapper;
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

        Meal meal = mealMapper.toEntity(mealInputDTO);
        List<Long> foodItemIds = meal.getMealIngredients().stream()
                .map(mi -> mi.getFoodItem().getId())
                .collect(Collectors.toList());
        log.debug("Collected food item IDs for duplicate check: {}", foodItemIds);

        // Use CheckForDuplicateTemplateMealUtil to check for duplicate template meals
        checkForDuplicateTemplateMeal.checkForDuplicateTemplateMeal(foodItemIds, null);

        log.debug("Attempting to retrieve user by ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        meal.setCreatedBy(user);
        meal.incrementUserCount();
        user.getMeals().add(meal);
        log.debug("Meal prepared for saving: {}", meal);

        Meal savedMeal = mealRepository.save(meal);
        userRepository.save(user);

        log.info("Successfully created a new meal for user with ID: {}", userId);
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
        log.info("Attempting to add a copy of meal with ID: {} to user with ID: {}", mealId, userId);

        // Retrieve user by ID or throw exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Retrieve meal by ID or throw exception if not found
        Meal originalMeal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal not found with ID: " + mealId));

        // Collect food item IDs for duplicate check
        List<Long> foodItemIds = originalMeal.getMealIngredients().stream()
                .map(ingredient -> ingredient.getFoodItem().getId())
                .collect(Collectors.toList());
        log.debug("Collected food item IDs for duplicate check: {}", foodItemIds);

        // Check for duplicates in the user's meal list
        List<Meal> duplicateMeals = mealRepository.findUserMealsWithExactIngredients(mealId, userId);
        if (!duplicateMeals.isEmpty()) {
            Meal duplicateMeal = duplicateMeals.get(0); // Neem de eerste duplicate
            String errorMsg = String.format("A meal with the same ingredients already exists. %s (ID: %d)",
                    duplicateMeal.getName(), duplicateMeal.getId());
            log.warn("Duplicate meal detected in user's list: {}", errorMsg);
            throw new DuplicateMealException(errorMsg);
        }

        // Increment the user count for the original meal
        originalMeal.incrementUserCount();
        mealRepository.save(originalMeal); // Persist the updated user count for the original meal

        // Create a deep copy of the original meal for the user
        Meal mealCopy = new Meal();
        mealCopy.setName(originalMeal.getName());
        mealCopy.setMealDescription(originalMeal.getMealDescription());
        mealCopy.setCreatedBy(originalMeal.getCreatedBy()); // Preserve the original creator
        mealCopy.setAdjustedBy(user); // Set the current user as the one who adjusted the meal
        mealCopy.setIsTemplate(false); // Mark this as a user-specific copy, not a template

        // Copy each ingredient from the original meal into the new meal
        originalMeal.getMealIngredients().forEach(ingredient -> {
            MealIngredient copiedIngredient = new MealIngredient();
            copiedIngredient.setFoodItem(ingredient.getFoodItem());
            copiedIngredient.setQuantity(ingredient.getQuantity());
            mealCopy.addMealIngredient(copiedIngredient);
        });

        user.getMeals().add(mealCopy);
        mealRepository.save(mealCopy); // Persist the meal copy in the database

        log.info("Successfully added a copy of meal with ID: {} to user with ID: {}", mealId, userId);

        // Save the updated user to maintain the relationship with the new meal copy
        User updatedUser = userRepository.save(user);

        return userMapper.toDTO(updatedUser);
    }

    /**
     * Retrieves all meals for a specific user by user ID.
     *
     * @param userId the ID of the user whose meals are to be retrieved
     * @return a list of MealDTOs representing the user's meals, or an empty list if no meals are found
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

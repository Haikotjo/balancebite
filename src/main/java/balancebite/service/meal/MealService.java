package balancebite.service.meal;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.dto.mealingredient.MealIngredientInputDTO;
import balancebite.errorHandling.DuplicateMealException;
import balancebite.errorHandling.InvalidFoodItemException;
import balancebite.mapper.MealIngredientMapper;
import balancebite.mapper.MealMapper;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.user.User;
import balancebite.repository.FoodItemRepository;
import balancebite.repository.MealRepository;
import balancebite.repository.UserRepository;
import balancebite.service.interfaces.meal.IMealService;
import balancebite.utils.NutrientCalculatorUtil;
import balancebite.utils.CheckForDuplicateTemplateMealUtil;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(MealService.class);

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

//    /**
//     * Creates a new Meal entity based on the provided MealInputDTO.
//     * This method converts the input DTO to a Meal entity, checks the validity of each FoodItem in the meal,
//     * persists the entity, and then converts the result back to a DTO.
//     *
//     * @param mealInputDTO The DTO containing the input data for creating a Meal.
//     * @return The created MealDTO with the persisted meal information.
//     * @throws InvalidFoodItemException if any food item in the input is invalid.
//     * @throws DuplicateMealException if a template meal with the same ingredients already exists.
//     */
//    @Override
//    @Transactional
//    public MealDTO createMealNoUser(MealInputDTO mealInputDTO) {
//        log.info("Attempting to create a new meal with name: {}", mealInputDTO.getName());
//
//        // Controleer of zowel image als imageUrl is ingevuld
//        if (mealInputDTO.getImage() != null && mealInputDTO.getImageUrl() != null) {
//            log.error("Both image and imageUrl provided. Only one of them is allowed.");
//            throw new IllegalArgumentException("You can only provide either an image or an imageUrl, not both.");
//        }
//
//        // Convert input DTO to Meal entity
//        Meal meal = mealMapper.toEntity(mealInputDTO);
//
//        // Handle image and imageUrl logic
//        if (mealInputDTO.getImage() != null) {
//            log.info("Using uploaded image for the meal.");
//            meal.setImage(mealInputDTO.getImage());
//        } else if (mealInputDTO.getImageUrl() != null) {
//            log.info("Using provided image URL for the meal.");
//            meal.setImageUrl(mealInputDTO.getImageUrl());
//        } else {
//            log.info("No image or imageUrl provided for the meal.");
//        }
//
//        // Collect all FoodItem IDs from the Meal's ingredients
//        List<Long> foodItemIds = meal.getMealIngredients().stream()
//                .map(mi -> mi.getFoodItem().getId())
//                .collect(Collectors.toList());
//        log.debug("Collected food item IDs for duplicate check: {}", foodItemIds);
//
//        // Validate each FoodItem ID to ensure it exists in the database
//        for (Long foodItemId : foodItemIds) {
//            if (!foodItemRepository.existsById(foodItemId)) {
//                log.error("Invalid food item ID: {}", foodItemId);
//                throw new InvalidFoodItemException("Invalid food item ID: " + foodItemId);
//            }
//        }
//
//        // Use CheckForDuplicateTemplateMealUtil to check for duplicate template meals
//        checkForDuplicateTemplateMeal.checkForDuplicateTemplateMeal(foodItemIds, null);
//
//        // Prepare the meal for saving and log the action
//        log.debug("Meal prepared for saving: {}", meal);
//        Meal savedMeal = mealRepository.save(meal);
//        log.info("Successfully created a new meal with ID: {}", savedMeal.getId());
//
//        // Convert saved Meal entity to DTO for the response
//        return mealMapper.toDTO(savedMeal);
//    }

//    /**
//     * Updates an existing Meal entity with new information.
//     * If the meal is a template (isTemplate = true), it checks to ensure no duplicate ingredient lists.
//     * The user relationship remains unchanged during this update.
//     *
//     * @param id           the ID of the meal to be updated.
//     * @param mealInputDTO the DTO containing the updated meal information.
//     * @return the updated MealDTO containing the new meal data.
//     * @throws EntityNotFoundException if the meal with the given ID is not found.
//     * @throws InvalidFoodItemException if any food item ID in the ingredients is invalid.
//     * @throws DuplicateMealException   if updating would create a duplicate template meal.
//     */
//    @Override
//    @Transactional
//    public MealDTO updateMeal(Long id, MealInputDTO mealInputDTO) {
//        log.info("Updating meal with ID: {}", id);
//        Meal existingMeal = mealRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + id));
//
//        // Duplicate check for template meals (isTemplate = true)
//        if (existingMeal.isTemplate() && mealInputDTO.getMealIngredients() != null) {
//            List<Long> foodItemIds = mealInputDTO.getMealIngredients().stream()
//                    .map(MealIngredientInputDTO::getFoodItemId)
//                    .toList();
//
//            // Use CheckForDuplicateTemplateMealUtil to check for duplicate template meals, passing the current meal ID
//            checkForDuplicateTemplateMeal.checkForDuplicateTemplateMeal(foodItemIds, id);
//        }
//
//        // Proceed with updating the meal fields
//        if (mealInputDTO.getName() != null) {
//            existingMeal.setName(mealInputDTO.getName());
//        }
//        if (mealInputDTO.getMealDescription() != null) {
//            existingMeal.setMealDescription(mealInputDTO.getMealDescription());
//        }
//
//        if (mealInputDTO.getMealIngredients() != null) {
//            // Clear existing ingredients only if we have a new list to replace them with
//            existingMeal.getMealIngredients().clear();
//            List<MealIngredient> updatedIngredients = mealInputDTO.getMealIngredients().stream()
//                    .map(inputDTO -> mealIngredientMapper.toEntity(inputDTO, existingMeal))
//                    .toList();
//            existingMeal.addMealIngredients(updatedIngredients);
//        }
//
//        Meal savedMeal = mealRepository.save(existingMeal);
//        log.info("Successfully updated meal with ID: {}", id);
//        return mealMapper.toDTO(savedMeal);
//    }

    /**
     * Retrieves all template Meals from the repository (isTemplate = true).
     *
     * @return a list of MealDTOs representing all template meals, or an empty list if no templates are found.
     */
    @Override
    @Transactional(readOnly = true)
    public List<MealDTO> getAllMeals() {
        log.info("Retrieving all template meals from the system.");
        List<Meal> templateMeals = mealRepository.findAllTemplateMeals(); // Only fetch meals with isTemplate = true
        if (templateMeals.isEmpty()) {
            log.info("No template meals found in the system.");
        } else {
            log.info("Found {} template meals in the system.", templateMeals.size());
        }
        return templateMeals.stream().map(mealMapper::toDTO).toList();
    }

    /**
     * Retrieves a Meal by its ID, only if it is a template.
     *
     * @param id The ID of the Meal.
     * @return The MealDTO.
     * @throws EntityNotFoundException If the meal with the given ID is not found,
     *                                 or if the meal is not a template.
     */
    @Override
    @Transactional(readOnly = true)
    public MealDTO getMealById(Long id) {
        log.info("Attempting to retrieve template meal with ID: {}", id);

        // Fetch the meal from the repository
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + id));

        // Ensure the meal is a template
        if (!meal.isTemplate()) {
            log.warn("Meal with ID {} is not marked as a template.", id);
            throw new EntityNotFoundException("Meal not found or not a template.");
        }

        // Map the Meal entity to a MealDTO
        MealDTO mealDTO = mealMapper.toDTO(meal);

        log.info("Successfully retrieved template meal with ID: {}", id);
        return mealDTO;
    }

//    /**
//     * Deletes a specific meal from the repository.
//     *
//     * @param mealId The ID of the meal to be deleted.
//     * @throws EntityNotFoundException if the meal with the given ID is not found.
//     */
//    @Override
//    @Transactional
//    public void deleteMeal(Long mealId) {
//        log.info("Attempting to delete meal with ID: {}", mealId);
//
//        // Retrieve the meal or throw an exception if not found
//        Meal meal = mealRepository.findById(mealId)
//                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + mealId));
//
//        // Loop through users associated with the meal and remove the association
//        List<User> associatedUsers = userRepository.findAllByMealsContaining(meal);
//        for (User user : associatedUsers) {
//            log.info("Removing association between User ID: {} and Meal ID: {}", user.getId(), meal.getId());
//            user.getMeals().remove(meal);
//        }
//
//        // Save updated users back to the database to ensure association is removed
//        userRepository.saveAll(associatedUsers);
//
//        // Delete the meal after cleaning up the relationships
//        mealRepository.delete(meal);
//        log.info("Successfully deleted meal with ID: {}", mealId);
//    }


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
}

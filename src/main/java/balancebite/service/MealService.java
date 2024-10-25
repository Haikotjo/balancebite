package balancebite.service;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.errorHandling.InvalidFoodItemException;
import balancebite.mapper.MealIngredientMapper;
import balancebite.mapper.MealMapper;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.User;
import balancebite.repository.FoodItemRepository;
import balancebite.repository.MealRepository;
import balancebite.repository.UserRepository;
import balancebite.utils.NutrientCalculatorUtil;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service class for managing Meal entities.
 * Handles the creation, retrieval, updating, and processing of Meal entities and their related data.
 */
@Service
public class MealService {

    private static final Logger log = LoggerFactory.getLogger(MealService.class);

    private final MealRepository mealRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;
    private final MealMapper mealMapper;
    private final MealIngredientMapper mealIngredientMapper;

    /**
     * Constructor for MealService, using constructor injection.
     *
     * @param mealRepository     the repository for managing Meal entities.
     * @param foodItemRepository the repository for managing FoodItem entities.
     * @param userRepository     the repository for managing User entities.
     * @param mealMapper         the mapper for converting Meal entities to DTOs.
     */
    public MealService(MealRepository mealRepository, FoodItemRepository foodItemRepository, UserRepository userRepository, MealMapper mealMapper, MealIngredientMapper mealIngredientMapper) {
        this.mealRepository = mealRepository;
        this.foodItemRepository = foodItemRepository;
        this.userRepository = userRepository;
        this.mealMapper = mealMapper;
        this.mealIngredientMapper = mealIngredientMapper;
    }

    /**
     * Creates a new Meal entity based on the provided MealInputDTO.
     * This method converts the input DTO to a Meal entity, persists it, and then converts the result back to a DTO.
     *
     * @param mealInputDTO the DTO containing the input data for creating a Meal.
     * @return the created MealDTO with the persisted meal information.
     * @throws InvalidFoodItemException if any food item in the input is invalid.
     */
    @Transactional
    public MealDTO createMeal(MealInputDTO mealInputDTO) {
        log.info("Attempting to create a new meal with name: {}", mealInputDTO.getName());
        try {
            Meal meal = mealMapper.toEntity(mealInputDTO);
            Meal savedMeal = mealRepository.save(meal);
            log.info("Successfully created a new meal with ID: {}", savedMeal.getId());
            return mealMapper.toDTO(savedMeal);
        } catch (InvalidFoodItemException e) {
            log.error("Failed to create meal due to invalid food item: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during meal creation: {}", e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred while creating the meal.");
        }
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
     */
    @Transactional
    public MealDTO createMealForUser(MealInputDTO mealInputDTO, Long userId) {
        log.info("Attempting to create a new meal for user ID: {}", userId);
        try {
            Meal meal = mealMapper.toEntity(mealInputDTO);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
            meal.setCreatedBy(user);
            meal.getUsers().add(user);
            Meal savedMeal = mealRepository.save(meal);
            log.info("Successfully created a new meal for user with ID: {}", userId);
            return mealMapper.toDTO(savedMeal);
        } catch (InvalidFoodItemException e) {
            log.error("Failed to create meal due to invalid food item: {}", e.getMessage());
            throw e;
        } catch (EntityNotFoundException e) {
            log.error("User not found while creating meal for user ID {}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during meal creation for user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred while creating the meal for user.");
        }
    }

    /**
     * Updates an existing Meal entity with new information.
     * This method updates the meal's name and ingredients based on the provided MealInputDTO.
     * The user relationship remains unchanged during this update.
     *
     * @param id           the ID of the meal to be updated.
     * @param mealInputDTO the DTO containing the updated meal information.
     * @return the updated MealDTO containing the new meal data.
     * @throws EntityNotFoundException if the meal with the given ID is not found.
     * @throws InvalidFoodItemException if any food item ID in the ingredients is invalid.
     */
    @Transactional
    public MealDTO updateMeal(Long id, MealInputDTO mealInputDTO) {
        log.info("Updating meal with ID: {}", id);
        Meal existingMeal = mealRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + id));

        existingMeal.setName(mealInputDTO.getName());
        existingMeal.getMealIngredients().clear();
        List<MealIngredient> updatedIngredients = mealInputDTO.getMealIngredients().stream()
                .map(inputDTO -> mealIngredientMapper.toEntity(inputDTO, existingMeal))
                .toList();
        existingMeal.addMealIngredients(updatedIngredients);

        Meal savedMeal = mealRepository.save(existingMeal);
        log.info("Successfully updated meal with ID: {}", id);
        return mealMapper.toDTO(savedMeal);
    }

    /**
     * Retrieves all Meals from the repository.
     *
     * @return a list of MealDTOs, or an empty list if no meals are found.
     */
    @Transactional(readOnly = true)
    public List<MealDTO> getAllMeals() {
        log.info("Retrieving all meals from the system.");
        List<Meal> meals = mealRepository.findAll();
        if (meals.isEmpty()) {
            log.info("No meals found in the system.");
        } else {
            log.info("Found {} meals in the system.", meals.size());
        }
        return meals.stream().map(mealMapper::toDTO).toList();
    }

    /**
     * Retrieves a Meal by its ID.
     *
     * @param id the ID of the Meal.
     * @return the MealDTO.
     * @throws EntityNotFoundException if the meal with the given ID is not found.
     */
    @Transactional(readOnly = true)
    public MealDTO getMealById(Long id) {
        log.info("Retrieving meal with ID: {}", id);
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + id));
        log.info("Successfully retrieved meal with ID: {}", id);
        return mealMapper.toDTO(meal);
    }

    /**
     * Deletes a specific meal from the repository.
     *
     * @param mealId The ID of the meal to be deleted.
     * @throws EntityNotFoundException if the meal with the given ID is not found.
     */
    @Transactional
    public void deleteMeal(Long mealId) {
        log.info("Attempting to delete meal with ID: {}", mealId);
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + mealId));

        mealRepository.delete(meal);
        log.info("Successfully deleted meal with ID: {}", mealId);
    }

    /**
     * Retrieves the total nutrients for a given Meal by its ID.
     *
     * @param mealId the ID of the Meal.
     * @return a map of nutrient names and their corresponding total values for the meal.
     * @throws EntityNotFoundException if the meal with the given ID is not found.
     */
    public Map<String, NutrientInfoDTO> calculateNutrients(Long mealId) {
        log.info("Calculating total nutrients for meal with ID: {}", mealId);
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + mealId));
        return NutrientCalculatorUtil.calculateTotalNutrients(meal.getMealIngredients());
    }

    /**
     * Retrieves the nutrients per food item for a given Meal by its ID.
     *
     * @param mealId the ID of the Meal.
     * @return a map of food item IDs to nutrient maps, where each map contains nutrient names and their values.
     * @throws EntityNotFoundException if the meal with the given ID is not found.
     */
    public Map<Long, Map<String, NutrientInfoDTO>> calculateNutrientsPerFoodItem(Long mealId) {
        log.info("Calculating nutrients per food item for meal with ID: {}", mealId);
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + mealId));
        return NutrientCalculatorUtil.calculateNutrientsPerFoodItem(meal.getMealIngredients());
    }
}

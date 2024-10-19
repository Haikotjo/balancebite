package balancebite.service;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.NutrientInfoDTO;
import balancebite.exceptions.InvalidFoodItemException;
import balancebite.mapper.MealMapper;
import balancebite.model.FoodItem;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.User;
import balancebite.repository.FoodItemRepository;
import balancebite.repository.MealRepository;
import balancebite.repository.UserRepository;
import balancebite.utils.NutrientCalculatorUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.*;

/**
 * Service class for managing Meal entities.
 * Handles the creation, retrieval, and processing of Meal entities and their related data.
 */
@Service
public class MealService {

    private final MealRepository mealRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;
    private final MealMapper mealMapper;

    /**
     * Constructor for MealService, using constructor injection.
     *
     * @param mealRepository     the repository for managing Meal entities.
     * @param foodItemRepository the repository for managing FoodItem entities.
     * @param userRepository     the repository for managing User entities.
     * @param mealMapper         the mapper for converting Meal entities to DTOs.
     */
    public MealService(MealRepository mealRepository, FoodItemRepository foodItemRepository, UserRepository userRepository, MealMapper mealMapper) {
        this.mealRepository = mealRepository;
        this.foodItemRepository = foodItemRepository;
        this.userRepository = userRepository;
        this.mealMapper = mealMapper;
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
        Meal meal = mealMapper.toEntity(mealInputDTO);
        Meal savedMeal = mealRepository.save(meal);
        return mealMapper.toDTO(savedMeal);
    }

    /**
     * Creates a new Meal entity for a specific user based on the provided MealInputDTO.
     * This method converts the input DTO to a Meal entity, associates it with a user, persists it, and then converts the result back to a DTO.
     *
     * @param mealInputDTO The DTO containing the input data for creating a Meal.
     * @param userId The ID of the user to whom the meal will be associated.
     * @return The created MealDTO with the persisted meal information.
     * @throws InvalidFoodItemException if any food item in the input is invalid.
     * @throws EntityNotFoundException if the user cannot be found.
     */
    @Transactional
    public MealDTO createMealForUser(MealInputDTO mealInputDTO, Long userId) {
        Meal meal = mealMapper.toEntity(mealInputDTO);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        meal.setCreatedBy(user);
        meal.getUsers().add(user);
        Meal savedMeal = mealRepository.save(meal);
        return mealMapper.toDTO(savedMeal);
    }

    /**
     * Adds an existing meal to the list of meals for a specific user.
     * This method associates the specified meal with the user but does not allow modifications to the meal.
     *
     * @param mealId The ID of the meal to be added.
     * @param userId The ID of the user who wants to add the meal.
     * @throws EntityNotFoundException if the meal or user is not found.
     */
    @Transactional
    public void addMealToUser(Long mealId, Long userId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + mealId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        user.getMeals().add(meal);
        userRepository.save(user);
    }

    /**
     * Updates an existing Meal entity with new information.
     * This method updates the meal's name and ingredients based on the provided MealInputDTO.
     * The user relationship remains unchanged during this update.
     *
     * @param id the ID of the meal to be updated
     * @param mealInputDTO the DTO containing the updated meal information
     * @return the updated MealDTO containing the new meal data
     * @throws EntityNotFoundException if the meal with the given ID is not found
     * @throws InvalidFoodItemException if any food item ID in the ingredients is invalid
     */
    @Transactional
    public MealDTO updateMeal(Long id, MealInputDTO mealInputDTO) {
        Meal existingMeal = mealRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + id));
        existingMeal.setName(mealInputDTO.getName());
        existingMeal.getMealIngredients().clear();
        List<MealIngredient> updatedIngredients = mealInputDTO.getMealIngredients().stream().map(inputDTO -> {
            FoodItem foodItem = foodItemRepository.findById(inputDTO.getFoodItemId())
                    .orElseThrow(() -> new InvalidFoodItemException("Invalid food item ID: " + inputDTO.getFoodItemId()));
            double quantity = inputDTO.getQuantity() == null || inputDTO.getQuantity() == 0.0
                    ? foodItem.getGramWeight()
                    : inputDTO.getQuantity();
            return new MealIngredient(existingMeal, foodItem, quantity);
        }).toList();
        existingMeal.addMealIngredients(updatedIngredients);
        Meal savedMeal = mealRepository.save(existingMeal);
        return mealMapper.toDTO(savedMeal);
    }

    /**
     * Retrieves all Meals from the repository.
     *
     * @return a list of MealDTOs.
     */
    @Transactional(readOnly = true)
    public List<MealDTO> getAllMeals() {
        List<Meal> meals = mealRepository.findAll();
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
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + id));
        return mealMapper.toDTO(meal);
    }

    /**
     * Deletes a specific meal from a user's list.
     * This method checks if the meal exists, if the user exists, and if the user has permission to delete the meal.
     *
     * @param userId The ID of the user requesting the deletion.
     * @param mealId The ID of the meal to be deleted from the user's list.
     * @throws EntityNotFoundException if the user or meal is not found.
     */
    @Transactional
    public void deleteUserMeal(Long userId, Long mealId) {
        // Retrieve the meal by its ID, throw exception if not found
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + mealId));

        // Retrieve the user by their ID, throw exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        // Check if the meal belongs to the user
        // Commenting out AccessDeniedException until proper authorization mechanism is implemented.
        // Uncomment this once access validation has been properly set up, e.g. using Spring Security.
        /*
        if (!meal.getUsers().contains(user)) {
            throw new AccessDeniedException("User does not have permission to delete this meal.");
        }
        */

        // Remove the meal from the user's list and save the changes to the repository
        user.getMeals().remove(meal);
        userRepository.save(user);
    }


    /**
     * Deletes a specific meal from the repository.
     * This operation should be restricted to administrative users only.
     *
     * @param mealId The ID of the meal to be deleted.
     * @throws EntityNotFoundException if the meal with the given ID is not found.
     */
    @Transactional
    public void deleteMeal(Long mealId) {
        // Retrieve the meal by its ID, throw exception if not found
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + mealId));

        // Delete the meal from the repository
        mealRepository.delete(meal);
    }


    /**
     * Retrieves the total nutrients for a given Meal by its ID.
     *
     * @param mealId the ID of the Meal.
     * @return a map of nutrient names and their corresponding total values for the meal.
     * @throws EntityNotFoundException if the meal with the given ID is not found.
     */
    public Map<String, NutrientInfoDTO> calculateNutrients(Long mealId) {
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
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found with ID: " + mealId));
        return NutrientCalculatorUtil.calculateNutrientsPerFoodItem(meal.getMealIngredients());
    }
}

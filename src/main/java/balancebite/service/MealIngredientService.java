package balancebite.service;

import balancebite.dto.mealingredient.MealIngredientInputDTO;
import balancebite.mapper.MealIngredientMapper;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.repository.MealIngredientRepository;
import balancebite.repository.MealRepository;
import balancebite.service.interfaces.meal.IMealIngredientService;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing meal ingredients.
 */
@Service
public class MealIngredientService implements IMealIngredientService {

    private final MealIngredientRepository mealIngredientRepository;
    private final MealRepository mealRepository;
    private final MealIngredientMapper mealIngredientMapper;

    /**
     * Constructor for MealIngredientService, using constructor injection
     * for better testability and clear dependency management.
     *
     * @param mealIngredientRepository the repository for managing meal ingredients.
     * @param mealRepository the repository for managing meals.
     * @param mealIngredientMapper the mapper for converting DTOs to entities.
     */
    public MealIngredientService(MealIngredientRepository mealIngredientRepository,
                                 MealRepository mealRepository,
                                 MealIngredientMapper mealIngredientMapper) {
        this.mealIngredientRepository = mealIngredientRepository;
        this.mealRepository = mealRepository;
        this.mealIngredientMapper = mealIngredientMapper;
    }

    /**
     * Adds a meal ingredient to a specific meal.
     *
     * @param mealId the ID of the meal to which the ingredient should be added.
     * @param inputDTO the DTO containing the data of the meal ingredient to be added.
     */
    @Override
    public void addMealIngredient(Long mealId, MealIngredientInputDTO inputDTO) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid meal ID: " + mealId));
        MealIngredient mealIngredient = mealIngredientMapper.toEntity(inputDTO, meal);
        mealIngredientRepository.save(mealIngredient);
    }
}

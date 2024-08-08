package balancebite.service;

import balancebite.dto.MealIngredientInputDTO;
import balancebite.mapper.MealIngredientMapper;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.repository.MealIngredientRepository;
import balancebite.repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MealIngredientService {

    @Autowired
    private MealIngredientRepository mealIngredientRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private MealIngredientMapper mealIngredientMapper;

    public void addMealIngredient(Long mealId, MealIngredientInputDTO inputDTO) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid meal ID: " + mealId));
        MealIngredient mealIngredient = mealIngredientMapper.toEntity(inputDTO, meal);
        mealIngredientRepository.save(mealIngredient);
    }
}

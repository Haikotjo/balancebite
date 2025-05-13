package balancebite.mapper;

import balancebite.dto.NutrientInfoDTO;
import balancebite.dto.diet.DietDayDTO;
import balancebite.dto.diet.DietDayInputDTO;
import balancebite.dto.meal.MealDTO;
import balancebite.model.diet.DietDay;
import balancebite.model.meal.Meal;
import balancebite.model.meal.references.Diet;
import balancebite.utils.NutrientCalculatorUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class DietDayMapper {

    private final MealMapper mealMapper;

    public DietDayMapper(MealMapper mealMapper) {
        this.mealMapper = mealMapper;
    }

    public DietDayDTO toDTO(DietDay dietDay) {
        if (dietDay == null) return null;

        List<MealDTO> mealDTOs = dietDay.getMeals().stream()
                .map(mealMapper::toDTO)
                .toList();

        Map<String, NutrientInfoDTO> nutrients = NutrientCalculatorUtil.calculateTotalNutrients(
                dietDay.getAllMealIngredients()
        );

        return new DietDayDTO(
                dietDay.getId(),
                dietDay.getDayLabel(),
                dietDay.getDate(),
                mealDTOs,
                nutrients,
                dietDay.getDietDayDescription(),
                dietDay.getDiets()
        );
    }

    public DietDay toEntity(DietDayInputDTO input, Set<Meal> meals, int index) {
        if (input == null || meals == null) return null;

        DietDay entity = new DietDay();
        String label = (input.getDayLabel() != null && !input.getDayLabel().isBlank())
                ? input.getDayLabel()
                : "Day " + (index + 1);
        entity.setDayLabel(label);
        entity.setDate(input.getDate());
        entity.setMeals(meals.stream().toList());
        entity.setDietDayDescription(input.getDietDayDescription());
        entity.setDiets(input.getDiets());

        return entity;
    }

    public void updateFromInputDTO(DietDay dietDay, DietDayInputDTO input, Set<Meal> meals, int index) {
        if (dietDay == null || input == null || meals == null) return;

        String label = (input.getDayLabel() != null && !input.getDayLabel().isBlank())
                ? input.getDayLabel()
                : "Day " + (index + 1);
        dietDay.setDayLabel(label);
        dietDay.setDate(input.getDate());
        dietDay.setMeals(meals.stream().toList());
        dietDay.setDietDayDescription(input.getDietDayDescription());
        dietDay.setDiets(input.getDiets());
    }
}

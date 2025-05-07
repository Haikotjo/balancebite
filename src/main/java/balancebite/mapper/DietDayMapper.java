package balancebite.mapper;

import balancebite.dto.NutrientInfoDTO;
import balancebite.dto.diet.DietDayDTO;
import balancebite.dto.diet.DietDayInputDTO;
import balancebite.dto.meal.MealDTO;
import balancebite.model.diet.DietDay;
import balancebite.model.meal.Meal;
import balancebite.utils.NutrientCalculatorUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
                nutrients
        );
    }

    public DietDay toEntity(DietDayInputDTO input, Set<Meal> meals, int index) {
        if (input == null || meals == null) return null;

        DietDay entity = new DietDay();
        entity.setDayLabel("Day " + (index + 1)); // Automatisch label
        entity.setDate(input.getDate());
        entity.setMeals(meals.stream().toList());

        return entity;
    }

    public void updateFromInputDTO(DietDay dietDay, DietDayInputDTO input, Set<Meal> meals, int index) {
        if (dietDay == null || input == null || meals == null) return;

        dietDay.setDayLabel("Day " + (index + 1)); // Zorg dat dit ook klopt bij updates
        dietDay.setDate(input.getDate());
        dietDay.setMeals(meals.stream().toList());
    }
}

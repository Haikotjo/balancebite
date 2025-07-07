package balancebite.mapper;

import balancebite.dto.diet.DietDayDTO;
import balancebite.dto.diet.DietDayInputDTO;
import balancebite.dto.meal.MealDTO;
import balancebite.model.diet.DietDay;
import balancebite.model.meal.Meal;
import org.springframework.stereotype.Component;

import java.util.List;

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

        return new DietDayDTO(
                dietDay.getId(),
                dietDay.getDayLabel(),
                dietDay.getDate(),
                mealDTOs,
                dietDay.getDietDayDescription(),
                dietDay.getDiets(),
                dietDay.getTotalCalories(),
                dietDay.getTotalProtein(),
                dietDay.getTotalCarbs(),
                dietDay.getTotalFat(),
                dietDay.getTotalSaturatedFat(),
                dietDay.getTotalUnsaturatedFat(),
                dietDay.getTotalSugars()
        );
    }

    // Belangrijk: gebruik List<Meal> i.p.v. Set<Meal> zodat dubbele maaltijden kunnen
    public DietDay toEntity(DietDayInputDTO input, List<Meal> meals, int index) {
        if (input == null || meals == null) return null;

        DietDay entity = new DietDay();
        String label = (input.getDayLabel() != null && !input.getDayLabel().isBlank())
                ? input.getDayLabel()
                : "Day " + (index + 1);
        entity.setDayLabel(label);
        entity.setDate(input.getDate());
        entity.setMeals(meals);
        entity.setDietDayDescription(input.getDietDayDescription());
        entity.setDiets(input.getDiets());

        // 🔁 Bereken en zet de totalen
        entity.updateNutrients();

        return entity;
    }

    public void updateFromInputDTO(DietDay dietDay, DietDayInputDTO input, List<Meal> meals, int index) {
        if (dietDay == null || input == null || meals == null) return;

        String label = (input.getDayLabel() != null && !input.getDayLabel().isBlank())
                ? input.getDayLabel()
                : "Day " + (index + 1);
        dietDay.setDayLabel(label);
        dietDay.setDate(input.getDate());
        dietDay.setMeals(meals);
        dietDay.setDietDayDescription(input.getDietDayDescription());
        dietDay.setDiets(input.getDiets());

        // 🔁 Herbereken nutrienten
        dietDay.updateNutrients();
    }
}

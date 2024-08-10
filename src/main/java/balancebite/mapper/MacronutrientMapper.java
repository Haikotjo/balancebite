package balancebite.mapper;

import balancebite.dto.MacronutrientDTO;
import balancebite.model.Meal;

public class MacronutrientMapper {

    public static MacronutrientDTO toDTO(Meal meal) {
        // Hier neem je de waarden die in NutrientCalculator zijn berekend
        return new MacronutrientDTO(
                meal.getProteins(),  // Deze waardes zou je moeten doorgeven vanuit de service
                meal.getCarbohydrates(),
                meal.getFats(),
                meal.getKcals()
        );
    }
}

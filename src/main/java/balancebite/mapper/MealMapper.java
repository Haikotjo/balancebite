package balancebite.mapper;

import balancebite.dto.MealDTO;
import balancebite.dto.MealIngredientDTO;
import balancebite.dto.VitaminsAndMineralsDTO;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.VitaminsAndMinerals;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MealMapper {

    public MealDTO toDTO(Meal meal) {
        if (meal == null) {
            return null;
        }

        // Maak een VitaminsAndMineralsDTO aan op basis van de gegevens in de Meal-entiteit
        VitaminsAndMineralsDTO vitaminsAndMineralsDTO = null;
        if (meal.getVitaminsAndMinerals() != null) {
            VitaminsAndMinerals vitaminsAndMinerals = meal.getVitaminsAndMinerals();
            vitaminsAndMineralsDTO = new VitaminsAndMineralsDTO();
            vitaminsAndMineralsDTO.setVitaminA(vitaminsAndMinerals.getVitaminA());
            vitaminsAndMineralsDTO.setVitaminC(vitaminsAndMinerals.getVitaminC());
            vitaminsAndMineralsDTO.setVitaminD(vitaminsAndMinerals.getVitaminD());
            vitaminsAndMineralsDTO.setVitaminE(vitaminsAndMinerals.getVitaminE());
            vitaminsAndMineralsDTO.setVitaminK(vitaminsAndMinerals.getVitaminK());
            vitaminsAndMineralsDTO.setThiamin(vitaminsAndMinerals.getThiamin());
            vitaminsAndMineralsDTO.setRiboflavin(vitaminsAndMinerals.getRiboflavin());
            vitaminsAndMineralsDTO.setNiacin(vitaminsAndMinerals.getNiacin());
            vitaminsAndMineralsDTO.setVitaminB6(vitaminsAndMinerals.getVitaminB6());
            vitaminsAndMineralsDTO.setFolate(vitaminsAndMinerals.getFolate());
            vitaminsAndMineralsDTO.setVitaminB12(vitaminsAndMinerals.getVitaminB12());
            vitaminsAndMineralsDTO.setPantothenicAcid(vitaminsAndMinerals.getPantothenicAcid());
            vitaminsAndMineralsDTO.setBiotin(vitaminsAndMinerals.getBiotin());
            vitaminsAndMineralsDTO.setCholine(vitaminsAndMinerals.getCholine());
            vitaminsAndMineralsDTO.setCalcium(vitaminsAndMinerals.getCalcium());
            vitaminsAndMineralsDTO.setIron(vitaminsAndMinerals.getIron());
            vitaminsAndMineralsDTO.setMagnesium(vitaminsAndMinerals.getMagnesium());
            vitaminsAndMineralsDTO.setPhosphorus(vitaminsAndMinerals.getPhosphorus());
            vitaminsAndMineralsDTO.setPotassium(vitaminsAndMinerals.getPotassium());
            vitaminsAndMineralsDTO.setSodium(vitaminsAndMinerals.getSodium());
            vitaminsAndMineralsDTO.setZinc(vitaminsAndMinerals.getZinc());
            vitaminsAndMineralsDTO.setCopper(vitaminsAndMinerals.getCopper());
            vitaminsAndMineralsDTO.setManganese(vitaminsAndMinerals.getManganese());
            vitaminsAndMineralsDTO.setSelenium(vitaminsAndMinerals.getSelenium());
            vitaminsAndMineralsDTO.setFluoride(vitaminsAndMinerals.getFluoride());
            vitaminsAndMineralsDTO.setChromium(vitaminsAndMinerals.getChromium());
            vitaminsAndMineralsDTO.setIodine(vitaminsAndMinerals.getIodine());
            vitaminsAndMineralsDTO.setMolybdenum(vitaminsAndMinerals.getMolybdenum());
        }

        // Zet de Meal-entiteit om naar MealDTO
        return new MealDTO(
                meal.getId(),
                meal.getName(),
                meal.getMealIngredients().stream()
                        .map(ingredient -> new MealIngredientDTO(
                                ingredient.getId(),
                                meal.getId(),
                                ingredient.getFoodItem() != null ? ingredient.getFoodItem().getId() : null,
                                ingredient.getQuantity()
                        ))
                        .collect(Collectors.toList()),
                meal.getProteins(),
                meal.getCarbohydrates(),
                meal.getFats(),
                meal.getKcals(),
                vitaminsAndMineralsDTO
        );
    }
}

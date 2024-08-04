package balancebite.mapper;

import balancebite.dto.FoodItemInputDTO;
import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.model.FoodItem;
import org.springframework.stereotype.Component;

@Component
public class FoodItemMapper {

    public FoodItemInputDTO toInputDto(UsdaFoodResponseDTO usdaFoodResponse) {
        FoodItemInputDTO foodItemInputDTO = new FoodItemInputDTO();
        foodItemInputDTO.setName(usdaFoodResponse.getDescription());

        for (UsdaFoodResponseDTO.FoodNutrientDTO nutrient : usdaFoodResponse.getFoodNutrients()) {
            if (nutrient.getNutrient() != null && nutrient.getNutrient().getName() != null) {
                mapNutrientToInputDTO(nutrient, foodItemInputDTO);
            }
        }
        return foodItemInputDTO;
    }

    private void mapNutrientToInputDTO(UsdaFoodResponseDTO.FoodNutrientDTO nutrient, FoodItemInputDTO foodItemInputDTO) {
        String nutrientName = nutrient.getNutrient().getName();
        double amount = nutrient.getAmount();

        switch (nutrientName) {
            case "Energy":
                foodItemInputDTO.setCalories((int) amount);
                break;
            case "Protein":
                foodItemInputDTO.setProtein((int) amount);
                break;
            case "Total lipid (fat)":
                foodItemInputDTO.setFat((int) amount);
                break;
            case "Carbohydrate, by difference":
                foodItemInputDTO.setCarbs((int) amount);
                break;
            case "Vitamin A, RAE":
                foodItemInputDTO.setVitaminA((int) amount);
                break;
            case "Vitamin C, total ascorbic acid":
                foodItemInputDTO.setVitaminC((int) amount);
                break;
            case "Vitamin D (D2 + D3)":
                foodItemInputDTO.setVitaminD((int) amount);
                break;
            case "Vitamin E (alpha-tocopherol)":
                foodItemInputDTO.setVitaminE((int) amount);
                break;
            case "Vitamin K (phylloquinone)":
                foodItemInputDTO.setVitaminK((int) amount);
                break;
            case "Thiamin":
                foodItemInputDTO.setThiamine((int) amount);
                break;
            case "Riboflavin":
                foodItemInputDTO.setRiboflavine((int) amount);
                break;
            case "Niacin":
                foodItemInputDTO.setNiacine((int) amount);
                break;
            case "Vitamin B-6":
                foodItemInputDTO.setVitaminB6((int) amount);
                break;
            case "Folate, total":
                foodItemInputDTO.setFoliumzuur((int) amount);
                break;
            case "Vitamin B-12":
                foodItemInputDTO.setVitaminB12((int) amount);
                break;
            case "Pantothenic acid":
                foodItemInputDTO.setPantotheenzuur((int) amount);
                break;
            case "Biotin":
                foodItemInputDTO.setBiotine((int) amount);
                break;
            case "Calcium, Ca":
                foodItemInputDTO.setCalcium((int) amount);
                break;
            case "Iron, Fe":
                foodItemInputDTO.setIron((int) amount);
                break;
            case "Magnesium, Mg":
                foodItemInputDTO.setMagnesium((int) amount);
                break;
            case "Phosphorus, P":
                foodItemInputDTO.setPhosphorus((int) amount);
                break;
            case "Potassium, K":
                foodItemInputDTO.setPotassium((int) amount);
                break;
            case "Sodium, Na":
                foodItemInputDTO.setSodium((int) amount);
                break;
            case "Zinc, Zn":
                foodItemInputDTO.setZinc((int) amount);
                break;
            case "Copper, Cu":
                foodItemInputDTO.setCopper((int) amount);
                break;
            case "Manganese, Mn":
                foodItemInputDTO.setManganese((int) amount);
                break;
            case "Selenium, Se":
                foodItemInputDTO.setSelenium((int) amount);
                break;
            // Voeg hier andere voedingsstoffen toe indien nodig
        }
    }

    public FoodItem toEntity(FoodItemInputDTO foodItemInputDTO) {
        FoodItem foodItem = new FoodItem();
        foodItem.setName(foodItemInputDTO.getName());
        foodItem.setCalories(foodItemInputDTO.getCalories());
        foodItem.setProtein(foodItemInputDTO.getProtein());
        foodItem.setFat(foodItemInputDTO.getFat());
        foodItem.setCarbs(foodItemInputDTO.getCarbs());
        foodItem.setVitaminA(foodItemInputDTO.getVitaminA());
        foodItem.setVitaminC(foodItemInputDTO.getVitaminC());
        foodItem.setVitaminD(foodItemInputDTO.getVitaminD());
        foodItem.setVitaminE(foodItemInputDTO.getVitaminE());
        foodItem.setVitaminK(foodItemInputDTO.getVitaminK());
        foodItem.setThiamine(foodItemInputDTO.getThiamine());
        foodItem.setRiboflavine(foodItemInputDTO.getRiboflavine());
        foodItem.setNiacine(foodItemInputDTO.getNiacine());
        foodItem.setVitaminB6(foodItemInputDTO.getVitaminB6());
        foodItem.setFoliumzuur(foodItemInputDTO.getFoliumzuur());
        foodItem.setVitaminB12(foodItemInputDTO.getVitaminB12());
        foodItem.setPantotheenzuur(foodItemInputDTO.getPantotheenzuur());
        foodItem.setBiotine(foodItemInputDTO.getBiotine());
        foodItem.setCalcium(foodItemInputDTO.getCalcium());
        foodItem.setIron(foodItemInputDTO.getIron());
        foodItem.setMagnesium(foodItemInputDTO.getMagnesium());
        foodItem.setPhosphorus(foodItemInputDTO.getPhosphorus());
        foodItem.setPotassium(foodItemInputDTO.getPotassium());
        foodItem.setSodium(foodItemInputDTO.getSodium());
        foodItem.setZinc(foodItemInputDTO.getZinc());
        foodItem.setCopper(foodItemInputDTO.getCopper());
        foodItem.setManganese(foodItemInputDTO.getManganese());
        foodItem.setSelenium(foodItemInputDTO.getSelenium());
        return foodItem;
    }
}

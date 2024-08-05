package balancebite.mapper;

import balancebite.dto.NutrientAPIDTO;
import balancebite.dto.NutrientInputDTO;
import balancebite.dto.NutrientDTO;
import balancebite.dto.NutrientAPIDTO.FoodNutrientDTO;
import balancebite.model.Nutrient;

public class NutrientMapper {

    public static Nutrient toEntity(NutrientInputDTO inputDTO) {
        if (inputDTO == null) {
            return null;
        }
        return new Nutrient(
                inputDTO.getNutrientName(),
                inputDTO.getValue(),
                inputDTO.getUnitName()
        );
    }

    public static NutrientDTO toDTO(Nutrient nutrient) {
        if (nutrient == null) {
            return null;
        }
        return new NutrientDTO(
                nutrient.getId(),
                nutrient.getNutrientName(),
                nutrient.getValue(),
                nutrient.getUnitName()
        );
    }

    public static Nutrient toEntity(NutrientAPIDTO.FoodNutrientDTO nutrientDTO) {
        if (nutrientDTO == null || nutrientDTO.getNutrient() == null) {
            return null;
        }
        return new Nutrient(
                nutrientDTO.getNutrient().getName(),
                nutrientDTO.getAmount(),
                nutrientDTO.getUnitName()
        );
    }
}

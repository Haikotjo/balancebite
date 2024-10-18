package balancebite.service;

import balancebite.dto.UsdaFoodResponseDTO;
import java.util.List;

public interface UsdaApiServiceInterface {
    UsdaFoodResponseDTO getFoodData(String fdcId);
    List<UsdaFoodResponseDTO> getMultipleFoodData(List<String> fdcIds);
}

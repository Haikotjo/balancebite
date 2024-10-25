package balancebite.service.interfaces;

import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.errorHandling.UsdaApiException;
import java.util.List;

/**
 * Interface for interacting with the USDA FoodData Central API.
 * Defines methods to fetch food data for single or multiple food items.
 */
public interface IUsdaApiService {

    /**
     * Fetches food data from the USDA API for a single food item identified by its FDC ID.
     *
     * @param fdcId The FoodData Central ID of the food item to fetch.
     * @return The UsdaFoodResponseDTO containing the food data.
     * @throws UsdaApiException if the HTTP request fails or the response is invalid.
     */
    UsdaFoodResponseDTO getFoodData(String fdcId);

    /**
     * Fetches food data from the USDA API for multiple food items identified by their FDC IDs.
     *
     * @param fdcIds The list of FoodData Central IDs of the food items to fetch.
     * @return A list of UsdaFoodResponseDTO objects containing the food data.
     * @throws UsdaApiException if the HTTP request fails or the response is invalid.
     */
    List<UsdaFoodResponseDTO> getMultipleFoodData(List<String> fdcIds);
}

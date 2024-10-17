package balancebite.service;

import balancebite.config.ApiConfig;
import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.exception.UsdaApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for interacting with the USDA FoodData Central API.
 * Provides methods to fetch food data by FDC ID and handle HTTP responses.
 */
@Service
public class UsdaApiService {

    private final ApiConfig apiConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public UsdaApiService(ApiConfig apiConfig, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.apiConfig = apiConfig;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Helper method to build the URL dynamically.
     *
     * @param path The specific API path to append to the base URL.
     * @return The complete URL with the API key.
     */
    private String buildUrl(String path) {
        return String.format("https://api.nal.usda.gov/fdc/v1/%s?api_key=%s", path, apiConfig.getUsdaApiKey());
    }

    /**
     * Fetches food data from the USDA API for a single food item identified by its FDC ID.
     *
     * @param fdcId The FoodData Central ID of the food item to fetch.
     * @return The UsdaFoodResponseDTO containing the food data.
     * @throws UsdaApiException if the HTTP request fails or the response is invalid.
     */
    public UsdaFoodResponseDTO getFoodData(String fdcId) {
        try {
            // Construct the URL using the helper method
            String url = buildUrl("food/" + fdcId);

            // Send the HTTP GET request
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

            // Validate the response status
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new UsdaApiException("Error occurred: " + responseEntity.getStatusCode());
            }

            // Convert JSON response to UsdaFoodResponseDTO
            return objectMapper.readValue(responseEntity.getBody(), UsdaFoodResponseDTO.class);

        } catch (RestClientException e) {
            throw new UsdaApiException("Failed to fetch food data from USDA API for FDC ID: " + fdcId, e);
        } catch (Exception e) {
            throw new UsdaApiException("An unexpected error occurred while processing USDA API response", e);
        }
    }

    /**
     * Fetches food data from the USDA API for multiple food items identified by their FDC IDs.
     *
     * @param fdcIds The list of FoodData Central IDs of the food items to fetch.
     * @return A list of UsdaFoodResponseDTO objects containing the food data.
     * @throws UsdaApiException if the HTTP request fails or the response is invalid.
     */
    public List<UsdaFoodResponseDTO> getMultipleFoodData(List<String> fdcIds) {
        try {
            // Construct the URL using the helper method
            String url = buildUrl("foods");

            // Prepare the request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("fdcIds", fdcIds);

            // Send the HTTP POST request
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestBody, String.class);

            // Validate the response status
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new UsdaApiException("Error occurred: " + responseEntity.getStatusCode());
            }

            // Convert JSON response to a list of UsdaFoodResponseDTO
            return Arrays.asList(objectMapper.readValue(responseEntity.getBody(), UsdaFoodResponseDTO[].class));

        } catch (RestClientException e) {
            throw new UsdaApiException("Failed to fetch multiple food data from USDA API", e);
        } catch (Exception e) {
            throw new UsdaApiException("An unexpected error occurred while processing USDA API response", e);
        }
    }
}

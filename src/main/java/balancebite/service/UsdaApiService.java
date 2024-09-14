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

    /**
     * Constructor for dependency injection.
     *
     * @param apiConfig     Configuration class for API settings such as API key.
     * @param restTemplate  Spring's RestTemplate for making HTTP requests.
     * @param objectMapper  ObjectMapper for JSON processing.
     */
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
        return "https://api.nal.usda.gov/fdc/v1/" + path + "?api_key=" + apiConfig.getUsdaApiKey();
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
            // Gebruik de helper-methode voor de URL-opbouw
            String url = buildUrl("food/" + fdcId);

            // Verzend het HTTP GET-verzoek
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

            // Controleer of de HTTP status succesvol is (2xx)
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                if (responseEntity.getStatusCode().is4xxClientError()) {
                    throw new UsdaApiException("Client error occurred: " + responseEntity.getStatusCode());
                } else if (responseEntity.getStatusCode().is5xxServerError()) {
                    throw new UsdaApiException("Server error occurred: " + responseEntity.getStatusCode());
                } else {
                    throw new UsdaApiException("Unexpected error: " + responseEntity.getStatusCode());
                }
            }

            // JSON-antwoord omzetten naar UsdaFoodResponseDTO
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
            // Gebruik de helper-methode voor de URL-opbouw
            String url = buildUrl("foods");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("fdcIds", fdcIds);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestBody, String.class);

            // Controleer of de HTTP status succesvol is (2xx)
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                if (responseEntity.getStatusCode().is4xxClientError()) {
                    throw new UsdaApiException("Client error occurred: " + responseEntity.getStatusCode());
                } else if (responseEntity.getStatusCode().is5xxServerError()) {
                    throw new UsdaApiException("Server error occurred: " + responseEntity.getStatusCode());
                } else {
                    throw new UsdaApiException("Unexpected error: " + responseEntity.getStatusCode());
                }
            }

            // JSON-antwoord omzetten naar een lijst van UsdaFoodResponseDTO
            return Arrays.asList(objectMapper.readValue(responseEntity.getBody(), UsdaFoodResponseDTO[].class));

        } catch (RestClientException e) {
            throw new UsdaApiException("Failed to fetch multiple food data from USDA API", e);
        } catch (Exception e) {
            throw new UsdaApiException("An unexpected error occurred while processing USDA API response", e);
        }
    }
}

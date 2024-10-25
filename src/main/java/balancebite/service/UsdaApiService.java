package balancebite.service;

import balancebite.config.ApiConfig;
import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.errorHandling.UsdaApiException;
import balancebite.service.interfaces.IUsdaApiService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for interacting with the USDA FoodData Central API.
 * Provides methods to fetch food data by FDC ID and handle HTTP responses.
 */
@Service
public class UsdaApiService implements IUsdaApiService {

    private static final Logger logger = LoggerFactory.getLogger(UsdaApiService.class);
    private static final String BASE_API_URL = "https://api.nal.usda.gov/fdc/v1/";
    private static final String FOOD_PATH = "food/";
    private static final String FOODS_PATH = "foods";

    private final ApiConfig apiConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public UsdaApiService(ApiConfig apiConfig, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.apiConfig = apiConfig;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        logger.info("UsdaApiService initialized with API key and configurations.");
    }

    /**
     * Helper method to build the URL dynamically based on the given path.
     *
     * @param path The specific API path to append to the base URL.
     * @return The complete URL including the API key.
     */
    private String buildUrl(String path) {
        return String.format(BASE_API_URL + "%s?api_key=%s", path, apiConfig.getUsdaApiKey());
    }

    /**
     * Helper method to parse JSON response using ObjectMapper.
     *
     * @param responseBody The JSON response body to parse.
     * @param valueType The class type to parse the response into.
     * @param <T> The type of the parsed object.
     * @return Parsed object of type T.
     * @throws IOException if parsing fails.
     */
    private <T> T parseResponse(String responseBody, Class<T> valueType) throws IOException {
        return objectMapper.readValue(responseBody, valueType);
    }

    /**
     * Fetches food data from the USDA API for a single food item identified by its FDC ID.
     *
     * @param fdcId The FoodData Central ID of the food item to fetch.
     * @return The UsdaFoodResponseDTO containing the food data.
     * @throws UsdaApiException if the HTTP request fails or the response is invalid.
     */
    @Override
    @Retryable(value = {RestClientException.class}, maxAttempts = 3, backoff = @Backoff(delay = 20000))
    public UsdaFoodResponseDTO getFoodData(String fdcId) {
        logger.info("Fetching food data for FDC ID: {}", fdcId);
        try {
            String url = buildUrl(FOOD_PATH + fdcId);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                logger.error("Received non-success status code {} for FDC ID {}", responseEntity.getStatusCode(), fdcId);
                throw new UsdaApiException("Error occurred: " + responseEntity.getStatusCode());
            }

            if (responseEntity.getBody() == null) {
                logger.error("Response body is null for FDC ID: {}", fdcId);
                throw new UsdaApiException("Response body is null for FDC ID: " + fdcId);
            }
            return parseResponse(responseEntity.getBody(), UsdaFoodResponseDTO.class);

        } catch (RestClientException e) {
            logger.error("HTTP request failed for FDC ID: {}", fdcId, e);
            throw new UsdaApiException("Failed to fetch food data from USDA API for FDC ID: " + fdcId, e);
        } catch (IOException e) {
            logger.error("Failed to parse JSON response for FDC ID: {}", fdcId, e);
            throw new UsdaApiException("Error parsing USDA API response for FDC ID: " + fdcId, e);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while processing response for FDC ID: {}", fdcId, e);
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
    @Override
    @Retryable(value = {RestClientException.class}, maxAttempts = 3, backoff = @Backoff(delay = 20000))
    public List<UsdaFoodResponseDTO> getMultipleFoodData(List<String> fdcIds) {
        logger.info("Fetching food data for multiple FDC IDs: {}", fdcIds);
        try {
            String url = buildUrl(FOODS_PATH);
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("fdcIds", fdcIds);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestBody, String.class);

            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                logger.error("Received non-success status code {} for multiple FDC IDs", responseEntity.getStatusCode());
                throw new UsdaApiException("Error occurred: " + responseEntity.getStatusCode());
            }

            if (responseEntity.getBody() == null) {
                logger.error("Response body is null for multiple FDC IDs: {}", fdcIds);
                throw new UsdaApiException("Response body is null for multiple FDC IDs: " + fdcIds);
            }
            return Arrays.asList(parseResponse(responseEntity.getBody(), UsdaFoodResponseDTO[].class));

        } catch (RestClientException e) {
            logger.error("HTTP request failed for multiple FDC IDs: {}", fdcIds, e);
            throw new UsdaApiException("Failed to fetch multiple food data from USDA API", e);
        } catch (IOException e) {
            logger.error("Failed to parse JSON response for multiple FDC IDs: {}", fdcIds, e);
            throw new UsdaApiException("Error parsing USDA API response for multiple FDC IDs", e);
        } catch (Exception e) {
            logger.error("Unexpected error occurred while processing response for multiple FDC IDs: {}", fdcIds, e);
            throw new UsdaApiException("An unexpected error occurred while processing USDA API response", e);
        }
    }
}

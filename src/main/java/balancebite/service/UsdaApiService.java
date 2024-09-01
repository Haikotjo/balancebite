package balancebite.service;

import balancebite.config.ApiConfig;
import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.exception.UsdaApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UsdaApiService {

    private final ApiConfig apiConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public UsdaApiService(ApiConfig apiConfig, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.apiConfig = apiConfig;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper; // ObjectMapper wordt nu als bean geïnjecteerd
    }

    public UsdaFoodResponseDTO getFoodData(String fdcId) {
        try {
            String apiKey = apiConfig.getUsdaApiKey();
            String url = "https://api.nal.usda.gov/fdc/v1/food/" + fdcId + "?api_key=" + apiKey;

            // Gebruik ResponseEntity om de HTTP-statuscode te controleren
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new UsdaApiException("Failed to fetch food data: " + responseEntity.getStatusCode().toString());
            }

            return objectMapper.readValue(responseEntity.getBody(), UsdaFoodResponseDTO.class);
        } catch (RestClientException e) {
            throw new UsdaApiException("Failed to fetch food data from USDA API for FDC ID: " + fdcId, e);
        } catch (Exception e) {
            throw new UsdaApiException("An unexpected error occurred while processing USDA API response", e);
        }
    }

    public List<UsdaFoodResponseDTO> getMultipleFoodData(List<String> fdcIds) {
        try {
            String apiKey = apiConfig.getUsdaApiKey();
            String url = "https://api.nal.usda.gov/fdc/v1/foods?api_key=" + apiKey;

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("fdcIds", fdcIds);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Gebruik ResponseEntity om de HTTP-statuscode te controleren
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                throw new UsdaApiException("Failed to fetch multiple food data: " + responseEntity.getStatusCode().toString());
            }

            return Arrays.asList(objectMapper.readValue(responseEntity.getBody(), UsdaFoodResponseDTO[].class));
        } catch (RestClientException e) {
            throw new UsdaApiException("Failed to fetch multiple food data from USDA API", e);
        } catch (Exception e) {
            throw new UsdaApiException("An unexpected error occurred while processing USDA API response", e);
        }
    }
}

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

    @Autowired
    public UsdaApiService(ApiConfig apiConfig, RestTemplate restTemplate) {
        this.apiConfig = apiConfig;
        this.restTemplate = restTemplate;
    }

    public UsdaFoodResponseDTO getFoodData(String fdcId) {
        try {
            String apiKey = apiConfig.getUsdaApiKey();
            String url = "https://api.nal.usda.gov/fdc/v1/food/" + fdcId + "?api_key=" + apiKey;
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response, UsdaFoodResponseDTO.class);
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

            String response = restTemplate.postForObject(url, entity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            return Arrays.asList(objectMapper.readValue(response, UsdaFoodResponseDTO[].class));
        } catch (RestClientException e) {
            throw new UsdaApiException("Failed to fetch multiple food data from USDA API", e);
        } catch (Exception e) {
            throw new UsdaApiException("An unexpected error occurred while processing USDA API response", e);
        }
    }
}

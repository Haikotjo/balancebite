package balancebite.service;

import balancebite.config.ApiConfig;
import balancebite.dto.UsdaFoodResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
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

    public UsdaFoodResponseDTO getFoodData(String fdcId) throws Exception {
        String apiKey = apiConfig.getUsdaApiKey();
        String url = "https://api.nal.usda.gov/fdc/v1/food/" + fdcId + "?api_key=" + apiKey;
        String response = restTemplate.getForObject(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response, UsdaFoodResponseDTO.class);
    }

    public List<UsdaFoodResponseDTO> getMultipleFoodData(List<String> fdcIds) throws Exception {
        String apiKey = apiConfig.getUsdaApiKey();
        String url = "https://api.nal.usda.gov/fdc/v1/foods?api_key=" + apiKey;

        // Prepare the request body with the list of FDC IDs as a JSON object
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("fdcIds", fdcIds);

        // Set the headers to indicate the content type is JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // Send the POST request and get the response
        String response = restTemplate.postForObject(url, entity, String.class);

        // Convert the JSON response to a list of UsdaFoodResponseDTO objects
        ObjectMapper objectMapper = new ObjectMapper();
        return Arrays.asList(objectMapper.readValue(response, UsdaFoodResponseDTO[].class));
    }
}

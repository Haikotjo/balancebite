package balancebite.service;

import balancebite.config.ApiConfig;
import balancebite.dto.UsdaFoodResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        System.out.println("API Response: " + response);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response, UsdaFoodResponseDTO.class);
    }
}

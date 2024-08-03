package balancebite.service;
import balancebite.config.ApiConfig;
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

    public String getFoodData(String fdcId) {
        String apiKey = apiConfig.getUsdaApiKey();
        String url = "https://api.nal.usda.gov/fdc/v1/food/" + fdcId + "?api_key=" + apiKey;
        return restTemplate.getForObject(url, String.class);
    }
}
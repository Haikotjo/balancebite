package balancebite.service;

import balancebite.config.ApiConfig;
import balancebite.dto.NutrientAPIDTO;
import balancebite.mapper.FoodItemMapper;
import balancebite.model.FoodItem;
import balancebite.model.NutrientInfo;
import balancebite.repository.FoodItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodItemService {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private ApiConfig apiConfig;

    @Autowired
    private RestTemplate restTemplate;

    public void fetchAndSaveFoodItem(String fdcId) {
        String apiUrl = "https://api.nal.usda.gov/fdc/v1/food/" + fdcId + "?api_key=" + apiConfig.getUsdaApiKey();
        NutrientAPIDTO response = restTemplate.getForObject(apiUrl, NutrientAPIDTO.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(response);
            System.out.println("API Response: " + jsonResponse);
        } catch (Exception e) {
//            e.printStackTrace();
        }

        if (response != null && response.getFoodNutrients() != null) {
            FoodItem foodItem = new FoodItem(response.getDescription(),
                    response.getFoodNutrients().stream()
                            .map(n -> new NutrientInfo(n.getNutrient().getName(), n.getAmount(), n.getUnitName()))
                            .collect(Collectors.toList()));
            foodItemRepository.save(foodItem);
        }
    }

    public void fetchAndSaveAllFoodItems(List<String> fdcIds) {
        for (String fdcId : fdcIds) {
            fetchAndSaveFoodItem(fdcId);
        }
    }
}

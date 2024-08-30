package balancebite.service;

import balancebite.config.ApiConfig;
import balancebite.dto.NutrientAPIDTO;
import balancebite.model.FoodItem;
import balancebite.model.NutrientInfo;
import balancebite.repository.FoodItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FoodItemService {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private ApiConfig apiConfig;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Fetches a single FoodItem from the USDA API using the provided FDC ID and saves it to the database.
     * This method sends a GET request to the /v1/food/{fdcId} endpoint to retrieve the details of one food item.
     */
    public boolean fetchAndSaveFoodItem(String fdcId) {
        // Fetch the food item data from the USDA API
        String apiUrl = "https://api.nal.usda.gov/fdc/v1/food/" + fdcId + "?api_key=" + apiConfig.getUsdaApiKey();
        NutrientAPIDTO response = restTemplate.getForObject(apiUrl, NutrientAPIDTO.class);

        try {
            if (response != null && response.getFoodNutrients() != null) {
                // Check if the FoodItem already exists in the database by name
                if (foodItemRepository.existsByName(response.getDescription())) {
                    return false; // Indicate that the item already exists
                }

                // If the item does not exist, create a new FoodItem entity and save it
                FoodItem foodItem = new FoodItem(response.getDescription(),
                        response.getFoodNutrients().stream()
                                .map(n -> new NutrientInfo(
                                        n.getNutrient().getName(),
                                        n.getAmount(),
                                        n.getUnitName(),
                                        n.getNutrient().getNutrientId()
                                ))
                                .collect(Collectors.toList()));
                foodItemRepository.save(foodItem);
                return true; // Indicate that the item was newly added
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Default to false if something goes wrong
    }


    /**
     * Fetches multiple FoodItems from the USDA API using a list of FDC IDs and saves them to the database.
     * This method sends a POST request to the /v1/foods endpoint to retrieve the details of multiple food items in a single API call.
     */
    public void fetchAndSaveAllFoodItems(List<String> fdcIds) {
        String apiUrl = "https://api.nal.usda.gov/fdc/v1/foods?api_key=" + apiConfig.getUsdaApiKey();

        // Prepare the request body with the list of FDC IDs as a JSON object
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("fdcIds", fdcIds);

        // Set the headers to indicate the content type is JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // Send the POST request and get the response
        NutrientAPIDTO[] responses = restTemplate.postForObject(apiUrl, entity, NutrientAPIDTO[].class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(responses);
            System.out.println("API Response: " + jsonResponse);
        } catch (Exception e) {
            // Handle exceptions
        }

        if (responses != null) {
            for (NutrientAPIDTO response : responses) {
                if (response.getFoodNutrients() != null) {
                    FoodItem foodItem = new FoodItem(response.getDescription(),
                            response.getFoodNutrients().stream()
                                    .map(n -> new NutrientInfo(
                                            n.getNutrient().getName(),
                                            n.getAmount(),
                                            n.getUnitName(),
                                            n.getNutrient().getNutrientId()
                                    ))
                                    .collect(Collectors.toList()));
                    foodItemRepository.save(foodItem);
                }
            }
        }
    }
}

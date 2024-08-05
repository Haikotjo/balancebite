package balancebite.service;

import balancebite.config.ApiConfig;
import balancebite.dto.NutrientAPIDTO;
import balancebite.dto.NutrientDTO;
import balancebite.dto.NutrientInputDTO;
import balancebite.mapper.NutrientMapper;
import balancebite.model.Nutrient;
import balancebite.repository.NutrientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NutrientService {

    @Autowired
    private NutrientRepository nutrientRepository;

    @Autowired
    private ApiConfig apiConfig;

    @Autowired
    private RestTemplate restTemplate;

    public void fetchAndSaveNutrients(String fdcId) {
        String apiUrl = "https://api.nal.usda.gov/fdc/v1/food/" + fdcId + "?api_key=" + apiConfig.getUsdaApiKey();
        NutrientAPIDTO response = restTemplate.getForObject(apiUrl, NutrientAPIDTO.class);

        try {
            // Log de volledige response om te controleren of unitName aanwezig is
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(response);
            System.out.println("API Response: " + jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (response != null && response.getFoodNutrients() != null) {
            for (NutrientAPIDTO.FoodNutrientDTO nutrientDTO : response.getFoodNutrients()) {
                System.out.println("Nutrient: " + nutrientDTO.getNutrient().getName());
                System.out.println("Unit Name: " + nutrientDTO.getUnitName());
                System.out.println("Amount: " + nutrientDTO.getAmount());

                Nutrient entity = NutrientMapper.toEntity(nutrientDTO);
                nutrientRepository.save(entity);
            }
        }
    }
}

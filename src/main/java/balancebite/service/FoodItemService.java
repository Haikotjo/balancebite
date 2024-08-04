package balancebite.service;

import balancebite.dto.FoodItemInputDTO;
import balancebite.model.FoodItem;
import balancebite.repository.FoodItemRepository;
import balancebite.mapper.FoodItemMapper;
import balancebite.dto.UsdaFoodResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FoodItemService {

    private static final Logger logger = LoggerFactory.getLogger(FoodItemService.class);

    private final FoodItemRepository foodItemRepository;
    private final FoodItemMapper foodItemMapper;
    private final UsdaApiService usdaApiService;

    @Autowired
    public FoodItemService(FoodItemRepository foodItemRepository, FoodItemMapper foodItemMapper, UsdaApiService usdaApiService) {
        this.foodItemRepository = foodItemRepository;
        this.foodItemMapper = foodItemMapper;
        this.usdaApiService = usdaApiService;
    }

    public void saveFoodItem(String fdcId) {
        try {
            logger.info("Fetching food data for FDC ID: {}", fdcId);
            UsdaFoodResponseDTO usdaFoodResponse = usdaApiService.getFoodData(fdcId);
            logger.debug("Received USDA Response: {}", usdaFoodResponse);

            FoodItemInputDTO foodItemInputDTO = foodItemMapper.toInputDto(usdaFoodResponse);
            logger.debug("Mapped FoodItemInputDTO: {}", foodItemInputDTO);

            FoodItem foodItem = foodItemMapper.toEntity(foodItemInputDTO);
            logger.debug("Mapped FoodItem Entity: {}", foodItem);

            foodItemRepository.save(foodItem);
            logger.info("Food item saved successfully: {}", foodItem);
        } catch (Exception e) {
            logger.error("Error saving food item", e);
        }
    }
}

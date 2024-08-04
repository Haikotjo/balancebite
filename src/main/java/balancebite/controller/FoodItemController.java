package balancebite.controller;

import balancebite.dto.UsdaFoodResponseDTO;
import balancebite.service.FoodItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fooditems")
public class FoodItemController {

    private final FoodItemService foodItemService;

    @Autowired
    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    @PostMapping("/usda/{fdcId}")
    public ResponseEntity<String> createFoodItemFromUsda(@PathVariable String fdcId) {
        try {
            foodItemService.saveFoodItem(fdcId);
            return ResponseEntity.ok("Food item saved successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching data from USDA API");
        }
    }
}

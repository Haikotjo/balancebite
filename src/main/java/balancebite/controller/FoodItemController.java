package balancebite.controller;

import balancebite.model.FoodItem;
import balancebite.repository.FoodItemRepository;
import balancebite.service.FoodItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/food-items")
public class FoodItemController {

    @Autowired
    private FoodItemService foodItemService;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @GetMapping("/fetch/{fdcId}")
    public String fetchFoodItem(@PathVariable String fdcId) {
        foodItemService.fetchAndSaveFoodItem(fdcId);
        return "Food item for FDC ID " + fdcId + " fetched and saved successfully";
    }

    @GetMapping
    public List<FoodItem> getAllFoodItems() {
        return foodItemRepository.findAll();
    }

    @GetMapping("/search")
    public List<FoodItem> searchFoodItems(@RequestParam String name) {
        return foodItemRepository.findByNameContainingIgnoreCase(name);
    }

    @GetMapping("/{id}")
    public Optional<FoodItem> getFoodItemById(@PathVariable Long id) {
        return foodItemRepository.findById(id);
    }
}

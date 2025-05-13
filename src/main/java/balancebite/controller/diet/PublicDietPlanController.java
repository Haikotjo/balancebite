package balancebite.controller.diet;

import balancebite.dto.diet.DietPlanDTO;
import balancebite.errorHandling.DietPlanNotFoundException;
import balancebite.service.interfaces.diet.IPublicDietPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public/diet-plans")
public class PublicDietPlanController {

    private static final Logger log = LoggerFactory.getLogger(PublicDietPlanController.class);
    private final IPublicDietPlanService publicDietPlanService;

    public PublicDietPlanController(IPublicDietPlanService publicDietPlanService) {
        this.publicDietPlanService = publicDietPlanService;
    }

    @GetMapping
    public ResponseEntity<?> getAllPublicDiets() {
        List<DietPlanDTO> diets = publicDietPlanService.getAllPublicDietPlans();
        if (diets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(diets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPublicDietById(@PathVariable Long id) {
        try {
            DietPlanDTO diet = publicDietPlanService.getPublicDietPlanById(id);
            return ResponseEntity.ok(diet);
        } catch (DietPlanNotFoundException e) {
            log.warn("Public diet not found: {}", e.getMessage());
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
}

package balancebite.controller.publiccontroller;

import balancebite.dto.diet.DietDTO;
import balancebite.errorHandling.DietNotFoundException;
import balancebite.service.interfaces.diet.IPublicDietService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public")
public class PublicDietController {

    private static final Logger log = LoggerFactory.getLogger(PublicDietController.class);
    private final IPublicDietService publicDietService;

    public PublicDietController(IPublicDietService publicDietService) {
        this.publicDietService = publicDietService;
    }

    @GetMapping("/diets")
    public ResponseEntity<?> getAllPublicDiets() {
        List<DietDTO> diets = publicDietService.getAllPublicDiets();
        if (diets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(diets);
    }

    @GetMapping("/diet/{id}")
    public ResponseEntity<?> getPublicDietById(@PathVariable Long id) {
        try {
            DietDTO diet = publicDietService.getPublicDietById(id);
            return ResponseEntity.ok(diet);
        } catch (DietNotFoundException e) {
            log.warn("Public diet not found: {}", e.getMessage());
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }
}

package balancebite.controller;

import balancebite.model.Nutrient;
import balancebite.repository.NutrientRepository;
import balancebite.service.NutrientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/nutrients")
public class NutrientController {

    @Autowired
    private NutrientService nutrientService;

    @Autowired
    private NutrientRepository nutrientRepository;

    @GetMapping("/fetch/{fdcId}")
    public String fetchNutrients(@PathVariable String fdcId) {
        nutrientService.fetchAndSaveNutrients(fdcId);
        return "Nutrients for FDC ID " + fdcId + " fetched and saved successfully";
    }

    @GetMapping
    public List<Nutrient> getAllNutrients() {
        return nutrientRepository.findAll();
    }

    @GetMapping("/search")
    public List<Nutrient> searchNutrients(@RequestParam String name) {
        return nutrientRepository.findByNutrientNameContainingIgnoreCase(name);
    }

    @GetMapping("/{id}")
    public Optional<Nutrient> getNutrientById(@PathVariable Long id) {
        return nutrientRepository.findById(id);
    }
}

package balancebite.service.diet;

import balancebite.dto.diet.DietPlanDTO;
import balancebite.errorHandling.DietPlanNotFoundException;
import balancebite.mapper.DietPlanMapper;
import balancebite.model.diet.DietPlan;
import balancebite.repository.DietPlanRepository;
import balancebite.service.interfaces.diet.IPublicDietPlanService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicDietPlanService implements IPublicDietPlanService {

    private final DietPlanRepository dietPlanRepository;
    private final DietPlanMapper dietPlanMapper;

    public PublicDietPlanService(DietPlanRepository dietPlanRepository, DietPlanMapper dietPlanMapper) {
        this.dietPlanRepository = dietPlanRepository;
        this.dietPlanMapper = dietPlanMapper;
    }

    @Override
    public List<DietPlanDTO> getAllPublicDietPlans() {
        return dietPlanRepository.findAll().stream()
                .filter(DietPlan::isTemplate)
                .map(dietPlanMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DietPlanDTO getPublicDietPlanById(Long id) {
        DietPlan dietPlan = dietPlanRepository.findById(id)
                .filter(DietPlan::isTemplate)
                .orElseThrow(() -> new DietPlanNotFoundException("Public diet not found with ID: " + id));
        return dietPlanMapper.toDTO(dietPlan);
    }
}

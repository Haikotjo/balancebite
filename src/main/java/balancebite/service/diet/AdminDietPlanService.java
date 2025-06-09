package balancebite.service.diet;

import balancebite.dto.diet.DietPlanAdminListDTO;
import balancebite.errorHandling.DietPlanNotFoundException;
import balancebite.repository.DietPlanRepository;
import balancebite.service.interfaces.diet.IAdminDietPlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminDietPlanService implements IAdminDietPlanService {

    private final DietPlanRepository dietPlanRepository;

    public AdminDietPlanService(DietPlanRepository dietPlanRepository) {
        this.dietPlanRepository = dietPlanRepository;
    }

@Override
    public List<DietPlanAdminListDTO> getAllDietPlansWithCreator() {
        return dietPlanRepository.findAll().stream().map(dietPlan -> {
            String creatorName = dietPlan.getCreatedBy() != null ? dietPlan.getCreatedBy().getUserName() : "Unknown";
            String adjustedByName = dietPlan.getAdjustedBy() != null ? dietPlan.getAdjustedBy().getUserName() : "—";
            return new DietPlanAdminListDTO(
                    dietPlan.getId(),
                    dietPlan.getName(),
                    creatorName,
                    adjustedByName
            );
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteDietPlanById(Long id) {
        var dietPlan = dietPlanRepository.findById(id)
                .orElseThrow(() -> new DietPlanNotFoundException("No diet plan found with ID: " + id));

        dietPlan.getUsers().forEach(user -> user.getSavedDietPlans().remove(dietPlan));

        dietPlanRepository.delete(dietPlan);
    }

}

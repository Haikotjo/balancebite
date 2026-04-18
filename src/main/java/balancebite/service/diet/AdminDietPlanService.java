package balancebite.service.diet;

import balancebite.dto.diet.DietPlanAdminListDTO;
import balancebite.errorHandling.DietPlanNotFoundException;
import balancebite.repository.DietPlanRepository;
import balancebite.repository.SavedDietPlanRepository;
import balancebite.repository.UserRepository;
import balancebite.service.interfaces.diet.IAdminDietPlanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminDietPlanService implements IAdminDietPlanService {

    private final DietPlanRepository dietPlanRepository;
    private final SavedDietPlanRepository savedDietPlanRepository;
    private final UserRepository userRepository;

    public AdminDietPlanService(DietPlanRepository dietPlanRepository,
                                SavedDietPlanRepository savedDietPlanRepository,
                                UserRepository userRepository) {
        this.dietPlanRepository = dietPlanRepository;
        this.savedDietPlanRepository = savedDietPlanRepository;
        this.userRepository = userRepository;
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
                    adjustedByName,
                    dietPlan.isTemplate()
            );
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteDietPlanById(Long id) {
        var dietPlan = dietPlanRepository.findById(id)
                .orElseThrow(() -> new DietPlanNotFoundException("No diet plan found with ID: " + id));

        savedDietPlanRepository.deleteAllByDietPlan(dietPlan);

        dietPlan.getUsers().forEach(user -> {
            user.getSavedDietPlans().remove(dietPlan);
            userRepository.save(user);
        });

        dietPlanRepository.delete(dietPlan);
    }

}

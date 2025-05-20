package balancebite.service.diet;

import balancebite.dto.diet.DietPlanDTO;
import balancebite.errorHandling.DietPlanNotFoundException;
import balancebite.mapper.DietPlanMapper;
import balancebite.model.diet.DietPlan;
import balancebite.repository.DietPlanRepository;
import balancebite.service.interfaces.diet.IPublicDietPlanService;
import balancebite.service.meal.MealService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PublicDietPlanService implements IPublicDietPlanService {

    private static final Logger log = LoggerFactory.getLogger(MealService.class);
    private final DietPlanRepository dietPlanRepository;
    private final DietPlanMapper dietPlanMapper;

    public PublicDietPlanService(DietPlanRepository dietPlanRepository, DietPlanMapper dietPlanMapper) {
        this.dietPlanRepository = dietPlanRepository;
        this.dietPlanMapper = dietPlanMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DietPlanDTO> getAllPublicDietPlans(
            List<String> diets,
            String sortBy,
            String sortOrder,
            Pageable pageable
    ) {
        log.info("Retrieving paginated template diet plans with filters and sorting.");

        List<DietPlan> allTemplates = dietPlanRepository.findAll().stream()
                .filter(DietPlan::isTemplate)
                .collect(Collectors.toList());

        // Filter op diets
        if (diets != null && !diets.isEmpty()) {
            Set<String> dietSet = diets.stream().map(String::toUpperCase).collect(Collectors.toSet());
            allTemplates.removeIf(plan -> plan.getDiets().stream()
                    .map(Enum::name)
                    .noneMatch(dietSet::contains));
        }

        // Sorteren
        Comparator<DietPlan> comparator = Comparator.comparing(DietPlan::getName); // default
        if ("createdAt".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparing(DietPlan::getCreatedAt);
        }

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        allTemplates.sort(comparator);

        // Pagineren
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allTemplates.size());
        List<DietPlanDTO> pageContent = allTemplates.subList(start, end).stream()
                .map(dietPlanMapper::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(pageContent, pageable, allTemplates.size());
    }

    @Override
    public DietPlanDTO getPublicDietPlanById(Long id) {
        DietPlan dietPlan = dietPlanRepository.findById(id)
                .filter(DietPlan::isTemplate)
                .orElseThrow(() -> new DietPlanNotFoundException("Public diet not found with ID: " + id));
        return dietPlanMapper.toDTO(dietPlan);
    }
}

package balancebite.service.diet;

import balancebite.dto.diet.DietPlanDTO;
import balancebite.errorHandling.DietPlanNotFoundException;
import balancebite.mapper.DietPlanMapper;
import balancebite.model.diet.DietPlan;
import balancebite.model.meal.references.Diet;
import balancebite.repository.DietPlanRepository;
import balancebite.service.interfaces.diet.IPublicDietPlanService;
import balancebite.service.meal.MealService;
import balancebite.specification.DietPlanSpecification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PublicDietPlanService implements IPublicDietPlanService {

    private static final Logger log = LoggerFactory.getLogger(PublicDietPlanService.class);
    private final DietPlanRepository dietPlanRepository;
    private final DietPlanMapper dietPlanMapper;

    public PublicDietPlanService(DietPlanRepository dietPlanRepository, DietPlanMapper dietPlanMapper) {
        this.dietPlanRepository = dietPlanRepository;
        this.dietPlanMapper = dietPlanMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DietPlanDTO> getAllPublicDietPlans(
            List<String> requiredDiets,
            List<String> excludedDiets,
            List<String> diets,
            String sortBy,
            String sortOrder,
            Pageable pageable,
            Double minProtein,
            Double maxProtein,
            Double minCarbs,
            Double maxCarbs,
            Double minFat,
            Double maxFat,
            Double minCalories,
            Double maxCalories,
            Long createdByUserId
    ) {
        Specification<DietPlan> spec = createdByUserId != null
                ? Specification.where(DietPlanSpecification.isTemplateCreatedBy(createdByUserId))
                : Specification.where(DietPlanSpecification.isTemplate());

        spec = spec.and((root, query, cb) -> cb.isFalse(root.get("isPrivate")));


        if (diets != null && !diets.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.join("diets").in(diets));
        }

        // Vereist: ALLE diets moeten aanwezig zijn
        if (requiredDiets != null && !requiredDiets.isEmpty()) {
            Set<Diet> requiredEnums = requiredDiets.stream()
                    .map(Diet::valueOf)
                    .collect(Collectors.toSet());
            spec = spec.and(DietPlanSpecification.mustIncludeAllDiets(requiredEnums));
        }

        // Uitsluiten: GEEN van deze diets mag voorkomen
        if (excludedDiets != null && !excludedDiets.isEmpty()) {
            Set<Diet> excludedEnums = excludedDiets.stream()
                    .map(Diet::valueOf)
                    .collect(Collectors.toSet());
            spec = spec.and(DietPlanSpecification.mustExcludeAllDiets(excludedEnums));
        }

        // Gemiddelde (avg) filters – ondersteunt ook alleen min of alleen max
        if (minProtein != null && maxProtein != null) {
            spec = spec.and(DietPlanSpecification.avgProteinBetween(minProtein, maxProtein));
        } else if (minProtein != null) {
            spec = spec.and((root, query, cb) -> cb.ge(root.get("avgProtein"), minProtein));
        } else if (maxProtein != null) {
            spec = spec.and((root, query, cb) -> cb.le(root.get("avgProtein"), maxProtein));
        }

        if (minCarbs != null && maxCarbs != null) {
            spec = spec.and(DietPlanSpecification.avgCarbsBetween(minCarbs, maxCarbs));
        } else if (minCarbs != null) {
            spec = spec.and((root, query, cb) -> cb.ge(root.get("avgCarbs"), minCarbs));
        } else if (maxCarbs != null) {
            spec = spec.and((root, query, cb) -> cb.le(root.get("avgCarbs"), maxCarbs));
        }

        if (minFat != null && maxFat != null) {
            spec = spec.and(DietPlanSpecification.avgFatBetween(minFat, maxFat));
        } else if (minFat != null) {
            spec = spec.and((root, query, cb) -> cb.ge(root.get("avgFat"), minFat));
        } else if (maxFat != null) {
            spec = spec.and((root, query, cb) -> cb.le(root.get("avgFat"), maxFat));
        }

        if (minCalories != null && maxCalories != null) {
            spec = spec.and(DietPlanSpecification.avgCaloriesBetween(minCalories, maxCalories));
        } else if (minCalories != null) {
            spec = spec.and((root, query, cb) -> cb.ge(root.get("avgCalories"), minCalories));
        } else if (maxCalories != null) {
            spec = spec.and((root, query, cb) -> cb.le(root.get("avgCalories"), maxCalories));
        }


        // Pas sortering toe op de bestaande pageable
        Map<String, String> sortFieldMap = Map.ofEntries(
                Map.entry("avgProtein", "avgProtein"),
                Map.entry("avgCarbs", "avgCarbs"),
                Map.entry("avgFat", "avgFat"),
                Map.entry("avgCalories", "avgCalories"),
                Map.entry("totalProtein", "totalProtein"),
                Map.entry("totalCarbs", "totalCarbs"),
                Map.entry("totalFat", "totalFat"),
                Map.entry("totalCalories", "totalCalories"),
                Map.entry("saveCount", "saveCount"),
                Map.entry("weeklySaveCount", "weeklySaveCount"),
                Map.entry("monthlySaveCount", "monthlySaveCount"),
                Map.entry("createdAt", "createdAt"),
                Map.entry("name", "name")
        );


        String mappedSortBy = sortFieldMap.getOrDefault(sortBy, "createdAt");
        Sort.Direction direction = sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, mappedSortBy));

        log.info("Hallo!!! DIET FILTERS:minCarbs={}, maxCarbs={}, minProtein={}, maxProtein={}, minFat={}, maxFat={}, minCalories={}, maxCalories={}",
                minCarbs, maxCarbs, minProtein, maxProtein, minFat, maxFat, minCalories, maxCalories
        );

        return dietPlanRepository.findAll(spec, sortedPageable).map(dietPlanMapper::toDTO);
    }

    @Override
    public DietPlanDTO getPublicDietPlanById(Long id) {
        DietPlan dietPlan = dietPlanRepository.findById(id)
                .filter(d -> d.isTemplate() && !d.isPrivate())
                .orElseThrow(() -> new DietPlanNotFoundException("Public diet not found with ID: " + id));
        return dietPlanMapper.toDTO(dietPlan);
    }
}

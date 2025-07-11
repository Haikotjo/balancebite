package balancebite.service.diet;

import balancebite.dto.diet.DietPlanDTO;
import balancebite.dto.diet.DietPlanNameDTO;
import balancebite.errorHandling.DietPlanNotFoundException;
import balancebite.mapper.DietPlanMapper;
import balancebite.model.diet.DietPlan;
import balancebite.model.meal.references.Diet;
import balancebite.model.user.User;
import balancebite.model.user.UserRole;
import balancebite.repository.DietPlanRepository;
import balancebite.repository.SharedDietPlanAccessRepository;
import balancebite.repository.UserRepository;
import balancebite.security.SecurityUtils;
import balancebite.service.interfaces.diet.IPublicDietPlanService;
import balancebite.service.meal.MealService;
import balancebite.specification.DietPlanSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
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
    private final UserRepository userRepository;
    private final DietPlanMapper dietPlanMapper;

    private final SharedDietPlanAccessRepository sharedDietPlanAccessRepository;

    public PublicDietPlanService(DietPlanRepository dietPlanRepository, DietPlanMapper dietPlanMapper, UserRepository userRepository, SharedDietPlanAccessRepository sharedDietPlanAccessRepository) {
        this.dietPlanRepository = dietPlanRepository;
        this.dietPlanMapper = dietPlanMapper;
        this.userRepository = userRepository;
        this.sharedDietPlanAccessRepository = sharedDietPlanAccessRepository;
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
            Long createdByUserId,
            String name
    ) {
        Specification<DietPlan> spec = createdByUserId != null
                ? Specification.where(DietPlanSpecification.isTemplateCreatedBy(createdByUserId))
                : Specification.where(DietPlanSpecification.isTemplate());

        spec = spec.and((root, query, cb) -> cb.isFalse(root.get("isPrivate")));
        spec = spec.and(DietPlanSpecification.isNotRestricted());

        if (name != null && !name.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

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


        String mappedSortBy = sortFieldMap.getOrDefault(
                (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy,
                "createdAt"
        );

        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortOrder);
        } catch (Exception e) {
            direction = Sort.Direction.DESC; // fallback
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, mappedSortBy));

        log.info("Hallo!!! DIET FILTERS:minCarbs={}, maxCarbs={}, minProtein={}, maxProtein={}, minFat={}, maxFat={}, minCalories={}, maxCalories={}",
                minCarbs, maxCarbs, minProtein, maxProtein, minFat, maxFat, minCalories, maxCalories
        );

        return dietPlanRepository.findAll(spec, sortedPageable).map(dietPlanMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public DietPlanDTO getPublicDietPlanById(Long id) {
        DietPlan dietPlan = dietPlanRepository.findById(id)
                .orElseThrow(() -> new DietPlanNotFoundException("Diet plan not found with ID: " + id));

        if (!dietPlan.isTemplate()) {
            throw new DietPlanNotFoundException("Diet plan is not a public template");
        }

        if (dietPlan.isPrivate()) {
            User currentUser = getCurrentUserOrThrow();
            boolean isOwner = dietPlan.getCreatedBy().getId().equals(currentUser.getId());
            boolean isSharedByEmail = sharedDietPlanAccessRepository.existsByDietPlanIdAndEmail(id, currentUser.getEmail());
            boolean isSharedByUserId = sharedDietPlanAccessRepository.existsByDietPlanIdAndUserId(id, currentUser.getId());

            boolean isAdmin = currentUser.getRoles().stream()
                    .anyMatch(role -> role.getRolename() == UserRole.ADMIN);

            if (!isOwner && !isSharedByEmail && !isSharedByUserId && !isAdmin) {
                throw new AccessDeniedException("This diet plan is private.");
            }
        }

        return dietPlanMapper.toDTO(dietPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DietPlanNameDTO> getAllPublicDietPlanNames() {
        log.info("Fetching all public diet plan names and IDs.");
        return dietPlanRepository.findAllTemplateDietPlanNames();
    }

    private User getCurrentUserOrThrow() {
        Long userId = SecurityUtils.getCurrentAuthenticatedUserId();
        if (userId == null) {
            throw new AccessDeniedException("No authenticated user.");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }
}

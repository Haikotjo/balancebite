package balancebite.service.user;

import balancebite.dto.diet.DietDayInputDTO;
import balancebite.dto.diet.DietPlanDTO;
import balancebite.dto.diet.DietPlanInputDTO;
import balancebite.errorHandling.DietPlanNotFoundException;
import balancebite.errorHandling.DuplicateDietPlanException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.mapper.DietDayMapper;
import balancebite.mapper.DietPlanMapper;
import balancebite.model.diet.DietDay;
import balancebite.model.diet.DietPlan;
import balancebite.model.meal.Meal;
import balancebite.model.user.User;
import balancebite.repository.DietPlanRepository;
import balancebite.repository.MealRepository;
import balancebite.repository.UserRepository;
import balancebite.service.interfaces.diet.IUserDietPlanService;
import balancebite.dto.user.UserDTO;
import balancebite.mapper.UserMapper;
import balancebite.utils.MealAssignmentUtil;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserDietPlanService implements IUserDietPlanService {

    private static final Logger log = LoggerFactory.getLogger(UserDietPlanService.class);

    private final DietPlanRepository dietPlanRepository;
    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final DietPlanMapper dietPlanMapper;
    private final DietDayMapper dietDayMapper;
    private final UserMapper userMapper;
    private final UserMealService userMealService;
    private final MealAssignmentUtil mealAssignmentUtil;

    public UserDietPlanService(DietPlanRepository dietPlanRepository,
                               UserRepository userRepository,
                               MealRepository mealRepository,
                               DietPlanMapper dietPlanMapper,
                               DietDayMapper dietDayMapper,
                               UserMapper userMapper,
                               UserMealService userMealService,
                               MealAssignmentUtil mealAssignmentUtil) {
        this.dietPlanRepository = dietPlanRepository;
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.dietPlanMapper = dietPlanMapper;
        this.dietDayMapper = dietDayMapper;
        this.userMapper = userMapper;
        this.userMealService = userMealService;
        this.mealAssignmentUtil = mealAssignmentUtil;
    }

    @Override
    @Transactional
    public DietPlanDTO createDietPlan(DietPlanInputDTO input, Long userId) {
        log.info("Creating new diet for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        DietPlan dietPlan = new DietPlan();
        dietPlan.setName(input.getName());
        dietPlan.setCreatedBy(user);
        dietPlan.setTemplate(true);
        dietPlan.setOriginalDietId(null);
        dietPlan.setDietDescription(input.getDietDescription());
        dietPlan.setDiets(input.getDiets());

        // Fallback: zorg dat er minstens één dag is
        if (input.getDietDays() == null || input.getDietDays().isEmpty()) {
            DietDayInputDTO defaultDay = new DietDayInputDTO();
            defaultDay.setDietDayDescription("Default day description");
            defaultDay.setMealIds(new ArrayList<>());
            input.setDietDays(List.of(defaultDay));
        }

        List<DietDay> dietDays = new ArrayList<>();

        for (int i = 0; i < input.getDietDays().size(); i++) {
            DietDayInputDTO dayInput = input.getDietDays().get(i);

            // ➕ Haal alle meals op — als List, zodat duplicaten mogelijk zijn
            List<Meal> meals = dayInput.getMealIds().stream()
                    .map(id -> mealAssignmentUtil.getOrAddMealToUser(userId, id))
                    .collect(Collectors.toList());

            // ✅ Check op minstens 2 geldige maaltijden
            long validCount = meals.stream().filter(Objects::nonNull).count();
            if (validCount < 2) {
                throw new IllegalArgumentException("Each day must have at least 2 meals.");
            }

            // ➕ Pas eventueel je mapper aan zodat deze ook een List accepteert
            DietDay day = dietDayMapper.toEntity(dayInput, meals, i);
            day.setDiet(dietPlan);

            // ➕ Verzamel diets van de maaltijden in een Set (zonder duplicates)
            if (day.getDiets() == null) {
                day.setDiets(new HashSet<>());
            }
            for (Meal meal : meals) {
                if (meal != null && meal.getDiets() != null) {
                    day.getDiets().addAll(meal.getDiets());
                }
            }

            dietDays.add(day);
        }

        dietPlan.setDietDays(dietDays);

        // ➕ Voeg alle diets van alle dagen toe aan de DietPlan zelf
        Set<balancebite.model.meal.references.Diet> allDiets = new HashSet<>();
        for (DietDay day : dietDays) {
            if (day.getDiets() != null) {
                allDiets.addAll(day.getDiets());
            }
        }
        dietPlan.setDiets(allDiets);

        // ➕ Sla op en koppel aan gebruiker
        DietPlan saved = dietPlanRepository.save(dietPlan);
        user.getSavedDietPlans().add(saved);
        userRepository.save(user);

        log.info("Diet created with ID: {}", saved.getId());
        return dietPlanMapper.toDTO(saved);
    }


    @Override
    @Transactional
    public DietPlanDTO addDietPlanToUser(Long userId, Long dietPlanId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        DietPlan original = dietPlanRepository.findById(dietPlanId)
                .orElseThrow(() -> new DietPlanNotFoundException("DietPlan not found with ID: " + dietPlanId));

        // ── NIEUW: als jij de creator bent, her-link alleen het originele plan
        if (original.getCreatedBy() != null && original.getCreatedBy().getId().equals(userId)) {
            if (!user.getSavedDietPlans().contains(original)) {
                user.getSavedDietPlans().add(original);
                userRepository.save(user);
            }
            return dietPlanMapper.toDTO(original);
        }

        // ── Bestaande kopie (of template) check
        Optional<DietPlan> existingCopy = user.getSavedDietPlans().stream()
                .filter(d -> (d.getOriginalDietId() != null && d.getOriginalDietId().equals(dietPlanId))
                        || d.getId().equals(dietPlanId))
                .findFirst();

        if (existingCopy.isPresent()) {
            DietPlan copy = existingCopy.get();
            if (!user.getSavedDietPlans().contains(copy)) {
                user.getSavedDietPlans().add(copy);
                userRepository.save(user);
            }
            return dietPlanMapper.toDTO(copy);
        }

        // ── Anders maak je écht een nieuwe kopie
        DietPlan copy = new DietPlan();
        copy.setName(original.getName());
        copy.setTemplate(false);
        copy.setOriginalDietId(original.getId());
        copy.setCreatedBy(original.getCreatedBy());
        copy.setAdjustedBy(user);
        copy.setDietDescription(original.getDietDescription());
        copy.setDiets(new HashSet<>(original.getDiets()));

        List<DietDay> days = original.getDietDays().stream().map(origDay -> {
            DietDay d = new DietDay();
            d.setDayLabel(origDay.getDayLabel());
            d.setDate(origDay.getDate());
            d.setDietDayDescription(origDay.getDietDayDescription());
            d.setDiets(new HashSet<>(origDay.getDiets()));
            d.setDiet(copy);
            // voeg hier meals toe via jouw MealAssignmentUtil:
            d.setMeals(origDay.getMeals().stream()
                    .map(m -> mealAssignmentUtil.getOrAddMealToUser(userId, m.getId()))
                    .distinct()
                    .collect(Collectors.toList()));
            if (d.getMeals().stream().filter(Objects::nonNull).count() < 2) {
                throw new IllegalArgumentException("Each day must have at least 2 meals.");
            }
            return d;
        }).collect(Collectors.toList());
        copy.setDietDays(days);

        DietPlan saved = dietPlanRepository.save(copy);
        user.getSavedDietPlans().add(saved);
        userRepository.save(user);
        return dietPlanMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public DietPlanDTO updateDietPlan(Long dietPlanId, DietPlanInputDTO input, Long adjustedByUserId) {
        log.info("Updating diet plan ID: {} by user ID: {}", dietPlanId, adjustedByUserId);

        DietPlan dietPlan = dietPlanRepository.findById(dietPlanId)
                .orElseThrow(() -> new DietPlanNotFoundException("Diet plan not found with ID: " + dietPlanId));

        // Controleer of de gebruiker de eigenaar is
        if (dietPlan.getCreatedBy() == null || !dietPlan.getCreatedBy().getId().equals(adjustedByUserId)) {
            throw new SecurityException("User is not authorized to update this diet plan.");
        }

        Optional<User> adjustedBy = userRepository.findById(adjustedByUserId);
        dietPlanMapper.updateFromInputDTO(dietPlan, input, Optional.of(dietPlan.getCreatedBy()), adjustedBy);

        if (input.getDietDays() != null) {
            dietPlan.getDietDays().clear(); // oude dagen verwijderen i.v.m. orphanRemoval

            List<DietDay> newDietDays = new ArrayList<>();
            for (int i = 0; i < input.getDietDays().size(); i++) {
                DietDayInputDTO dayInput = input.getDietDays().get(i);

                List<Meal> meals = dayInput.getMealIds().stream()
                        .map(id -> mealAssignmentUtil.getOrAddMealToUser(adjustedByUserId, id))
                        .toList();

                DietDay day = dietDayMapper.toEntity(dayInput, meals, i);
                day.setDiet(dietPlan);

                Set<balancebite.model.meal.references.Diet> dietsForDay = new HashSet<>();
                for (Meal meal : meals) {
                    if (meal.getDiets() != null) {
                        dietsForDay.addAll(meal.getDiets());
                    }
                }
                day.setDiets(dietsForDay);

                newDietDays.add(day);

            }

            dietPlan.setDietDays(newDietDays);

            // Verzamel opnieuw alle diets van de dagen
            Set<balancebite.model.meal.references.Diet> allDiets = new HashSet<>();
            for (DietDay day : newDietDays) {
                allDiets.addAll(day.getDiets());
            }
            dietPlan.setDiets(allDiets);
        }

        DietPlan updated = dietPlanRepository.save(dietPlan);
        return dietPlanMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public DietPlanDTO addMealToDietDay(Long userId, Long dietId, int dayIndex, Long mealId) {
        DietPlan dietPlan = getOwnedDietPlanOrThrow(userId, dietId);
        DietDay targetDay = getDietDayOrThrow(dietPlan, dayIndex);

        Meal meal = mealAssignmentUtil.getOrAddMealToUser(userId, mealId);
        if (!targetDay.getMeals().contains(meal)) {
            targetDay.getMeals().add(meal);
        }

        DietPlan saved = dietPlanRepository.save(dietPlan);
        return dietPlanMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public DietPlanDTO removeMealFromDietDay(Long userId, Long dietPlanId, int dayIndex, Long mealId) {
        DietPlan dietPlan = getOwnedDietPlanOrThrow(userId, dietPlanId);
        DietDay targetDay = getDietDayOrThrow(dietPlan, dayIndex);

        targetDay.getMeals().removeIf(meal -> meal.getId().equals(mealId));

        DietPlan saved = dietPlanRepository.save(dietPlan);
        return dietPlanMapper.toDTO(saved);
    }

    @Override
    public DietPlanDTO getDietPlanById(Long dietPlanId, Long userId) {
        DietPlan dietPlan = dietPlanRepository.findById(dietPlanId)
                .orElseThrow(() -> new DietPlanNotFoundException("DietPlan not found with ID: " + dietPlanId));

        boolean isOwner = (dietPlan.getCreatedBy() != null && dietPlan.getCreatedBy().getId().equals(userId)) ||
                (dietPlan.getAdjustedBy() != null && dietPlan.getAdjustedBy().getId().equals(userId));

        if (!isOwner) {
            throw new SecurityException("You are not authorized to view this diet.");
        }

        return dietPlanMapper.toDTO(dietPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DietPlanDTO> getAllDietPlansForUser(
            Long userId,
            List<String> diets,
            String sortBy,
            String sortOrder,
            Pageable pageable
    ) {
        log.info("Fetching and filtering diet plans for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        List<DietPlan> dietPlans = new ArrayList<>(user.getSavedDietPlans());

        // Filter op diets
        if (diets != null && !diets.isEmpty()) {
            dietPlans.removeIf(dietPlan ->
                    dietPlan.getDiets().stream().noneMatch(d -> diets.contains(d.name()))
            );
        }

        // Sorteren
        Comparator<DietPlan> comparator = switch (sortBy != null ? sortBy.toLowerCase() : "") {
            case "name" -> Comparator.comparing(DietPlan::getName, String.CASE_INSENSITIVE_ORDER);
            case "days" -> Comparator.comparing(d -> d.getDietDays().size());
            default -> Comparator.comparing(DietPlan::getName);
        };

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }
        dietPlans.sort(comparator);

        // Pagination
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<DietPlan> paged = startItem >= dietPlans.size()
                ? Collections.emptyList()
                : dietPlans.subList(startItem, Math.min(startItem + pageSize, dietPlans.size()));

        return new PageImpl<>(paged.stream().map(dietPlanMapper::toDTO).toList(), pageable, dietPlans.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DietPlanDTO> getDietPlansCreatedByUser(Long userId, Pageable pageable) {
        log.info("Fetching diet plans created by user ID: {}", userId);
        Page<DietPlan> page = dietPlanRepository.findByCreatedBy_Id(userId, pageable);
        return page.map(dietPlanMapper::toDTO);
    }



    @Override
    @Transactional
    public DietPlanDTO removeDietDay(Long userId, Long dietPlanId, int dayIndex) {
        DietPlan dietPlan = getOwnedDietPlanOrThrow(userId, dietPlanId);
        DietDay targetDay = getDietDayOrThrow(dietPlan, dayIndex);

        dietPlan.getDietDays().remove(targetDay);

        // orphanRemoval = true zorgt ervoor dat de dag uit de DB wordt verwijderd
        DietPlan saved = dietPlanRepository.save(dietPlan);
        return dietPlanMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public UserDTO removeDietPlanFromUser(Long userId, Long dietPlanId) {
        log.info("Unlinking or deleting dietPlan ID {} from user ID {}", dietPlanId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        DietPlan diet = dietPlanRepository.findById(dietPlanId)
                .orElseThrow(() -> new DietPlanNotFoundException("DietPlan not found"));

        boolean isCreator = diet.getCreatedBy() != null && diet.getCreatedBy().getId().equals(userId);
        boolean isSaved = user.getSavedDietPlans().stream().anyMatch(d -> d.getId().equals(dietPlanId));

        if (!isCreator && !isSaved) {
            throw new DietPlanNotFoundException("DietPlan not found in user's list.");
        }

        if (diet.isTemplate()) {
            log.info("DietPlan is a template. Unlinking only, not deleting.");
            user.getSavedDietPlans().removeIf(d -> d.getId().equals(dietPlanId));
            user.getDietPlans().removeIf(d -> d.getId().equals(dietPlanId));
        } else if (!isCreator) {
            log.info("User is not the creator and diet is not a template. Deleting the copied diet.");
            user.getSavedDietPlans().removeIf(d -> d.getId().equals(dietPlanId));
            dietPlanRepository.delete(diet);
        } else {
            log.info("User is the creator. Unlinking and deleting the diet plan.");
            user.getDietPlans().removeIf(d -> d.getId().equals(dietPlanId));
            dietPlanRepository.delete(diet);
        }

        userRepository.save(user);
        return userMapper.toDTO(user);
    }

// =============================
// 🔽 Private helper methods 🔽
// =============================

    private DietPlan getOwnedDietPlanOrThrow(Long userId, Long dietPlanId) {
        DietPlan dietPlan = dietPlanRepository.findById(dietPlanId)
                .orElseThrow(() -> new DietPlanNotFoundException("Diet not found with ID: " + dietPlanId));

        boolean isOwner = (dietPlan.getCreatedBy() != null && dietPlan.getCreatedBy().getId().equals(userId)) ||
                (dietPlan.getAdjustedBy() != null && dietPlan.getAdjustedBy().getId().equals(userId));
        if (!isOwner) {
            throw new SecurityException("Not allowed to modify this diet.");
        }

        return dietPlan;
    }

    private DietDay getDietDayOrThrow(DietPlan dietPlan, int index) {
        List<DietDay> days = dietPlan.getDietDays();
        if (index < 0 || index >= days.size()) {
            throw new IllegalArgumentException("Invalid day index.");
        }
        return days.get(index);
    }


}

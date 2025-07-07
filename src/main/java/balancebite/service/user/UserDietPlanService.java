package balancebite.service.user;

import balancebite.dto.diet.DietDayInputDTO;
import balancebite.dto.diet.DietPlanDTO;
import balancebite.dto.diet.DietPlanInputDTO;
import balancebite.errorHandling.DietPlanNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.mapper.DietDayMapper;
import balancebite.mapper.DietPlanMapper;
import balancebite.model.diet.DietDay;
import balancebite.model.diet.DietPlan;
import balancebite.model.diet.SavedDietPlan;
import balancebite.model.foodItem.FoodItem;
import balancebite.model.meal.Meal;
import balancebite.model.meal.references.Diet;
import balancebite.model.user.User;
import balancebite.repository.DietPlanRepository;
import balancebite.repository.MealRepository;
import balancebite.repository.SavedDietPlanRepository;
import balancebite.repository.UserRepository;
import balancebite.service.interfaces.user.IUserDietPlanService;
import balancebite.dto.user.UserDTO;
import balancebite.mapper.UserMapper;
import balancebite.specification.DietPlanSpecification;
import balancebite.utils.MealAssignmentUtil;
import balancebite.utils.NutrientCalculatorUtil;
import balancebite.utils.ShoppingCartCalculator;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final SavedDietPlanRepository savedDietPlanRepository;


    public UserDietPlanService(DietPlanRepository dietPlanRepository,
                               UserRepository userRepository,
                               MealRepository mealRepository,
                               DietPlanMapper dietPlanMapper,
                               DietDayMapper dietDayMapper,
                               UserMapper userMapper,
                               UserMealService userMealService,
                               MealAssignmentUtil mealAssignmentUtil,
                               SavedDietPlanRepository savedDietPlanRepository) {
        this.dietPlanRepository = dietPlanRepository;
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.dietPlanMapper = dietPlanMapper;
        this.dietDayMapper = dietDayMapper;
        this.userMapper = userMapper;
        this.userMealService = userMealService;
        this.mealAssignmentUtil = mealAssignmentUtil;
        this.savedDietPlanRepository = savedDietPlanRepository;
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

        // Fallback: minstens één dag
        if (input.getDietDays() == null || input.getDietDays().isEmpty()) {
            DietDayInputDTO defaultDay = new DietDayInputDTO();
            defaultDay.setDietDayDescription("Default day description");
            defaultDay.setMealIds(new ArrayList<>());
            input.setDietDays(List.of(defaultDay));
        }

        List<DietDay> dietDays = new ArrayList<>();

        for (int i = 0; i < input.getDietDays().size(); i++) {
            DietDayInputDTO dayInput = input.getDietDays().get(i);

            List<Meal> meals = dayInput.getMealIds().stream()
                    .map(id -> mealAssignmentUtil.getOrAddMealToUser(userId, id))
                    .collect(Collectors.toList());

            long validCount = meals.stream().filter(Objects::nonNull).count();
            if (validCount < 2) {
                throw new IllegalArgumentException("Each day must have at least 2 meals.");
            }

            DietDay day = dietDayMapper.toEntity(dayInput, meals, i);
            day.setDiet(dietPlan);

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

        // Verzamel alle diets van alle dagen
        Set<balancebite.model.meal.references.Diet> allDiets = new HashSet<>();
        for (DietDay day : dietDays) {
            if (day.getDiets() != null) {
                allDiets.addAll(day.getDiets());
            }
        }
        dietPlan.setDiets(allDiets);

        // ✅ Nieuwe nutriententotalen en gemiddelden
        double totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;
        double totalSaturatedFat = 0;
        double totalUnsaturatedFat = 0;
        double totalSugars = 0;

        for (DietDay day : dietDays) {
            totalCalories += day.getTotalCalories();
            totalProtein += day.getTotalProtein();
            totalCarbs += day.getTotalCarbs();
            totalFat += day.getTotalFat();
            totalSaturatedFat += day.getTotalSaturatedFat();
            totalUnsaturatedFat += day.getTotalUnsaturatedFat();
            totalSugars += day.getTotalSugars();
        }

        int dayCount = dietDays.size();
        dietPlan.setTotalCalories(totalCalories);
        dietPlan.setTotalProtein(totalProtein);
        dietPlan.setTotalCarbs(totalCarbs);
        dietPlan.setTotalFat(totalFat);
        dietPlan.setTotalSaturatedFat(totalSaturatedFat);
        dietPlan.setTotalUnsaturatedFat(totalUnsaturatedFat);
        dietPlan.setTotalSugars(totalSugars);

        dietPlan.setAvgCalories(round1(totalCalories / dayCount));
        dietPlan.setAvgProtein(round1(totalProtein / dayCount));
        dietPlan.setAvgCarbs(round1(totalCarbs / dayCount));
        dietPlan.setAvgFat(round1(totalFat / dayCount));
        dietPlan.setAvgSaturatedFat(round1(totalSaturatedFat / dayCount));
        dietPlan.setAvgUnsaturatedFat(round1(totalUnsaturatedFat / dayCount));
        dietPlan.setAvgSugars(round1(totalSugars / dayCount));

        // Opslaan
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

        // ── Als jij de creator bent: geen kopie, gewoon relinken
        if (original.getCreatedBy() != null && original.getCreatedBy().getId().equals(userId)) {
            if (!user.getSavedDietPlans().contains(original)) {
                user.getSavedDietPlans().add(original);
                userRepository.save(user);

                // ⏱️ Registreer save van het originele plan
                SavedDietPlan savedRecord = new SavedDietPlan();
                savedRecord.setDietPlan(original);
                savedDietPlanRepository.save(savedRecord);

                long totalSaves = savedDietPlanRepository.countByDietPlan(original);
                original.setSaveCount(totalSaves);
                dietPlanRepository.save(original);
            }
            return dietPlanMapper.toDTO(original);
        }

        // ── Check op bestaande kopie
        Optional<DietPlan> existingCopy = user.getSavedDietPlans().stream()
                .filter(d -> (d.getOriginalDietId() != null && d.getOriginalDietId().equals(dietPlanId))
                        || d.getId().equals(dietPlanId))
                .findFirst();

        if (existingCopy.isPresent()) {
            DietPlan copy = existingCopy.get();
            if (!user.getSavedDietPlans().contains(copy)) {
                user.getSavedDietPlans().add(copy);
                userRepository.save(user);

                // ⏱️ Registreer save van het originele plan
                SavedDietPlan savedRecord = new SavedDietPlan();
                savedRecord.setDietPlan(original);
                savedDietPlanRepository.save(savedRecord);

                long totalSaves = savedDietPlanRepository.countByDietPlan(original);
                original.setSaveCount(totalSaves);
                dietPlanRepository.save(original);
            }
            return dietPlanMapper.toDTO(copy);
        }

        // ── Nieuwe kopie maken
        DietPlan copy = new DietPlan();
        copy.setName(original.getName());
        copy.setTemplate(false);
        copy.setOriginalDietId(original.getId());
        copy.setCreatedBy(original.getCreatedBy());
        copy.setAdjustedBy(user);
        copy.setDietDescription(original.getDietDescription());

        List<DietDay> days = original.getDietDays().stream().map(origDay -> {
            DietDay d = new DietDay();
            d.setDayLabel(origDay.getDayLabel());
            d.setDate(origDay.getDate());
            d.setDietDayDescription(origDay.getDietDayDescription());
            d.setDiets(new HashSet<>(origDay.getDiets()));
            d.setDiet(copy);

            // ⏱️ Kopieer macro-totalen van de dag
            d.setTotalCalories(origDay.getTotalCalories());
            d.setTotalProtein(origDay.getTotalProtein());
            d.setTotalCarbs(origDay.getTotalCarbs());
            d.setTotalFat(origDay.getTotalFat());
            d.setTotalSaturatedFat(origDay.getTotalSaturatedFat());
            d.setTotalUnsaturatedFat(origDay.getTotalUnsaturatedFat());
            d.setTotalSugars(origDay.getTotalSugars());

            List<Meal> assignedMeals = origDay.getMeals().stream()
                    .map(m -> mealAssignmentUtil.getOrAddMealToUser(userId, m.getId()))
                    .distinct()
                    .collect(Collectors.toList());

            if (assignedMeals.stream().filter(Objects::nonNull).count() < 2) {
                throw new IllegalArgumentException("Each day must have at least 2 meals.");
            }

            d.setMeals(assignedMeals);
            return d;
        }).collect(Collectors.toList());


        Set<balancebite.model.meal.references.Diet> allDiets = new HashSet<>();
        for (DietDay day : days) {
            for (Meal meal : day.getMeals()) {
                if (meal != null && meal.getDiets() != null) {
                    allDiets.addAll(meal.getDiets());
                }
            }
        }

        copy.setDiets(allDiets);
        copy.setDietDays(days);

        copy.setTotalCalories(original.getTotalCalories());
        copy.setTotalProtein(original.getTotalProtein());
        copy.setTotalCarbs(original.getTotalCarbs());
        copy.setTotalFat(original.getTotalFat());
        copy.setTotalSaturatedFat(original.getTotalSaturatedFat());
        copy.setTotalUnsaturatedFat(original.getTotalUnsaturatedFat());
        copy.setTotalSugars(original.getTotalSugars());

        copy.setAvgSaturatedFat(original.getAvgSaturatedFat());
        copy.setAvgUnsaturatedFat(original.getAvgUnsaturatedFat());
        copy.setAvgSugars(original.getAvgSugars());
        copy.setAvgCalories(original.getAvgCalories());
        copy.setAvgProtein(original.getAvgProtein());
        copy.setAvgCarbs(original.getAvgCarbs());
        copy.setAvgFat(original.getAvgFat());

        DietPlan saved = dietPlanRepository.save(copy);
        user.getSavedDietPlans().add(saved);
        userRepository.save(user);

        // ⏱️ Registreer save van het originele plan
        SavedDietPlan savedRecord = new SavedDietPlan();
        savedRecord.setDietPlan(original);
        savedDietPlanRepository.save(savedRecord);

        long totalSaves = savedDietPlanRepository.countByDietPlan(original);
        original.setSaveCount(totalSaves);
        dietPlanRepository.saveAndFlush(original);
        log.warn("⏱️ saveCount op {} is nu {}", original.getId(), original.getSaveCount());


        return dietPlanMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public DietPlanDTO updateDietPlan(Long dietPlanId, DietPlanInputDTO input, Long adjustedByUserId) {
        log.info("Updating diet plan ID: {} by user ID: {}", dietPlanId, adjustedByUserId);

        DietPlan dietPlan = dietPlanRepository.findById(dietPlanId)
                .orElseThrow(() -> new DietPlanNotFoundException("Diet plan not found with ID: " + dietPlanId));

        User user = userRepository.findById(adjustedByUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getSavedDietPlans().contains(dietPlan)) {
            throw new SecurityException("User is not authorized to update this diet plan.");
        }

        Optional<User> adjustedBy = userRepository.findById(adjustedByUserId);
        dietPlanMapper.updateFromInputDTO(dietPlan, input, Optional.of(dietPlan.getCreatedBy()), adjustedBy);

        if (input.getDietDays() != null) {
            List<DietDay> newDietDays = new ArrayList<>();
            for (int i = 0; i < input.getDietDays().size(); i++) {
                DietDayInputDTO dayInput = input.getDietDays().get(i);

                List<Meal> meals = dayInput.getMealIds().stream()
                        .map(id -> mealAssignmentUtil.getOrAddMealToUser(adjustedByUserId, id))
                        .toList();

                long validCount = meals.stream().filter(Objects::nonNull).count();
                if (validCount < 2) {
                    throw new IllegalArgumentException("Each day must have at least 2 meals.");
                }

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

            dietPlan.getDietDays().clear();
            dietPlan.getDietDays().addAll(newDietDays);

            Set<balancebite.model.meal.references.Diet> allDiets = new HashSet<>();
            for (DietDay day : newDietDays) {
                allDiets.addAll(day.getDiets());
            }
            dietPlan.setDiets(allDiets);
        }

        // ✅ BEREKEN DIRECTE TOTALEN EN GEMIDDELDES
        double totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;
        double totalSaturatedFat = 0;
        double totalUnsaturatedFat = 0;
        double totalSugars = 0;

        List<DietDay> days = dietPlan.getDietDays();
        int dayCount = days.size();

        for (DietDay day : days) {
            totalCalories += day.getTotalCalories();
            totalProtein += day.getTotalProtein();
            totalCarbs += day.getTotalCarbs();
            totalFat += day.getTotalFat();
            totalSaturatedFat += day.getTotalSaturatedFat();
            totalUnsaturatedFat += day.getTotalUnsaturatedFat();
            totalSugars += day.getTotalSugars();
        }

        dietPlan.setTotalCalories(totalCalories);
        dietPlan.setTotalProtein(totalProtein);
        dietPlan.setTotalCarbs(totalCarbs);
        dietPlan.setTotalFat(totalFat);
        dietPlan.setTotalSaturatedFat(totalSaturatedFat);
        dietPlan.setTotalUnsaturatedFat(totalUnsaturatedFat);
        dietPlan.setTotalSugars(totalSugars);

        dietPlan.setAvgCalories(round1(totalCalories / dayCount));
        dietPlan.setAvgProtein(round1(totalProtein / dayCount));
        dietPlan.setAvgCarbs(round1(totalCarbs / dayCount));
        dietPlan.setAvgFat(round1(totalFat / dayCount));
        dietPlan.setAvgSaturatedFat(round1(totalSaturatedFat / dayCount));
        dietPlan.setAvgUnsaturatedFat(round1(totalUnsaturatedFat / dayCount));
        dietPlan.setAvgSugars(round1(totalSugars / dayCount));

        DietPlan updated = dietPlanRepository.save(dietPlan);
        return dietPlanMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void updateDietPrivacy(Long userId, Long dietPlanId, boolean isPrivate) {
        DietPlan diet = dietPlanRepository.findById(dietPlanId)
                .orElseThrow(() -> new DietPlanNotFoundException("DietPlan not found with ID: " + dietPlanId));

        boolean isOwner = (diet.getCreatedBy() != null && diet.getCreatedBy().getId().equals(userId)) ||
                (diet.getAdjustedBy() != null && diet.getAdjustedBy().getId().equals(userId));

        if (!isOwner) {
            throw new SecurityException("User not authorized to update privacy of this diet.");
        }

        diet.setPrivate(isPrivate);
        dietPlanRepository.save(diet);
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
    public List<Map<String, Object>> getShoppingListForDietPlan(Long dietPlanId, Long userId) {
        DietPlan dietPlan = dietPlanRepository.findById(dietPlanId)
                .orElseThrow(() -> new DietPlanNotFoundException("DietPlan not found with ID: " + dietPlanId));

        boolean isOwner = (dietPlan.getCreatedBy() != null && dietPlan.getCreatedBy().getId().equals(userId)) ||
                (dietPlan.getAdjustedBy() != null && dietPlan.getAdjustedBy().getId().equals(userId));

        if (!isOwner) {
            throw new SecurityException("You are not authorized to view this diet.");
        }

        Map<FoodItem, Double> shoppingMap = ShoppingCartCalculator.calculateShoppingList(dietPlan);

        return shoppingMap.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", entry.getKey().getName());
                    item.put("quantity", entry.getValue());
                    item.put("source", entry.getKey().getSource());
                    return item;
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DietPlanDTO> getFilteredDietPlans(
            List<String> requiredDiets,
            List<String> excludedDiets,
            Long userId,
            String mode,
            Diet dietFilter,
            // avg-waarden (zonder “Avg” in de naam)
            Double minCalories,      Double maxCalories,
            Double minProtein,       Double maxProtein,
            Double minCarbs,         Double maxCarbs,
            Double minFat,           Double maxFat,
            String sortBy,
            String sortOrder,
            Pageable pageable
    ) {
        Specification<DietPlan> spec = switch (mode.toLowerCase()) {
            case "created" -> DietPlanSpecification.createdBy(userId);
            case "saved"   -> DietPlanSpecification.savedBy(userId);
            default        -> DietPlanSpecification.createdOrSavedBy(userId);
        };

        if (dietFilter != null) {
            spec = spec.and(DietPlanSpecification.hasDiet(dietFilter));
        }
        if (requiredDiets != null && !requiredDiets.isEmpty()) {
            spec = spec.and(DietPlanSpecification.mustIncludeAllDiets(
                    requiredDiets.stream().map(Diet::valueOf).collect(Collectors.toSet())
            ));
        }
        if (excludedDiets != null && !excludedDiets.isEmpty()) {
            spec = spec.and(DietPlanSpecification.mustExcludeAllDiets(
                    excludedDiets.stream().map(Diet::valueOf).collect(Collectors.toSet())
            ));
        }

// avg-filters
        if (minCalories != null && maxCalories != null) {
            spec = spec.and(DietPlanSpecification.avgCaloriesBetween(minCalories, maxCalories));
        } else if (minCalories != null) {
            spec = spec.and((root, query, cb) -> cb.ge(root.get("avgCalories"), minCalories));
        } else if (maxCalories != null) {
            spec = spec.and((root, query, cb) -> cb.le(root.get("avgCalories"), maxCalories));
        }

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


        // Zelfde mapping als in public method
        Map<String, String> sortFieldMap = Map.ofEntries(
                Map.entry("avgProtein", "avgProtein"),
                Map.entry("avgCarbs", "avgCarbs"),
                Map.entry("avgFat", "avgFat"),
                Map.entry("avgCalories", "avgCalories"),
                Map.entry("saveCount", "saveCount"),
                Map.entry("weeklySaveCount", "weeklySaveCount"),
                Map.entry("monthlySaveCount", "monthlySaveCount"),
                Map.entry("createdAt", "createdAt"),
                Map.entry("name", "name")
        );


        String mappedSortBy = sortFieldMap.getOrDefault(sortBy, "createdAt");
        Sort.Direction direction = sortOrder.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(direction, mappedSortBy));

//        log.info("Hallo!!! DIET FILTERS: minCarbs={}, maxCarbs={}, minProtein={}, maxProtein={}, minFat={}, maxFat={}, minCalories={}, maxCalories={}",
//             minCarbs, maxCarbs, minProtein, maxProtein,  minFat, maxFat, minCalories, maxCalories
//        );

        return dietPlanRepository.findAll(spec, sortedPageable)
                .map(dietPlanMapper::toDTO);
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

            // Trek 1 save af van het originele dieet
            if (diet.getOriginalDietId() != null) {
                DietPlan original = dietPlanRepository.findById(diet.getOriginalDietId())
                        .orElseThrow(() -> new DietPlanNotFoundException("Original diet not found"));

                savedDietPlanRepository.findTopByDietPlanOrderByTimestampDesc(original)
                        .ifPresent(savedDietPlanRepository::delete);
                long totalSaves = savedDietPlanRepository.countByDietPlan(original);
                original.setSaveCount(totalSaves);
                dietPlanRepository.save(original);

            }

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

    private double getValue(Map<String, balancebite.dto.NutrientInfoDTO> nutrients, String key) {
        return nutrients.getOrDefault(key, new balancebite.dto.NutrientInfoDTO(key, 0.0, "", 0L)).getValue();
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

}

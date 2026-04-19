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
import balancebite.model.foodItem.PromotedFoodItem;
import balancebite.model.meal.Meal;
import balancebite.model.meal.references.Diet;
import balancebite.model.user.User;
import balancebite.model.user.UserRole;
import balancebite.repository.*;
import balancebite.service.interfaces.user.IUserDietPlanService;
import balancebite.dto.user.UserDTO;
import balancebite.mapper.UserMapper;
import balancebite.specification.DietPlanSpecification;
import balancebite.utils.MealAssignmentUtil;
import balancebite.utils.NutrientCalculatorUtil;
import balancebite.model.user.Role;
import balancebite.model.user.userenums.Goal;
import balancebite.utils.ShoppingCartCalculator;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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
    private final SharedDietPlanAccessRepository sharedDietPlanAccessRepository;
    private final PromotedFoodItemRepository promotedFoodItemRepository;


    public UserDietPlanService(DietPlanRepository dietPlanRepository,
                               UserRepository userRepository,
                               MealRepository mealRepository,
                               DietPlanMapper dietPlanMapper,
                               DietDayMapper dietDayMapper,
                               UserMapper userMapper,
                               UserMealService userMealService,
                               MealAssignmentUtil mealAssignmentUtil,
                               SavedDietPlanRepository savedDietPlanRepository,
                               SharedDietPlanAccessRepository sharedDietPlanAccessRepository,
                               PromotedFoodItemRepository promotedFoodItemRepository) {
        this.dietPlanRepository = dietPlanRepository;
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.dietPlanMapper = dietPlanMapper;
        this.dietDayMapper = dietDayMapper;
        this.userMapper = userMapper;
        this.userMealService = userMealService;
        this.mealAssignmentUtil = mealAssignmentUtil;
        this.savedDietPlanRepository = savedDietPlanRepository;
        this.sharedDietPlanAccessRepository = sharedDietPlanAccessRepository;
        this.promotedFoodItemRepository = promotedFoodItemRepository;
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

        List<DietDay> dietDays = new ArrayList<>();

        for (int i = 0; i < input.getDietDays().size(); i++) {
            DietDayInputDTO dayInput = input.getDietDays().get(i);

            List<Long> mealIds = dayInput.getMealIds() != null ? dayInput.getMealIds() : List.of();
            List<Meal> meals = mealIds.stream()
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

        dietPlan.setGoal(detectGoal(totalProtein / dayCount, totalCarbs / dayCount, totalFat / dayCount));

        boolean isRestricted = user.getRoles().stream()
                .map(Role::getRolename)
                .anyMatch(role -> role == UserRole.RESTAURANT || role == UserRole.DIETITIAN);

        if (isRestricted) {
            dietPlan.setRestricted(true);
        }

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

        boolean hasSharedAccess =
                sharedDietPlanAccessRepository.existsByDietPlanIdAndUserId(dietPlanId, userId)
                        || sharedDietPlanAccessRepository.existsByDietPlanIdAndEmailIgnoreCase(dietPlanId, user.getEmail());

        if (original.isRestricted() || (original.isPrivate() && !hasSharedAccess)) {
            throw new AccessDeniedException("You cannot add a restricted or private diet plan.");
        }

        // ── Als jij de creator bent: geen kopie, gewoon relinken
        if (original.getCreatedBy() != null && original.getCreatedBy().getId().equals(userId)) {
            if (user.getSavedDietPlans().stream().noneMatch(d -> d.getId().equals(dietPlanId))) {
                user.getSavedDietPlans().add(original);
                userRepository.save(user);
            }
            return dietPlanMapper.toDTO(original);
        }

        // ── Check op bestaande kopie
        Optional<DietPlan> existingCopy = user.getSavedDietPlans().stream()
                .filter(d -> (d.getOriginalDietId() != null && d.getOriginalDietId().equals(dietPlanId))
                        || d.getId().equals(dietPlanId))
                .findFirst();

        if (existingCopy.isPresent()) {
            return dietPlanMapper.toDTO(existingCopy.get());
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
            throw new AccessDeniedException("User is not authorized to update this diet plan.");
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

        dietPlan.setGoal(detectGoal(totalProtein / dayCount, totalCarbs / dayCount, totalFat / dayCount));

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
    public void updateDietRestriction(Long userId, Long dietPlanId, boolean isRestricted) {
        DietPlan diet = dietPlanRepository.findById(dietPlanId)
                .orElseThrow(() -> new DietPlanNotFoundException("DietPlan not found with ID: " + dietPlanId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        boolean hasPermission = user.getRoles().stream()
                .map(Role::getRolename)
                .anyMatch(role -> role == UserRole.RESTAURANT || role == UserRole.DIETITIAN);

        if (!hasPermission) {
            throw new SecurityException("Only RESTAURANT or DIETITIAN users can update restriction status.");
        }

        diet.setRestricted(isRestricted);
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
            throw new AccessDeniedException("You are not authorized to view this diet.");
        }

        return dietPlanMapper.toDTO(dietPlan);
    }

    @Override
    public List<Map<String, Object>> getShoppingListForDietPlan(Long dietPlanId, Long userId) {
        DietPlan dietPlan = dietPlanRepository.findById(dietPlanId)
                .orElseThrow(() -> new DietPlanNotFoundException("DietPlan not found with ID: " + dietPlanId));

        boolean isOwner = (dietPlan.getCreatedBy() != null && dietPlan.getCreatedBy().getId().equals(userId)) ||
                (dietPlan.getAdjustedBy() != null && dietPlan.getAdjustedBy().getId().equals(userId));
        if (!isOwner) throw new AccessDeniedException("You are not authorized to view this diet.");

        Map<FoodItem, Double> shoppingMap = ShoppingCartCalculator.calculateShoppingList(dietPlan);

        return shoppingMap.entrySet().stream().map(entry -> {
            FoodItem fi = entry.getKey();
            double requiredGramsD = entry.getValue();
            BigDecimal requiredGrams = BigDecimal.valueOf(requiredGramsD);

            // Find active promotion (now between start and end)
            Optional<PromotedFoodItem> promoOpt = promotedFoodItemRepository.findByFoodItemId(fi.getId())
                    .filter(p -> {
                        LocalDateTime now = LocalDateTime.now();
                        return (p.getStartDate() == null || !now.isBefore(p.getStartDate()))
                                && (p.getEndDate()   == null || !now.isAfter(p.getEndDate()));
                    });

            // Effective unit price (per package)
            BigDecimal unitPrice = computeEffectivePrice(fi.getPrice(), promoOpt.orElse(null));

            // Package size in grams (how much you buy per unit)
            BigDecimal packGrams = fi.getGrams();

            Integer packsNeeded = null;
            BigDecimal totalBoughtGrams = null;
            BigDecimal leftoverGrams = null;
            BigDecimal totalCost = null;

            if (packGrams != null && packGrams.compareTo(BigDecimal.ZERO) > 0) {
                // ceil(required / pack)
                BigDecimal packs = requiredGrams.divide(packGrams, 0, RoundingMode.UP);
                packsNeeded = packs.intValue();

                totalBoughtGrams = packGrams.multiply(BigDecimal.valueOf(packsNeeded));
                leftoverGrams = totalBoughtGrams.subtract(requiredGrams);

                if (unitPrice != null) {
                    totalCost = unitPrice.multiply(BigDecimal.valueOf(packsNeeded))
                            .setScale(2, RoundingMode.HALF_UP);
                }
            }
            // If packGrams or unitPrice is missing we leave packsNeeded/totalCost as null

            boolean promoted = promoOpt.isPresent();
            String saleDescription = promoOpt.map(PromotedFoodItem::getSaleDescription).orElse(null);

            Map<String, Object> item = new HashMap<>();
            item.put("foodItemId", fi.getId());
            item.put("name", fi.getName());
            item.put("source", fi.getSource());
            item.put("requiredGrams", requiredGramsD);
            item.put("packGrams", packGrams);               // may be null
            item.put("packsNeeded", packsNeeded);           // may be null
            item.put("totalBoughtGrams", totalBoughtGrams); // may be null
            item.put("leftoverGrams", leftoverGrams);       // may be null
            item.put("unitPrice", unitPrice);               // effective per-package price (may be null)
            item.put("totalCost", totalCost);               // may be null
            item.put("hasPrice", totalCost != null);
            item.put("promoted", promoted);
            item.put("saleDescription", saleDescription);
            return item;
        }).toList();
    }

    /** Same logic as your mappers: promoPrice > salePercentage > base price. */
    private BigDecimal computeEffectivePrice(BigDecimal basePrice, PromotedFoodItem promo) {
        if (promo == null) return basePrice; // no promotion
        if (promo.getPromoPrice() != null) {
            return promo.getPromoPrice().setScale(2, RoundingMode.HALF_UP);
        }
        Integer pct = promo.getSalePercentage();
        if (basePrice != null && pct != null) {
            BigDecimal pctLeft = BigDecimal.valueOf(100 - pct);
            return basePrice.multiply(pctLeft)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        return basePrice; // percentage without basePrice → fall back to base
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DietPlanDTO> getFilteredDietPlans(
            List<String> requiredDiets,
            List<String> excludedDiets,
            Long userId,
            String mode,
            Diet dietFilter,
            Goal goal,
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
        if (goal != null) {
            spec = spec.and(DietPlanSpecification.hasGoal(goal));
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


        String mappedSortBy = sortFieldMap.getOrDefault(
                (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy,
                "createdAt"
        );

        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortOrder);
        } catch (Exception e) {
            direction = Sort.Direction.DESC;
        }

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(direction, mappedSortBy)
        );


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
            user.getSavedDietPlans().removeIf(d -> d.getId().equals(dietPlanId));
            user.getDietPlans().removeIf(d -> d.getId().equals(dietPlanId));
            userRepository.saveAndFlush(user);

            if (!dietPlanRepository.existsByOriginalDietId(dietPlanId)) {
                log.info("Template {} has no copies. Deleting completely.", dietPlanId);
                savedDietPlanRepository.deleteAllByDietPlan(diet);
                dietPlanRepository.delete(diet);
            } else {
                log.info("Template {} has copies. Only unlinking creator.", dietPlanId);
            }
            return userMapper.toDTO(user);
        } else if (!isCreator) {
            log.info("User is not the creator and diet is not a template. Deleting the copied diet.");
            user.getSavedDietPlans().removeIf(d -> d.getId().equals(dietPlanId));
            userRepository.save(user); // eerst FK-referentie verwijderen uit DB

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
            user.getSavedDietPlans().removeIf(d -> d.getId().equals(dietPlanId));
            userRepository.saveAndFlush(user);
            savedDietPlanRepository.deleteAllByDietPlan(diet);
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
            throw new AccessDeniedException("Not allowed to modify this diet.");
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

    private Goal detectGoal(double avgProtein, double avgCarbs, double avgFat) {
        double proteinKcal = avgProtein * 4;
        double carbsKcal   = avgCarbs   * 4;
        double fatKcal     = avgFat     * 9;
        double totalKcal   = proteinKcal + carbsKcal + fatKcal;

        if (totalKcal <= 0) return null;

        double p = proteinKcal / totalKcal * 100;
        double c = carbsKcal   / totalKcal * 100;
        double f = fatKcal     / totalKcal * 100;

        // [minProtein, maxProtein, minCarbs, maxCarbs, minFat, maxFat, midP, midC, midF]
        double[][] ranges = {
            {20, 25, 40, 50, 25, 35}, // WEIGHT_LOSS
            {25, 35, 35, 45, 25, 30}, // WEIGHT_LOSS_WITH_MUSCLE_MAINTENANCE
            {15, 25, 45, 55, 25, 35}, // MAINTENANCE
            {25, 30, 40, 50, 25, 30}, // MAINTENANCE_WITH_MUSCLE_FOCUS
            {15, 20, 50, 60, 25, 30}, // WEIGHT_GAIN
            {25, 30, 45, 55, 20, 30}, // WEIGHT_GAIN_WITH_MUSCLE_FOCUS
        };
        Goal[] goals = {
            Goal.WEIGHT_LOSS,
            Goal.WEIGHT_LOSS_WITH_MUSCLE_MAINTENANCE,
            Goal.MAINTENANCE,
            Goal.MAINTENANCE_WITH_MUSCLE_FOCUS,
            Goal.WEIGHT_GAIN,
            Goal.WEIGHT_GAIN_WITH_MUSCLE_FOCUS,
        };

        Goal best = null;
        double bestDeviation = Double.MAX_VALUE;

        for (int i = 0; i < ranges.length; i++) {
            double[] r = ranges[i];
            if (p >= r[0] && p <= r[1] && c >= r[2] && c <= r[3] && f >= r[4] && f <= r[5]) {
                double deviation = Math.abs(p - (r[0] + r[1]) / 2)
                        + Math.abs(c - (r[2] + r[3]) / 2)
                        + Math.abs(f - (r[4] + r[5]) / 2);
                if (deviation < bestDeviation) {
                    bestDeviation = deviation;
                    best = goals[i];
                }
            }
        }

        return best;
    }

}

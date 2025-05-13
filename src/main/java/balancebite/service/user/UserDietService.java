package balancebite.service.user;

import balancebite.dto.diet.DietDayInputDTO;
import balancebite.dto.diet.DietPlanDTO;
import balancebite.dto.diet.DietPlanInputDTO;
import balancebite.errorHandling.DietPlanNotFoundException;
import balancebite.errorHandling.DuplicateDietPlanException;
import balancebite.errorHandling.MealNotFoundException;
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
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserDietService implements IUserDietPlanService {

    private static final Logger log = LoggerFactory.getLogger(UserDietService.class);

    private final DietPlanRepository dietPlanRepository;
    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final DietPlanMapper dietPlanMapper;
    private final DietDayMapper dietDayMapper;
    private final UserMapper userMapper;
    private final UserMealService userMealService;
    private final MealAssignmentUtil mealAssignmentUtil;

    public UserDietService(DietPlanRepository dietPlanRepository,
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
        dietPlan.setCreatedBy(user);  // je kunt hier de volledige user gebruiken
        dietPlan.setTemplate(true); // altijd true bij handmatig aanmaken
        dietPlan.setOriginalDietId(null); // geen parent, dus null
        dietPlan.setDietDescription(input.getDietDescription());
        dietPlan.setDiets(input.getDiets());

        // Zorg ervoor dat er altijd minstens 1 DietDay is
        if (input.getDietDays() == null || input.getDietDays().isEmpty()) {
            DietDayInputDTO defaultDay = new DietDayInputDTO();  // Maak standaard dag aan
            defaultDay.setDietDayDescription("Default day description");
            defaultDay.setMealIds(new ArrayList<>());  // Voeg standaard maaltijden toe
            input.setDietDays(List.of(defaultDay));  // Voeg de standaard dag toe aan de input
        }

        // Voeg de DietDays toe aan het dieet
        List<DietDay> dietDays = new ArrayList<>();
        for (int i = 0; i < input.getDietDays().size(); i++) {
            DietDayInputDTO dayInput = input.getDietDays().get(i);

            // Haal maaltijden op en voeg de diets van de maaltijden toe
            Set<Meal> meals = dayInput.getMealIds().stream()
                    .map(id -> mealAssignmentUtil.getOrAddMealToUser(userId, id))
                    .collect(Collectors.toSet());
            if (meals.size() < 2) {
                throw new IllegalArgumentException("Each day must have at least 2 meals.");
            }
            DietDay day = dietDayMapper.toEntity(dayInput, meals, i);
            day.setDiet(dietPlan);

            // Voeg de diets van de maaltijden toe aan de DietDay
            if (day.getDiets() == null) {
                day.setDiets(new HashSet<>());  // Zorg ervoor dat de DietDay een niet-null Set heeft
            }

            for (Meal meal : meals) {
                day.getDiets().addAll(meal.getDiets());  // Voeg diets van de maaltijd toe aan de dag
            }

            dietDays.add(day);
        }

        dietPlan.setDietDays(dietDays);

        // Voeg alle diets van de dagen toe aan het dieet
        Set<balancebite.model.meal.references.Diet> allDiets = new HashSet<>();
        for (DietDay day : dietDays) {
            allDiets.addAll(day.getDiets());  // Voeg de diets van de dagen toe aan het dieet
        }
        dietPlan.setDiets(allDiets);

        // Sla het dieet op
        DietPlan saved = dietPlanRepository.save(dietPlan);
        log.info("Diet created with ID: {}", saved.getId());

        // Return het DietPlanDTO met de relevante informatie van de gebruiker
        return dietPlanMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public UserDTO addDietPlanToUser(Long userId, Long dietPlanId) {
        log.info("Creating a personalized copy of diet ID: {} for user ID: {}", dietPlanId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        DietPlan originalDiet = dietPlanRepository.findById(dietPlanId)
                .orElseThrow(() -> new DietPlanNotFoundException("DietPlan not found with ID: " + dietPlanId));

        // Check if user already has a copy of this diet
        boolean dietPlanExists = user.getDietPlans().stream()
                .anyMatch(d -> (d.getOriginalDietId() != null && d.getOriginalDietId().equals(dietPlanId)) || d.getId().equals(dietPlanId));

        if (dietPlanExists) {
            log.warn("User ID {} already has a copy of diet ID {}", userId, dietPlanId);
            throw new DuplicateDietPlanException("User already has a copy of this diet.");
        }

        // Create a new Diet copy
        DietPlan dietPlanCopy = new DietPlan();
        dietPlanCopy.setName(originalDiet.getName());
        dietPlanCopy.setTemplate(false);
        dietPlanCopy.setOriginalDietId(originalDiet.getId());
        dietPlanCopy.setCreatedBy(originalDiet.getCreatedBy());
        dietPlanCopy.setAdjustedBy(user);
        dietPlanCopy.setDietDescription(originalDiet.getDietDescription());
        dietPlanCopy.setDiets(originalDiet.getDiets());

        // Copy DietDays
        List<DietDay> copiedDays = new ArrayList<>();
        for (int i = 0; i < originalDiet.getDietDays().size(); i++) {
            DietDay originalDay = originalDiet.getDietDays().get(i);
            DietDay newDay = new DietDay();
            newDay.setDayLabel("Day " + (i + 1));
            newDay.setDate(originalDay.getDate());
            Set<Meal> mealsForUser = originalDay.getMeals().stream()
                    .map(meal -> mealAssignmentUtil.getOrAddMealToUser(userId, meal.getId()))
                    .collect(Collectors.toSet());

            newDay.setMeals(new ArrayList<>(mealsForUser));
            newDay.setDietDayDescription(originalDay.getDietDayDescription());
            newDay.setDiets(originalDay.getDiets());
            newDay.setDiet(dietPlanCopy);
            copiedDays.add(newDay);
        }
        dietPlanCopy.setDietDays(copiedDays);

        // Save and associate with user
        DietPlan savedDietPlan = dietPlanRepository.save(dietPlanCopy);
        user.getDietPlans().add(savedDietPlan);
        userRepository.save(user);

        log.info("Successfully created and linked a dietPlan copy with ID: {} for user ID: {}", savedDietPlan.getId(), userId);
        return userMapper.toDTO(user);
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

                Set<Meal> meals = dayInput.getMealIds().stream()
                        .map(id -> mealAssignmentUtil.getOrAddMealToUser(adjustedByUserId, id))
                        .collect(Collectors.toSet());

                DietDay day = dietDayMapper.toEntity(dayInput, meals, i);
                day.setDiet(dietPlan);
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
    public List<DietPlanDTO> getAllDietPlansForUser(Long userId) {
        log.info("Fetching all dietPlans for user ID: {}", userId);

        List<DietPlan> dietPlans = dietPlanRepository.findByCreatedBy_IdOrAdjustedBy_Id(userId, userId);
        return dietPlans.stream().map(dietPlanMapper::toDTO).collect(Collectors.toList());
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
    public void deleteDietPlan(Long dietPlanId, Long userId) {
        log.info("Deleting dietPlan ID: {} by user ID: {}", dietPlanId, userId);

        DietPlan dietPlan = dietPlanRepository.findById(dietPlanId)
                .orElseThrow(() -> new DietPlanNotFoundException("DietPlan not found with ID: " + dietPlanId));

        if (dietPlan.getCreatedBy() == null || !dietPlan.getCreatedBy().getId().equals(userId)) {
            throw new SecurityException("User is not authorized to delete this dietPlan.");
        }

        dietPlanRepository.delete(dietPlan);
        log.info("DietPlan with ID: {} successfully deleted", dietPlanId);
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

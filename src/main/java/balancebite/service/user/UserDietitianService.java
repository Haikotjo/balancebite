package balancebite.service.user;

import balancebite.dto.diet.DietDayInputDTO;
import balancebite.dto.diet.DietPlanDTO;
import balancebite.dto.diet.DietPlanInputDTO;
import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.user.ClientLinkRequestDTO;
import balancebite.errorHandling.*;
import balancebite.mapper.DietDayMapper;
import balancebite.mapper.DietPlanMapper;
import balancebite.mapper.MealMapper;
import balancebite.model.diet.DietDay;
import balancebite.model.diet.DietPlan;
import balancebite.model.diet.SharedDietPlanAccess;
import balancebite.model.meal.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.meal.SharedMealAccess;
import balancebite.model.user.PendingClient;
import balancebite.model.user.User;
import balancebite.repository.*;
import balancebite.service.FileStorageService;
import balancebite.service.interfaces.meal.IDietitianService;
import balancebite.utils.CheckForDuplicateTemplateMealUtil;
import balancebite.utils.MealAssignmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserDietitianService implements IDietitianService {

    private static final Logger log = LoggerFactory.getLogger(UserDietitianService.class);

    private final PendingClientRepository pendingClientRepository;
    private final UserRepository userRepository;
    private final MealMapper mealMapper;
    private final FileStorageService fileStorageService;
    private final MealRepository mealRepository;
    private final SharedMealAccessRepository sharedMealAccessRepository;
    private final CheckForDuplicateTemplateMealUtil checkForDuplicateTemplateMeal;
    private final DietPlanRepository dietPlanRepository;
    private final MealAssignmentUtil mealAssignmentUtil;
    private final DietPlanMapper dietPlanMapper;
    private final DietDayMapper dietDayMapper;
    private final SharedDietPlanAccessRepository sharedDietPlanAccessRepository;


    public UserDietitianService(
            PendingClientRepository pendingClientRepository,
            UserRepository userRepository,
            MealMapper mealMapper,
            FileStorageService fileStorageService,
            MealRepository mealRepository,
            SharedMealAccessRepository sharedMealAccessRepository,
            CheckForDuplicateTemplateMealUtil checkForDuplicateTemplateMeal,
            DietPlanRepository dietPlanRepository,
            MealAssignmentUtil mealAssignmentUtil,
            DietPlanMapper dietPlanMapper,
            DietDayMapper dietDayMapper,
            SharedDietPlanAccessRepository sharedDietPlanAccessRepository
    ) {
        this.pendingClientRepository = pendingClientRepository;
        this.userRepository = userRepository;
        this.mealMapper = mealMapper;
        this.fileStorageService = fileStorageService;
        this.mealRepository = mealRepository;
        this.sharedMealAccessRepository = sharedMealAccessRepository;
        this.checkForDuplicateTemplateMeal = checkForDuplicateTemplateMeal;
        this.dietPlanRepository = dietPlanRepository;
        this.mealAssignmentUtil = mealAssignmentUtil;
        this.dietPlanMapper = dietPlanMapper;
        this.dietDayMapper = dietDayMapper;
        this.sharedDietPlanAccessRepository = sharedDietPlanAccessRepository;
    }
    @Override
    @Transactional
    public void inviteClientByEmail(ClientLinkRequestDTO requestDTO, User dietitian) {
        String email = requestDTO.getClientEmail().toLowerCase().trim();

        Optional<User> existingUserOpt = userRepository.findByEmailIgnoreCase(email);

        if (existingUserOpt.isPresent()) {
            User client = existingUserOpt.get();

            // Check of de relatie al bestaat
            if (dietitian.getClients().contains(client)) {
                log.info("User {} is already a client of dietitian {}", email, dietitian.getEmail());
                return;
            }

            // Voeg toe aan clientlijst
            dietitian.getClients().add(client);
            userRepository.save(dietitian);

            log.info("Existing user {} linked as client to dietitian {}", email, dietitian.getEmail());
        } else {
            // Check of er al een pending-client is
            boolean alreadyPending = pendingClientRepository.findByEmailAndDietitian(email, dietitian).isPresent();
            if (alreadyPending) {
                log.warn("Pending client invitation already exists for email {} and dietitian ID {}", email, dietitian.getId());
                return;
            }

            // Maak nieuwe pending client aan
            PendingClient pendingClient = new PendingClient(email, dietitian);
            pendingClientRepository.save(pendingClient);

            log.info("Pending client created for email {} by dietitian ID {}", email, dietitian.getId());
        }

        // TODO: eventueel mail verzenden
    }

    @Override
    @Transactional
    public MealDTO createMealAsDietitian(MealInputDTO mealInputDTO, Long dietitianId, List<Long> sharedUserIds, List<String> sharedEmails) {
        log.info("Dietitian ID {} is creating a new private meal", dietitianId);

        // Convert DTO to Meal entity
        Meal meal = mealMapper.toEntity(mealInputDTO);
        meal.setMealTypes(mealInputDTO.getMealTypes());
        meal.setCuisines(mealInputDTO.getCuisines());
        meal.setDiets(mealInputDTO.getDiets());

        if (meal.getImageUrl() == null && mealInputDTO.getImageFile() != null && !mealInputDTO.getImageFile().isEmpty()) {
            String imageUrl = fileStorageService.saveFile(mealInputDTO.getImageFile());
            meal.setImageUrl(imageUrl);
        }

        meal.setPreparationTime(
                mealInputDTO.getPreparationTime() != null && !mealInputDTO.getPreparationTime().isBlank()
                        ? Duration.parse(mealInputDTO.getPreparationTime())
                        : null
        );

        meal.setVersion(LocalDateTime.now());

        // Check for duplicate food items (same name + quantity)
        Set<String> uniqueFoodItems = new HashSet<>();
        for (MealIngredient ingredient : meal.getMealIngredients()) {
            String key = ingredient.getFoodItem().getName() + "-" + ingredient.getQuantity();
            if (!uniqueFoodItems.add(key)) {
                throw new InvalidFoodItemException("Duplicate food item with same quantity: " + ingredient.getFoodItem().getName());
            }
        }

        List<Long> foodItemIds = meal.getMealIngredients().stream()
                .map(i -> i.getFoodItem().getId())
                .collect(Collectors.toList());
        checkForDuplicateTemplateMeal.checkForDuplicateTemplateMeal(foodItemIds, null);

        // Ophalen van de dietitian (creator)
        User dietitian = userRepository.findById(dietitianId)
                .orElseThrow(() -> new UserNotFoundException("Dietitian not found with ID: " + dietitianId));

        // Associate meal
        meal.setCreatedBy(dietitian);
        meal.setPrivate(true); // 🔒 altijd privé bij aanmaken
        meal.updateNutrients();

        Meal savedMeal = mealRepository.save(meal);
        dietitian.getSavedMeals().add(savedMeal);
        userRepository.save(dietitian);

        // Deel met specifieke users
        if (sharedUserIds != null) {
            for (Long userId : sharedUserIds) {
                userRepository.findById(userId).ifPresent(user -> {
                    SharedMealAccess access = new SharedMealAccess(savedMeal, user, null);
                    sharedMealAccessRepository.save(access);
                    log.info("SharedMealAccess created for user ID {} and meal ID {}", userId, savedMeal.getId());
                });
            }
        }

        // Deel met e-mailadres (optioneel)
        if (sharedEmails != null && !sharedEmails.isEmpty()) {
            String email = sharedEmails.get(0).trim().toLowerCase();

            // Altijd SharedMealAccess aanmaken
            SharedMealAccess access = new SharedMealAccess(savedMeal, null, email);
            sharedMealAccessRepository.save(access);
            log.info("SharedMealAccess created for email {} and meal ID {}", email, savedMeal.getId());

            Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (!dietitian.getClients().contains(user)) {
                    dietitian.getClients().add(user);
                    userRepository.save(dietitian);
                    log.info("Existing user {} linked as client to dietitian {}", email, dietitian.getEmail());
                } else {
                    log.info("User {} is already a client of dietitian {}", email, dietitian.getEmail());
                }
            }
            else {
                // Geen account nog -> uitnodigen
                boolean alreadyPending = pendingClientRepository.findByEmailAndDietitian(email, dietitian).isPresent();
                if (!alreadyPending) {
                    PendingClient pendingClient = new PendingClient(email, dietitian);
                    pendingClientRepository.save(pendingClient);
                    log.info("PendingClient created for unknown user {} by dietitian ID {}", email, dietitian.getId());
                } else {
                    log.info("PendingClient already exists for email {} and dietitian ID {}", email, dietitian.getId());
                }
            }
        }

        log.info("Private meal created by dietitian ID: {}", dietitianId);
        return mealMapper.toDTO(savedMeal);
    }

    @Override
    @Transactional
    public void addSharedAccessToMeal(Long mealId, List<Long> sharedUserIds, List<String> sharedEmails, Long dietitianId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new EntityNotFoundException("Meal not found"));

        if (!meal.getCreatedBy().getId().equals(dietitianId)) {
            throw new ForbiddenActionException("You are not the creator of this meal");
        }

        User dietitian = userRepository.findById(dietitianId)
                .orElseThrow(() -> new EntityNotFoundException("Dietitian not found"));

        // Deel met userIds
        if (sharedUserIds != null) {
            for (Long userId : sharedUserIds) {
                boolean alreadyShared = sharedMealAccessRepository.existsByMealIdAndUserId(mealId, userId);
                if (!alreadyShared) {
                    userRepository.findById(userId).ifPresent(user -> {
                        SharedMealAccess access = new SharedMealAccess(meal, user, null);
                        sharedMealAccessRepository.save(access);
                        log.info("Meal {} shared with user {}", mealId, userId);
                    });
                }
            }
        }

        // Deel met e-mails
        if (sharedEmails != null) {
            for (String email : sharedEmails) {
                String cleanEmail = email.trim().toLowerCase();
                boolean alreadyShared = sharedMealAccessRepository.existsByMealIdAndEmail(mealId, cleanEmail);

                if (!alreadyShared) {
                    SharedMealAccess access = new SharedMealAccess(meal, null, cleanEmail);
                    sharedMealAccessRepository.save(access);
                    log.info("Meal {} shared with email {}", mealId, cleanEmail);
                }

                Optional<User> userOpt = userRepository.findByEmailIgnoreCase(cleanEmail);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    if (!dietitian.getClients().contains(user)) {
                        dietitian.getClients().add(user);
                        userRepository.save(dietitian);
                        log.info("User {} added as client to dietitian {}", cleanEmail, dietitian.getEmail());
                    }
                } else {
                    boolean alreadyPending = pendingClientRepository.findByEmailAndDietitian(cleanEmail, dietitian).isPresent();
                    if (!alreadyPending) {
                        PendingClient pendingClient = new PendingClient(cleanEmail, dietitian);
                        pendingClientRepository.save(pendingClient);
                        log.info("Pending client created for email {} and dietitian ID {}", cleanEmail, dietitian.getId());
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public DietPlanDTO createDietPlanAsDietitian(
            DietPlanInputDTO input,
            Long dietitianId,
            List<Long> sharedUserIds,
            List<String> sharedEmails
    ) {
        log.info("Dietitian ID {} is creating a private diet plan", dietitianId);

        User dietitian = userRepository.findById(dietitianId)
                .orElseThrow(() -> new UserNotFoundException("Dietitian not found with ID: " + dietitianId));

        DietPlan dietPlan = new DietPlan();
        dietPlan.setName(input.getName());
        dietPlan.setCreatedBy(dietitian);
        dietPlan.setTemplate(true);
        dietPlan.setRestricted(true); // 🔒 altijd restricted voor dietitians
        dietPlan.setPrivate(true);    // 🔒 altijd private
        dietPlan.setOriginalDietId(null);
        dietPlan.setDietDescription(input.getDietDescription());
        dietPlan.setDiets(input.getDiets());

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
                    .map(id -> mealAssignmentUtil.getOrAddMealToUser(dietitianId, id))
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

        // Bereken macro's
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
        dietitian.getSavedDietPlans().add(saved);
        userRepository.save(dietitian);

        // Gedeelde toegang opslaan
        if (sharedUserIds != null) {
            for (Long userId : sharedUserIds) {
                userRepository.findById(userId).ifPresent(user -> {
                    SharedDietPlanAccess access = new SharedDietPlanAccess(saved, user, null);
                    sharedDietPlanAccessRepository.save(access);
                    log.info("SharedDietPlanAccess created for user ID {} and diet plan ID {}", userId, saved.getId());
                });
            }
        }

        if (sharedEmails != null && !sharedEmails.isEmpty()) {
            String email = sharedEmails.get(0).trim().toLowerCase();

            // Altijd SharedDietPlanAccess aanmaken
            SharedDietPlanAccess access = new SharedDietPlanAccess(saved, null, email);
            sharedDietPlanAccessRepository.save(access);
            log.info("SharedDietPlanAccess created for email {} and diet plan ID {}", email, saved.getId());

            Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (!dietitian.getClients().contains(user)) {
                    dietitian.getClients().add(user);
                    userRepository.save(dietitian);
                    log.info("Existing user {} linked as client to dietitian {}", email, dietitian.getEmail());
                } else {
                    log.info("User {} is already a client of dietitian {}", email, dietitian.getEmail());
                }
            } else {
                // Geen account nog -> uitnodigen
                boolean alreadyPending = pendingClientRepository.findByEmailAndDietitian(email, dietitian).isPresent();
                if (!alreadyPending) {
                    PendingClient pendingClient = new PendingClient(email, dietitian);
                    pendingClientRepository.save(pendingClient);
                    log.info("PendingClient created for unknown user {} by dietitian ID {}", email, dietitian.getId());
                } else {
                    log.info("PendingClient already exists for email {} and dietitian ID {}", email, dietitian.getId());
                }
            }
        }

        log.info("Private diet plan created by dietitian ID: {}", dietitianId);
        return dietPlanMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void addSharedAccessToDietPlan(Long dietPlanId, List<Long> sharedUserIds, List<String> sharedEmails, Long dietitianId) {
        DietPlan dietPlan = dietPlanRepository.findById(dietPlanId)
                .orElseThrow(() -> new EntityNotFoundException("Diet plan not found"));

        if (!dietPlan.getCreatedBy().getId().equals(dietitianId)) {
            throw new ForbiddenActionException("You are not the creator of this diet plan");
        }
        // Deel met users
        if (sharedUserIds != null) {
            for (Long userId : sharedUserIds) {
                boolean alreadyShared = sharedDietPlanAccessRepository.existsByDietPlanIdAndUserId(dietPlanId, userId);
                if (!alreadyShared) {
                    userRepository.findById(userId).ifPresent(user -> {
                        SharedDietPlanAccess access = new SharedDietPlanAccess(dietPlan, user, null);
                        sharedDietPlanAccessRepository.save(access);
                        log.info("Diet plan {} shared with user {}", dietPlanId, userId);
                    });
                }
            }
        }

        // Deel met e-mails
        if (sharedEmails != null && !sharedEmails.isEmpty()) {
            String cleanEmail = sharedEmails.get(0).trim().toLowerCase();
            boolean alreadyShared = sharedDietPlanAccessRepository.existsByDietPlanIdAndEmail(dietPlanId, cleanEmail);

            if (!alreadyShared) {
                SharedDietPlanAccess access = new SharedDietPlanAccess(dietPlan, null, cleanEmail);
                sharedDietPlanAccessRepository.save(access);
                log.info("Diet plan {} shared with email {}", dietPlanId, cleanEmail);
            }

            Optional<User> userOpt = userRepository.findByEmailIgnoreCase(cleanEmail);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                if (!dietPlan.getCreatedBy().getClients().contains(user)) {
                    dietPlan.getCreatedBy().getClients().add(user);
                    userRepository.save(dietPlan.getCreatedBy());
                    log.info("User {} added as client to dietitian {}", cleanEmail, dietPlan.getCreatedBy().getEmail());
                }
            } else {
                boolean alreadyPending = pendingClientRepository.findByEmailAndDietitian(cleanEmail, dietPlan.getCreatedBy()).isPresent();
                if (!alreadyPending) {
                    PendingClient pendingClient = new PendingClient(cleanEmail, dietPlan.getCreatedBy());
                    pendingClientRepository.save(pendingClient);
                    log.info("Pending client created for email {} and dietitian ID {}", cleanEmail, dietPlan.getCreatedBy().getId());
                }
            }
        }
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}

package balancebite.service.user;

import balancebite.dto.diet.DietDTO;
import balancebite.dto.diet.DietInputDTO;
import balancebite.dto.diet.DietDayInputDTO;
import balancebite.errorHandling.DietNotFoundException;
import balancebite.errorHandling.DuplicateDietException;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.mapper.DietMapper;
import balancebite.mapper.DietDayMapper;
import balancebite.model.diet.Diet;
import balancebite.model.diet.DietDay;
import balancebite.model.meal.Meal;
import balancebite.model.user.User;
import balancebite.repository.DietRepository;
import balancebite.repository.MealRepository;
import balancebite.repository.UserRepository;
import balancebite.service.interfaces.diet.IUserDietService;
import balancebite.dto.user.UserDTO;
import balancebite.mapper.UserMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserDietService implements IUserDietService {

    private static final Logger log = LoggerFactory.getLogger(UserDietService.class);

    private final DietRepository dietRepository;
    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final DietMapper dietMapper;
    private final DietDayMapper dietDayMapper;
    private final UserMapper userMapper;

    public UserDietService(DietRepository dietRepository,
                       UserRepository userRepository,
                       MealRepository mealRepository,
                       DietMapper dietMapper,
                       DietDayMapper dietDayMapper,
                       UserMapper userMapper) {
        this.dietRepository = dietRepository;
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.dietMapper = dietMapper;
        this.dietDayMapper = dietDayMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public DietDTO createDiet(DietInputDTO input, Long userId) {
        log.info("Creating new diet for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Diet diet = new Diet();
        diet.setName(input.getName());
        diet.setCreatedBy(user);
        diet.setTemplate(false); // altijd false bij handmatig aanmaken
        diet.setOriginalDietId(null); // geen parent, dus null
        diet.setVersion(LocalDateTime.now());

        if (input.getDietDays() != null && !input.getDietDays().isEmpty()) {
            List<DietDay> dietDays = new ArrayList<>();
            for (int i = 0; i < input.getDietDays().size(); i++) {
                DietDayInputDTO dayInput = input.getDietDays().get(i);
                Set<Meal> meals = dayInput.getMealIds().stream()
                        .map(id -> mealRepository.findById(id)
                                .orElseThrow(() -> new MealNotFoundException("Meal not found with ID: " + id)))
                        .collect(Collectors.toSet());

                DietDay day = new DietDay();
                day.setDayLabel("Day " + (i + 1));
                day.setDate(dayInput.getDate()); // mag null zijn
                day.setMeals(new ArrayList<>(meals));
                day.setDiet(diet);
                dietDays.add(day);
            }
            diet.setDietDays(dietDays);
        }

        Diet saved = dietRepository.save(diet);
        log.info("Diet created with ID: {}", saved.getId());
        return dietMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public UserDTO addDietToUser(Long userId, Long dietId) {
        log.info("Creating a personalized copy of diet ID: {} for user ID: {}", dietId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Diet originalDiet = dietRepository.findById(dietId)
                .orElseThrow(() -> new DietNotFoundException("Diet not found with ID: " + dietId));

        // Check if user already has a copy of this diet
        boolean dietExists = user.getDiets().stream()
                .anyMatch(d -> (d.getOriginalDietId() != null && d.getOriginalDietId().equals(dietId)) || d.getId().equals(dietId));

        if (dietExists) {
            log.warn("User ID {} already has a copy of diet ID {}", userId, dietId);
            throw new DuplicateDietException("User already has a copy of this diet.");
        }

        // Create a new Diet copy
        Diet dietCopy = new Diet();
        dietCopy.setName(originalDiet.getName());
        dietCopy.setTemplate(false);
        dietCopy.setOriginalDietId(originalDiet.getId());
        dietCopy.setCreatedBy(originalDiet.getCreatedBy());
        dietCopy.setAdjustedBy(user);
        dietCopy.setVersion(LocalDateTime.now());

        // Copy DietDays
        List<DietDay> copiedDays = new ArrayList<>();
        for (int i = 0; i < originalDiet.getDietDays().size(); i++) {
            DietDay originalDay = originalDiet.getDietDays().get(i);
            DietDay newDay = new DietDay();
            newDay.setDayLabel("Day " + (i + 1));
            newDay.setDate(originalDay.getDate());
            newDay.setMeals(new ArrayList<>(originalDay.getMeals()));
            newDay.setDiet(dietCopy);
            copiedDays.add(newDay);
        }
        dietCopy.setDietDays(copiedDays);

        // Save and associate with user
        Diet savedDiet = dietRepository.save(dietCopy);
        user.getDiets().add(savedDiet);
        userRepository.save(user);

        log.info("Successfully created and linked a diet copy with ID: {} for user ID: {}", savedDiet.getId(), userId);
        return userMapper.toDTO(user);
    }

    @Override
    public DietDTO getDietById(Long dietId, Long userId) {
        Diet diet = dietRepository.findById(dietId)
                .orElseThrow(() -> new DietNotFoundException("Diet not found with ID: " + dietId));

        boolean isOwner = (diet.getCreatedBy() != null && diet.getCreatedBy().getId().equals(userId)) ||
                (diet.getAdjustedBy() != null && diet.getAdjustedBy().getId().equals(userId));

        if (!isOwner) {
            throw new SecurityException("You are not authorized to view this diet.");
        }

        return dietMapper.toDTO(diet);
    }


    @Override
    public List<DietDTO> getAllDietsForUser(Long userId) {
        log.info("Fetching all diets for user ID: {}", userId);

        List<Diet> diets = dietRepository.findByCreatedBy_IdOrAdjustedBy_Id(userId, userId);
        return diets.stream().map(dietMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public DietDTO updateDiet(Long dietId, DietInputDTO input, Long adjustedByUserId) {
        log.info("Updating diet ID: {} by user ID: {}", dietId, adjustedByUserId);

        Diet diet = dietRepository.findById(dietId)
                .orElseThrow(() -> new DietNotFoundException("Diet not found with ID: " + dietId));

        Optional<User> adjustedBy = userRepository.findById(adjustedByUserId);
        dietMapper.updateFromInputDTO(diet, input, Optional.ofNullable(diet.getCreatedBy()), adjustedBy);
        diet.setVersion(LocalDateTime.now());

        if (input.getDietDays() != null) {
            List<DietDay> dietDays = new ArrayList<>();
            for (int i = 0; i < input.getDietDays().size(); i++) {
                DietDayInputDTO dayInput = input.getDietDays().get(i);
                Set<Meal> meals = dayInput.getMealIds().stream()
                        .map(id -> mealRepository.findById(id)
                                .orElseThrow(() -> new MealNotFoundException("Meal not found with ID: " + id)))
                        .collect(Collectors.toSet());
                DietDay day = dietDayMapper.toEntity(dayInput, meals, i);
                day.setDiet(diet);
                dietDays.add(day);
            }
            diet.setDietDays(dietDays);
        }

        Diet updated = dietRepository.save(diet);
        return dietMapper.toDTO(updated);
    }

    @Override
    public void deleteDiet(Long dietId, Long userId) {
        log.info("Deleting diet ID: {} by user ID: {}", dietId, userId);

        Diet diet = dietRepository.findById(dietId)
                .orElseThrow(() -> new DietNotFoundException("Diet not found with ID: " + dietId));

        if (diet.getCreatedBy() == null || !diet.getCreatedBy().getId().equals(userId)) {
            throw new SecurityException("User is not authorized to delete this diet.");
        }

        dietRepository.delete(diet);
        log.info("Diet with ID: {} successfully deleted", dietId);
    }
}

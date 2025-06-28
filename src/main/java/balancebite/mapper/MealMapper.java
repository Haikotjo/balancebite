package balancebite.mapper;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.mealingredient.MealIngredientDTO;
import balancebite.dto.mealingredient.MealIngredientInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.InvalidFoodItemException;
import balancebite.model.foodItem.FoodItem;
import balancebite.model.meal.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.user.User;
import balancebite.repository.FoodItemRepository;
import balancebite.repository.SavedDietPlanRepository;
import balancebite.repository.SavedMealRepository;
import balancebite.repository.UserRepository;
import balancebite.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Meal entities and DTOs.
 * Handles the transformation of Meal entities, including their ingredients,
 * into corresponding Data Transfer Objects (DTOs).
 */
@Component
public class MealMapper {
    private static final Logger log = LoggerFactory.getLogger(MealMapper.class);

    private final FoodItemRepository foodItemRepository;
    private final MealIngredientMapper mealIngredientMapper;
    private final FileStorageService fileStorageService;
    private final SavedMealRepository savedMealRepository;
    private final UserMapper userMapper;

    public MealMapper(FoodItemRepository foodItemRepository, MealIngredientMapper mealIngredientMapper, FileStorageService fileStorageService, SavedMealRepository savedMealRepository, @Lazy UserMapper userMapper) {
        this.foodItemRepository = foodItemRepository;
        this.mealIngredientMapper = mealIngredientMapper;
        this.fileStorageService = fileStorageService;
        this.savedMealRepository = savedMealRepository;
        this.userMapper = userMapper;
    }

    /**
     * Converts a Meal entity to a MealDTO.
     * This includes converting the meal's ingredients, user count, creator, adjusted user, and optional image fields.
     *
     * @param meal the Meal entity to be converted.
     * @return the created MealDTO.
     */
    public MealDTO toDTO(Meal meal) {
        log.info("Converting Meal entity to MealDTO for meal ID: {}", meal.getId());
        if (meal == null) {
            log.warn("Received null Meal entity, returning null for MealDTO.");
            return null;
        }

        long saveCount = Optional.ofNullable(meal.getSaveCount()).orElse(0L);
        long weeklySaveCount = Optional.ofNullable(meal.getWeeklySaveCount()).orElse(0L);
        long monthlySaveCount = Optional.ofNullable(meal.getMonthlySaveCount()).orElse(0L);

        MealDTO dto = new MealDTO(
                meal.getId(),
                meal.getName(),
                meal.getMealDescription(),
                meal.getImage(),
                meal.getImageUrl(),
                meal.getOriginalMealId(),
                meal.getVersion(),
                meal.getMealIngredients().stream()
                        .map(this::toMealIngredientDTO)
                        .collect(Collectors.toList()),
                meal.getCreatedBy() != null ? userMapper.toPublicUserDTO(meal.getCreatedBy()) : null,
                meal.getAdjustedBy() != null ? userMapper.toPublicUserDTO(meal.getAdjustedBy()) : null,
                meal.isTemplate(),
                meal.isPrivate(),
                meal.getMealTypes(),
                meal.getCuisines(),
                meal.getDiets(),
                meal.getTotalCalories(),
                meal.getTotalProtein(),
                meal.getTotalCarbs(),
                meal.getTotalFat(),
                meal.getFoodItemsString(),
                meal.getPreparationTime() != null ? meal.getPreparationTime().toString() : null,
                saveCount,
                weeklySaveCount,
                monthlySaveCount
        );

        log.debug("Finished converting Meal entity to MealDTO: {}", dto);
        return dto;
    }



    /**
     * Converts a MealInputDTO to a Meal entity.
     * Used for creating or updating a Meal entity, including optional image fields.
     *
     * @param mealInputDTO the MealInputDTO containing the meal data.
     * @return the Meal entity created from the input DTO.
     */
    public Meal toEntity(MealInputDTO mealInputDTO) {
        log.info("Converting MealInputDTO to Meal entity.");
        return Optional.ofNullable(mealInputDTO)
                .map(dto -> {
                    log.debug("Mapping fields from MealInputDTO to Meal entity.");
                    Meal meal = new Meal();
                    meal.setName(dto.getName());
                    meal.setMealDescription(dto.getMealDescription());
                    meal.setMealTypes(dto.getMealTypes());
                    meal.setCuisines(dto.getCuisines());
                    meal.setDiets(dto.getDiets());
                    meal.setPrivate(dto.isPrivate());

                    if (dto.getPreparationTime() != null && !dto.getPreparationTime().isBlank()) {
                        meal.setPreparationTime(java.time.Duration.parse(dto.getPreparationTime()));
                    }

                    // Verwerk Base64 of URL afbeelding
                    if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
                        String imageUrl = fileStorageService.saveFile(dto.getImageFile());
                        meal.setImageUrl(imageUrl);
                    } else if (dto.getImage() != null) {
                        meal.setImage(dto.getImage());
                    } else if (dto.getImageUrl() != null) {
                        meal.setImageUrl(dto.getImageUrl()); // Zet expliciet de imageUrl
                    }

                    List<MealIngredient> mealIngredients = dto.getMealIngredients().stream()
                            .map(input -> mealIngredientMapper.toEntity(input, meal))
                            .collect(Collectors.toList());
                    meal.addMealIngredients(mealIngredients);

                    meal.setCreatedBy(dto.getCreatedBy() != null ? toUserEntity(dto.getCreatedBy()) : null);
                    log.debug("Finished mapping MealInputDTO to Meal entity: {}", meal);
                    return meal;
                })
                .orElseGet(() -> {
                    log.warn("Received null MealInputDTO, returning null for Meal entity.");
                    return null;
                });
    }

    /**
     * Converts a MealIngredient entity to a MealIngredientDTO.
     *
     * @param mealIngredient the MealIngredient entity to be converted.
     * @return the MealIngredientDTO created from the entity.
     */
    private MealIngredientDTO toMealIngredientDTO(MealIngredient mealIngredient) {
        log.info("Converting MealIngredient entity to MealIngredientDTO for ingredient ID: {}", mealIngredient.getId());
        MealIngredientDTO dto = new MealIngredientDTO(
                mealIngredient.getId(),
                mealIngredient.getMeal().getId(),
                mealIngredient.getFoodItem() != null ? mealIngredient.getFoodItem().getId() : null,
                mealIngredient.getFoodItemName(),
                mealIngredient.getQuantity()
        );
        log.debug("Finished converting MealIngredient entity to MealIngredientDTO: {}", dto);
        return dto;
    }

    /**
     * Converts a MealIngredientInputDTO to a MealIngredient entity.
     *
     * @param inputDTO the MealIngredientInputDTO containing the ingredient data.
     * @return the MealIngredient entity created from the input DTO.
     */
    private MealIngredient toMealIngredientEntity(MealIngredientInputDTO inputDTO) {
        log.info("Converting MealIngredientInputDTO to MealIngredient entity.");
        MealIngredient ingredient = new MealIngredient();
        ingredient.setQuantity(inputDTO.getQuantity());

        FoodItem foodItem = foodItemRepository.findById(inputDTO.getFoodItemId())
                .orElseThrow(() -> {
                    log.error("Invalid food item ID: {}", inputDTO.getFoodItemId());
                    return new InvalidFoodItemException("Invalid food item ID: " + inputDTO.getFoodItemId());
                });
        ingredient.setFoodItem(foodItem);

        log.debug("Finished converting MealIngredientInputDTO to MealIngredient entity: {}", ingredient);
        return ingredient;
    }

    /**
     * Converts a User entity to a UserDTO.
     *
     * @param user the User entity to be converted.
     * @return the UserDTO created from the entity.
     */
    private UserDTO toUserDTO(User user) {
        log.info("Converting User entity to UserDTO for user ID: {}", user.getId());
        UserDTO dto = new UserDTO(user.getId(), user.getUserName(), user.getEmail());
        log.debug("Finished converting User entity to UserDTO: {}", dto);
        return dto;
    }

    /**
     * Converts a UserDTO to a User entity.
     *
     * @param userDTO the UserDTO containing the user data.
     * @return the User entity created from the DTO.
     */
    private User toUserEntity(UserDTO userDTO) {
        log.info("Converting UserDTO to User entity.");
        return Optional.ofNullable(userDTO)
                .map(dto -> {
                    User user = new User();
                    user.setUserName(dto.getUserName());
                    user.setEmail(dto.getEmail());
                    log.debug("Finished converting UserDTO to User entity: {}", user);
                    return user;
                })
                .orElse(null);
    }
}

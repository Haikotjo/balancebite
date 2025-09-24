package balancebite.mapper;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.mealingredient.MealIngredientDTO;
import balancebite.dto.mealingredient.MealIngredientInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.InvalidFoodItemException;
import balancebite.model.MealIngredient;
import balancebite.model.foodItem.FoodItem;
import balancebite.model.meal.Meal;
import balancebite.model.user.User;
import balancebite.repository.FoodItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Maps between Meal entities and DTOs.
 *
 * IMPORTANT:
 * - This mapper NEVER uploads files. It only copies fields.
 * - If a MultipartFile was provided (dto.getImageFile()),
 *   your Service layer must upload it (e.g., Cloudinary) and set meal.setImageUrl(...)
 *   BEFORE saving the entity.
 * - Direct imageUrl and base64 image are passed through if present.
 */
@Component
public class MealMapper {

    private static final Logger log = LoggerFactory.getLogger(MealMapper.class);

    private final FoodItemRepository foodItemRepository;
    private final MealIngredientMapper mealIngredientMapper;
    private final UserMapper userMapper;
    private final FoodItemMapper foodItemMapper;

    public MealMapper(FoodItemRepository foodItemRepository,
                      MealIngredientMapper mealIngredientMapper,
                      @Lazy UserMapper userMapper,
                      FoodItemMapper foodItemMapper) {
        this.foodItemRepository = foodItemRepository;
        this.mealIngredientMapper = mealIngredientMapper;
        this.userMapper = userMapper;
        this.foodItemMapper = foodItemMapper;
    }

    // -------- Entity -> DTO --------

  import java.math.BigDecimal;           // add
import java.math.RoundingMode;         // add
import java.util.Objects;              // add
import java.util.stream.Collectors;    // already present

    public MealDTO toDTO(Meal meal) {
        if (meal == null) {
            log.warn("toDTO called with null Meal");
            return null;
        }

        long saveCount        = Optional.ofNullable(meal.getSaveCount()).orElse(0L);
        long weeklySaveCount  = Optional.ofNullable(meal.getWeeklySaveCount()).orElse(0L);
        long monthlySaveCount = Optional.ofNullable(meal.getMonthlySaveCount()).orElse(0L);

        // Map ingredients once
        var items = meal.getMealIngredients().stream()
                .map(mealIngredientMapper::toDTO)
                .collect(Collectors.toList());

        // Sum known item costs -> mealPrice (nullable if none known)
        var knownCosts = items.stream()
                .map(MealIngredientDTO::getItemCost)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        BigDecimal mealPrice = knownCosts.isEmpty()
                ? null
                : knownCosts.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        // Any unknown prices?
        boolean hasUnknownPrices = items.stream()
                .anyMatch(i -> i.getItemCost() == null);

        return new MealDTO(
                meal.getId(),
                meal.getName(),
                meal.getMealDescription(),
                meal.getImage(),
                meal.getImageUrl(),
                meal.getOriginalMealId(),
                meal.getVersion(),
                items, // use the mapped list
                meal.getCreatedBy()  != null ? userMapper.toPublicUserDTO(meal.getCreatedBy())   : null,
                meal.getAdjustedBy() != null ? userMapper.toPublicUserDTO(meal.getAdjustedBy()) : null,
                meal.isTemplate(),
                meal.isPrivate(),
                meal.isRestricted(),
                meal.getMealTypes(),
                meal.getCuisines(),
                meal.getDiets(),
                Optional.ofNullable(meal.getTotalCalories()).orElse(0.0),
                Optional.ofNullable(meal.getTotalProtein()).orElse(0.0),
                Optional.ofNullable(meal.getTotalCarbs()).orElse(0.0),
                Optional.ofNullable(meal.getTotalSugars()).orElse(0.0),
                Optional.ofNullable(meal.getTotalSaturatedFat()).orElse(0.0),
                Optional.ofNullable(meal.getTotalUnsaturatedFat()).orElse(0.0),
                Optional.ofNullable(meal.getTotalFat()).orElse(0.0),
                meal.getFoodItemsString(),
                meal.getPreparationTime() != null ? meal.getPreparationTime().toString() : null,
                saveCount,
                weeklySaveCount,
                monthlySaveCount,
                mealPrice,
                hasUnknownPrices
        );
    }


    // -------- DTO -> Entity --------

    /**
     * Builds a new Meal entity from MealInputDTO.
     *
     * IMAGE RULES:
     * - No upload is performed here.
     * - If dto.imageUrl is present, it is copied to entity.imageUrl.
     * - If dto.image (base64) is present, it is copied to entity.image.
     * - If dto.imageFile is present, DO NOTHING here (Service must upload and set imageUrl).
     */
    public Meal toEntity(MealInputDTO dto) {
        if (dto == null) {
            log.warn("toEntity called with null MealInputDTO");
            return null;
        }

        Meal meal = new Meal();
        meal.setName(dto.getName());
        meal.setMealDescription(dto.getMealDescription());
        meal.setMealTypes(dto.getMealTypes());
        meal.setCuisines(dto.getCuisines());
        meal.setDiets(dto.getDiets());
        meal.setPrivate(dto.isPrivate());
        meal.setRestricted(dto.isRestricted());

        if (dto.getPreparationTime() != null && !dto.getPreparationTime().isBlank()) {
            meal.setPreparationTime(java.time.Duration.parse(dto.getPreparationTime()));
        }

        // Image pass-through (NO upload here)
        if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
            meal.setImageUrl(dto.getImageUrl());
        }
        if (dto.getImage() != null) {
            meal.setImage(dto.getImage()); // if you still support base64 storage
        }
        // dto.getImageFile() is intentionally ignored here.

        // Ingredients
        List<MealIngredient> mealIngredients = dto.getMealIngredients().stream()
                .map(input -> mealIngredientMapper.toEntity(input, meal))
                .collect(Collectors.toList());
        meal.addMealIngredients(mealIngredients);

        // Creator
        meal.setCreatedBy(dto.getCreatedBy() != null ? toUserEntity(dto.getCreatedBy()) : null);

        return meal;
    }

    // -------- Helpers --------

    private UserDTO toUserDTO(User user) {
        if (user == null) return null;
        return new UserDTO(user.getId(), user.getUserName(), user.getEmail());
    }

    private User toUserEntity(UserDTO userDTO) {
        if (userDTO == null) return null;
        User user = new User();
        user.setUserName(userDTO.getUserName());
        user.setEmail(userDTO.getEmail());
        return user;
    }

    // If you still need this for other mappers, keep it; otherwise you can remove it.
    @SuppressWarnings("unused")
    private MealIngredient toMealIngredientEntity(MealIngredientInputDTO inputDTO) {
        MealIngredient ingredient = new MealIngredient();
        ingredient.setQuantity(inputDTO.getQuantity());

        FoodItem foodItem = foodItemRepository.findById(inputDTO.getFoodItemId())
                .orElseThrow(() -> new InvalidFoodItemException("Invalid food item ID: " + inputDTO.getFoodItemId()));
        ingredient.setFoodItem(foodItem);

        return ingredient;
    }
}

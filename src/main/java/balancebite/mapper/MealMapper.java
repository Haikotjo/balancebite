package balancebite.mapper;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.mealingredient.MealIngredientDTO;
import balancebite.dto.mealingredient.MealIngredientInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.InvalidFoodItemException;
import balancebite.model.FoodItem;
import balancebite.model.Meal;
import balancebite.model.MealIngredient;
import balancebite.model.User;
import balancebite.repository.FoodItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

    public MealMapper(FoodItemRepository foodItemRepository, MealIngredientMapper mealIngredientMapper) {
        this.foodItemRepository = foodItemRepository;
        this.mealIngredientMapper = mealIngredientMapper;
    }

    /**
     * Converts a Meal entity to a MealDTO.
     * This includes converting the meal's ingredients, user count, the creator, and adjusted user if applicable.
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

        MealDTO dto = new MealDTO(
                meal.getId(),
                meal.getName(),
                meal.getMealDescription(),
                meal.getMealIngredients().stream()
                        .map(this::toMealIngredientDTO)
                        .collect(Collectors.toList()),
                meal.getUserCount(),
                meal.getCreatedBy() != null ? toUserDTO(meal.getCreatedBy()) : null,
                meal.getAdjustedBy() != null ? toUserDTO(meal.getAdjustedBy()) : null
        );
        log.debug("Finished converting Meal entity to MealDTO: {}", dto);
        return dto;
    }

    /**
     * Converts a MealInputDTO to a Meal entity.
     * Used for creating or updating a Meal entity.
     * The fields adjustedBy and isTemplate are managed by the service logic and not set from the input DTO.
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

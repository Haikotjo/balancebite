package balancebite.service;

import balancebite.dto.NutrientInfoDTO;
import balancebite.dto.user.UserBasicInfoInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserDetailsInputDTO;
import balancebite.mapper.UserMapper;
import balancebite.model.Meal;
import balancebite.model.Nutrient;
import balancebite.model.RecommendedDailyIntake;
import balancebite.model.User;
import balancebite.repository.MealRepository;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing users.
 */
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final MealRepository mealRepository;

    private final RecommendedDailyIntakeRepository recommendedDailyIntakeRepository;
    private final UserMapper userMapper;
    private final MealService mealService;  // Add MealService

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Constructor that initializes the UserService with the required repositories, mappers, and services.
     *
     * @param userRepository Repository to interact with the User data in the database.
     * @param mealRepository Repository to interact with the Meal data in the database.
     * @param userMapper Mapper to convert between User entities and DTOs.
     * @param mealService Service to interact with Meal-related operations.
     */
    public UserService(UserRepository userRepository, MealRepository mealRepository, UserMapper userMapper, MealService mealService, RecommendedDailyIntakeRepository recommendedDailyIntakeRepository) {
        this.userRepository = userRepository;
        this.mealRepository = mealRepository;
        this.userMapper = userMapper;
        this.mealService = mealService;
        this.recommendedDailyIntakeRepository = recommendedDailyIntakeRepository;
    }

    /**
     * Creates a new user in the system based on the provided UserBasicInfoInputDTO.
     * Meals are not added at the time of creation.
     *
     * @param userBasicInfoInputDTO The input data for creating the user.
     * @return The created UserDTO.
     */
    public UserDTO createUser(UserBasicInfoInputDTO userBasicInfoInputDTO) {
        try {
            User savedUser = userRepository.save(userMapper.toEntity(userBasicInfoInputDTO));
            return userMapper.toDTO(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new EntityExistsException("User with email " + userBasicInfoInputDTO.getEmail() + " already exists.");
        }
    }

    /**
     * Updates the basic information of an existing user in the system based on the provided UserBasicInfoInputDTO.
     *
     * @param id The ID of the user to update.
     * @param userBasicInfoInputDTO The input data for updating the user.
     * @return The updated UserDTO.
     */
    public UserDTO updateUserBasicInfo(Long id, UserBasicInfoInputDTO userBasicInfoInputDTO) {
        // Retrieve the existing user
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID " + id));

        // Use values from DTO to update the User object only if they are not null/blank
        if (userBasicInfoInputDTO.getUserName() != null) {
            existingUser.setUserName(userBasicInfoInputDTO.getUserName());
        }
        if (userBasicInfoInputDTO.getEmail() != null) {
            existingUser.setEmail(userBasicInfoInputDTO.getEmail());
        }
        if (userBasicInfoInputDTO.getPassword() != null) {
            // TODO: Hash the password before setting it here
            existingUser.setPassword(userBasicInfoInputDTO.getPassword());
        }
        if (userBasicInfoInputDTO.getRole() != null) {
            existingUser.setRole(userBasicInfoInputDTO.getRole());
        }

        // Save and return updated user information
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDTO(updatedUser);
    }

    /**
     * Updates the detailed information of an existing user in the system based on the provided UserDetailsInputDTO.
     *
     * @param id The ID of the user to update.
     * @param userDetailsInputDTO The input data for updating the user's detailed information.
     * @return The updated UserDTO.
     */
    public UserDTO updateUserDetails(Long id, UserDetailsInputDTO userDetailsInputDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID " + id));

        userMapper.updateEntityWithDetails(existingUser, userDetailsInputDTO);

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toDTO(updatedUser);
    }


    /**
     * Retrieves all users in the system.
     *
     * @return A list of UserDTOs representing all users.
     */
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id The ID of the user to retrieve.
     * @return The UserDTO representing the user.
     */
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID " + id));
        return userMapper.toDTO(user);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id The ID of the user to delete.
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Adds an existing meal to the user's list of meals.
     *
     * @param userId The ID of the user.
     * @param mealId The ID of the meal to be added.
     * @return The updated UserDTO with the new meal included.
     */
    public UserDTO addMealToUser(Long userId, Long mealId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID " + userId));

        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Meal not found with ID " + mealId));

        user.getMeals().add(meal); // Add the meal to the user's meals
        User updatedUser = userRepository.save(user);

        return userMapper.toDTO(updatedUser);
    }

    /**
     * Removes a meal from the user's list of meals.
     *
     * @param userId The ID of the user.
     * @param mealId The ID of the meal to be removed.
     * @return The updated UserDTO without the removed meal.
     */
    public UserDTO removeMealFromUser(Long userId, Long mealId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID " + userId));

        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Meal not found with ID " + mealId));

        user.getMeals().remove(meal); // Remove the meal from the user's meals
        User updatedUser = userRepository.save(user);

        return userMapper.toDTO(updatedUser);
    }

    /**
     * Processes the consumption of a meal by a user, updating the user's intake of nutrients for the current day.
     * The method retrieves the nutrients of the meal, deducts them from the recommended daily intake for today,
     * and updates the remaining intake for each nutrient. The updated intake values are then saved
     * for the user in the RecommendedDailyIntake.
     *
     * @param userId The ID of the user consuming the meal.
     * @param mealId The ID of the meal being consumed.
     * @return A map containing the remaining daily intake for each nutrient after the meal consumption for today.
     */
    @Transactional
    public Map<String, Double> eatMeal(Long userId, Long mealId) {
        // Retrieve the user by their ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID " + userId));

        // Retrieve the meal by its ID
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new RuntimeException("Meal not found with ID " + mealId));

        // Retrieve the nutrient values of the meal
        Map<String, NutrientInfoDTO> mealNutrients = mealService.calculateNutrients(mealId);

        // Get the user's existing recommended daily intake for today
        LocalDate today = LocalDate.now();  // Ensure that only today's intake is modified
        Optional<RecommendedDailyIntake> intakeForToday = user.getRecommendedDailyIntakes().stream()
                .filter(intake -> intake.getCreatedAt().toLocalDate().equals(today))  // Filter only for today's intake
                .findFirst();

        if (intakeForToday.isEmpty()) {
            throw new RuntimeException("Recommended daily intake for today not found for user with ID " + userId);
        }

        RecommendedDailyIntake recommendedDailyIntake = intakeForToday.get();

        // Retrieve the recommended daily intake values and normalize the keys
        Map<String, Double> recommendedIntakes = recommendedDailyIntake.getNutrients().stream()
                .collect(Collectors.toMap(
                        nutrient -> normalizeNutrientName(nutrient.getName()),  // Normalize the nutrient names
                        nutrient -> nutrient.getValue() != null ? nutrient.getValue() : 0.0  // Handle null values
                ));

        // Map to store the remaining intake for each nutrient
        Map<String, Double> remainingIntakes = new HashMap<>(recommendedIntakes);

        // Subtract the nutrients from the meal from the recommended daily intake
        for (Map.Entry<String, NutrientInfoDTO> entry : mealNutrients.entrySet()) {
            String originalNutrientName = entry.getKey();  // Original nutrient name from meal
            String normalizedNutrientName = normalizeNutrientName(originalNutrientName);  // Normalized version of the meal nutrient name

            NutrientInfoDTO nutrientInfo = entry.getValue();

            // Check if the normalized nutrient exists in the recommended intake
            if (remainingIntakes.containsKey(normalizedNutrientName)) {
                // Check if nutrient value is not null before subtracting
                double nutrientValue = nutrientInfo.getValue() != null ? nutrientInfo.getValue() : 0.0;
                // Subtract the nutrient value from the daily intake
                double remainingIntake = remainingIntakes.get(normalizedNutrientName) - nutrientValue;
                remainingIntakes.put(normalizedNutrientName, remainingIntake);  // Update remaining intake

                // Update the recommended intake for this nutrient in the Nutrient entity
                recommendedDailyIntake.getNutrients().forEach(nutrient -> {
                    if (normalizeNutrientName(nutrient.getName()).equals(normalizedNutrientName)) {
                        nutrient.setValue(remainingIntake);  // Update the remaining intake for today
                    }
                });
            }
        }

        // Save the updated RecommendedDailyIntake and ensure the changes are flushed to the database
        try {
            recommendedDailyIntakeRepository.save(recommendedDailyIntake);
            entityManager.flush();  // Ensure the changes are flushed to the database immediately
        } catch (Exception e) {
            System.out.println("Error saving RecommendedDailyIntake: " + e.getMessage());
            e.printStackTrace();
        }

        // Return the remaining intake for each nutrient to the client
        return remainingIntakes;
    }


    /**
     * Normalizes nutrient names by converting them to lowercase, removing units like "g", "mg", and "µg"
     * only at the end of the string, and then removing all spaces.
     *
     * @param nutrientName The nutrient name to normalize.
     * @return The normalized nutrient name without units at the end and without spaces.
     */
    private String normalizeNutrientName(String nutrientName) {
        return nutrientName.toLowerCase()
                .replaceAll("\\s(g|mg|µg)$", "")  // Remove " g", " mg", " µg" at the end
                .replace(" ", "");  // Remove all remaining spaces
    }
}

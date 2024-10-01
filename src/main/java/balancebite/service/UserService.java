package balancebite.service;

import balancebite.dto.NutrientInfoDTO;
import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserInputDTO;
import balancebite.mapper.UserMapper;
import balancebite.model.Meal;
import balancebite.model.RecommendedDailyIntake;
import balancebite.model.User;
import balancebite.repository.MealRepository;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * Creates a new user in the system based on the provided UserInputDTO.
     * Meals are not added at the time of creation.
     *
     * @param userInputDTO The input data for creating the user.
     * @return The created UserDTO.
     */
    public UserDTO createUser(UserInputDTO userInputDTO) {
        User user = userMapper.toEntity(userInputDTO);
        user.setMeals(null);  // No meals added at this point
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    /**
     * Updates an existing user in the system based on the provided UserInputDTO.
     * This can include updating the meals associated with the user.
     *
     * @param id The ID of the user to update.
     * @param userInputDTO The input data for updating the user.
     * @return The updated UserDTO.
     */
    public UserDTO updateUser(Long id, UserInputDTO userInputDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID " + id));

        // Update the user's information
        existingUser.setName(userInputDTO.getName());
        existingUser.setEmail(userInputDTO.getEmail());
        existingUser.setPassword(userInputDTO.getPassword());  // Hash the password before saving in the service layer
        existingUser.setRole(userInputDTO.getRole());

        // Save the updated user
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
     * Processes the consumption of a meal by a user, updating the user's intake of nutrients.
     * The method retrieves the nutrients of the meal, deducts them from the recommended daily intake,
     * and updates the remaining intake for each nutrient. The updated intake values are then saved
     * for the user in the RecommendedDailyIntake.
     *
     * @param userId The ID of the user consuming the meal.
     * @param mealId The ID of the meal being consumed.
     * @return A map containing the remaining daily intake for each nutrient after the meal consumption.
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

        // Get the user's existing recommended daily intake
        RecommendedDailyIntake recommendedDailyIntake = user.getRecommendedDailyIntake();

        // Check if the user has a recommended daily intake set
        if (recommendedDailyIntake == null) {
            throw new RuntimeException("Recommended daily intake not found for user with ID " + userId);
        }

        // Retrieve the recommended daily intake values and normalize the keys
        Map<String, Double> recommendedIntakes = recommendedDailyIntake.getAllRecommendedIntakes().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> normalizeNutrientName(entry.getKey()),  // Normalize the nutrient names in the intakeMap
                        Map.Entry::getValue
                ));

        // Log de volledige intakeMap na normalisatie
        System.out.println("Intake map after normalization:");
        recommendedIntakes.forEach((nutrient, value) ->
                System.out.println("Nutrient: " + nutrient + ", Value: " + value)
        );

        // Map to store the remaining intake for each nutrient, initialized with full recommended intake
        Map<String, Double> remainingIntakes = new HashMap<>(recommendedIntakes);

        // Subtract the nutrients from the meal from the recommended daily intake
        for (Map.Entry<String, NutrientInfoDTO> entry : mealNutrients.entrySet()) {
            String originalNutrientName = entry.getKey();  // Original nutrient name from meal
            String normalizedNutrientName = normalizeNutrientName(originalNutrientName);  // Normalized version of the meal nutrient name

            // Log both original and normalized nutrient names
            System.out.println("Original nutrient name from meal: " + originalNutrientName);
            System.out.println("Normalized nutrient name from meal: " + normalizedNutrientName);

            NutrientInfoDTO nutrientInfo = entry.getValue();

            // Check if the normalized nutrient exists in the recommended intake
            if (remainingIntakes.containsKey(normalizedNutrientName)) {
                // Subtract the nutrient value from the daily intake
                double remainingIntake = remainingIntakes.get(normalizedNutrientName) - nutrientInfo.getValue();
                remainingIntakes.put(normalizedNutrientName, remainingIntake);  // Update remaining intake

                // Update the recommended intake for this nutrient in the intakeMap of the RecommendedDailyIntake
                recommendedDailyIntake.updateIntake(normalizedNutrientName, remainingIntake);

                System.out.println("Updated intake for nutrient: " + normalizedNutrientName + " to " + remainingIntake);
            } else {
                // Log nutrient not found
                System.out.println("Nutrient not found in intake map: " + normalizedNutrientName);
            }
        }

        // Save the updated RecommendedDailyIntake (including the intakeMap)
        recommendedDailyIntakeRepository.save(recommendedDailyIntake);  // This is the repository for RecommendedDailyIntake

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

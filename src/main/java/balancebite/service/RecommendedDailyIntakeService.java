package balancebite.service;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.errorHandling.*;
import balancebite.mapper.RecommendedDailyIntakeMapper;
import balancebite.model.RecommendedDailyIntake;
import balancebite.model.user.User;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import balancebite.service.interfaces.IRecommendedDailyIntakeService;
import balancebite.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class responsible for managing Recommended Daily Intake logic.
 * Provides methods to retrieve, create, and manage daily, weekly, and monthly nutrient intakes for users.
 */
@Service
public class RecommendedDailyIntakeService implements IRecommendedDailyIntakeService {
    private static final Logger log = LoggerFactory.getLogger(RecommendedDailyIntakeService.class);

    private final RecommendedDailyIntakeMapper recommendedDailyIntakeMapper;
    private final RecommendedDailyIntakeRepository recommendedDailyIntakeRepository;
    private final UserRepository userRepository;

    /**
     * Constructs the service with the required dependencies.
     *
     * @param recommendedDailyIntakeMapper       Mapper for converting entities to DTOs.
     * @param recommendedDailyIntakeRepository   Repository for managing RecommendedDailyIntake entities.
     * @param userRepository                     Repository for managing User entities.
     */
    public RecommendedDailyIntakeService(RecommendedDailyIntakeMapper recommendedDailyIntakeMapper,
                                         RecommendedDailyIntakeRepository recommendedDailyIntakeRepository,
                                         UserRepository userRepository) {
        this.recommendedDailyIntakeMapper = recommendedDailyIntakeMapper;
        this.recommendedDailyIntakeRepository = recommendedDailyIntakeRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves the recommended daily intake for a specific user for the current date.
     *
     * @param userId The ID of the user for whom the recommended daily intake is retrieved.
     * @return A DTO containing the recommended daily intake values.
     */
    @Override
    public RecommendedDailyIntakeDTO getDailyIntakeForUser(Long userId) {
        log.info("Attempting to retrieve recommended daily intake for user with ID: {}", userId);

        // Fetch the user to ensure they exist.
        User user = findUserById(userId);
        log.info("Fetched user: ID={}, Gender={}, ActivityLevel={}, Goal={}",
                user.getId(), user.getGender(), user.getActivityLevel(), user.getGoal());

        // Check if an intake exists for today.
        Optional<RecommendedDailyIntake> existingIntake = recommendedDailyIntakeRepository.findByUser_IdAndCreatedAt(user.getId(), LocalDate.now());
        if (existingIntake.isPresent()) {
            log.info("Found existing RecommendedDailyIntake for user ID {} on date {}", userId, LocalDate.now());
            log.info("Existing intake details: ID={}, UserId={}, CreatedAt={}",
                    existingIntake.get().getId(), existingIntake.get().getUser().getId(), existingIntake.get().getCreatedAt());
            return recommendedDailyIntakeMapper.toDTO(existingIntake.get());
        } else {
            log.warn("No RecommendedDailyIntake found for user ID {} on date {}", userId, LocalDate.now());
            throw new DailyIntakeNotFoundException("No daily intake found for user ID: " + userId);
        }
    }

    /**
     * Retrieves the base recommended daily intake for a specific user.
     *
     * @param userId The ID of the user for whom the base recommended daily intake is retrieved.
     * @return A DTO containing the base recommended daily intake values.
     * @throws DailyIntakeNotFoundException If no base recommended daily intake is found for the user.
     */
    @Override
    public RecommendedDailyIntakeDTO getBaseDailyIntakeForUser(Long userId) {
        log.info("Attempting to retrieve base recommended daily intake for user with ID: {}", userId);

        // Fetch the user to ensure they exist.
        User user = findUserById(userId);
        log.info("Fetched user: ID={}, Gender={}, ActivityLevel={}, Goal={}",
                user.getId(), user.getGender(), user.getActivityLevel(), user.getGoal());

        // Retrieve the Base RDI
        RecommendedDailyIntake baseRDI = user.getBaseRecommendedDailyIntake();
        if (baseRDI != null) {
            log.info("Found BaseRecommendedDailyIntake for user ID {} with ID {}", userId, baseRDI.getId());
            return recommendedDailyIntakeMapper.toDTO(baseRDI);
        } else {
            log.warn("No BaseRecommendedDailyIntake found for user ID {}", userId);
            throw new DailyIntakeNotFoundException("No base daily intake found for user ID: " + userId);
        }
    }

    /**
     * Retrieves the cumulative weekly nutrient intake for a specific user.
     *
     * @param userId The ID of the user whose weekly intake is being retrieved.
     * @return A map containing the total intake values for the week.
     */
    @Override
    public Map<String, Double> getAdjustedWeeklyIntakeForUser(Long userId) {
        log.info("Calculating adjusted weekly intake for user with ID: {}", userId);

        // Fetch the user and validate their information.
        User user = findUserById(userId);
        UserValidationUtil.validateUserInformation(user);

        // Calculate the weekly intake.
        Map<String, Double> weeklyIntake = WeeklyIntakeCalculatorUtil.calculateAdjustedWeeklyIntake(user);
        log.info("Successfully calculated weekly intake for user ID: {}", userId);
        return weeklyIntake;
    }

    /**
     * Retrieves the cumulative monthly nutrient intake for a specific user.
     *
     * @param userId The ID of the user whose monthly intake is being retrieved.
     * @return A map containing the total intake values for the month.
     */
    @Override
    public Map<String, Double> getAdjustedMonthlyIntakeForUser(Long userId) {
        log.info("Calculating adjusted monthly intake for user with ID: {}", userId);

        // Fetch the user and validate their information.
        User user = findUserById(userId);
        UserValidationUtil.validateUserInformation(user);

        // Calculate the monthly intake.
        Map<String, Double> monthlyIntake = MonthlyIntakeCalculatorUtil.calculateAdjustedMonthlyIntake(user);
        log.info("Successfully calculated monthly intake for user ID: {}", userId);
        return monthlyIntake;
    }

    /**
     * Retrieves the recommended daily intake for a specific user on a specific date.
     *
     * @param userId The ID of the user for whom the intake is being retrieved.
     * @param date   The date for which the intake is being retrieved.
     * @return A DTO containing the recommended daily intake values for the specified date.
     */
    @Override
    public RecommendedDailyIntakeDTO getDailyIntakeForDate(Long userId, LocalDate date) {
        log.info("Fetching recommended daily intake for user ID {} on date {}", userId, date);

        // Fetch the user.
        User user = findUserById(userId);

        // Retrieve the intake for the specified date.
        Optional<RecommendedDailyIntake> intake = recommendedDailyIntakeRepository.findByUser_IdAndCreatedAt(userId, date);
        if (intake.isEmpty()) {
            log.warn("No RecommendedDailyIntake found for user ID {} on date {}", userId, date);
            throw new DailyIntakeNotFoundException("No recommended daily intake found for user ID " + userId + " on date " + date);
        }

        log.info("Successfully fetched RecommendedDailyIntake for user ID {} on date {}", userId, date);
        return recommendedDailyIntakeMapper.toDTO(intake.get());
    }

    /**
     * Deletes all recommended daily intake records associated with a specific user.
     *
     * @param userId The ID of the user whose intake records will be deleted.
     */
    @Override
    public void deleteRecommendedDailyIntakeForUser(Long userId) {
        log.info("Attempting to delete recommended daily intake for user with ID: {}", userId);

        // Fetch the user and check for existing intakes.
        User user = findUserById(userId);
        if (user.getRecommendedDailyIntakes() == null) {
            log.error("No RecommendedDailyIntake found for user with ID {}", userId);
            throw new DailyIntakeNotFoundException("No RecommendedDailyIntake found for user with ID " + userId);
        }

        // Remove the intakes and save the user entity.
        user.setRecommendedDailyIntakes(null);
        userRepository.save(user);
        log.info("Successfully deleted recommended daily intake for user ID: {}", userId);
    }

    /**
     * Finds a user by their ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The User entity if found.
     * @throws UserNotFoundException If no user is found with the specified ID.
     */
    private User findUserById(Long userId) {
        log.info("Fetching user with ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", userId);
                    throw new UserNotFoundException("User with ID " + userId + " not found");
                });
    }

    /**
     * Scheduled method to create daily Recommended Daily Intake (RDI) entries for users.
     *
     * This method runs every day at 00:00 and performs the following:
     * 1. Retrieves all users from the database.
     * 2. Skips users who have not provided all required details (weight, height, goal, activity level).
     * 3. Checks if an RDI already exists for the current day for each user.
     * 4. Creates a new RDI entry for users who do not have one for the current day.
     *
     * The new RDI is calculated using the user's details and saved to the database.
     */
    @Scheduled(cron = "0 1 0 * * ?") // Executes daily at 00:00
    public void createDailyIntakesForUsers() {
        log.info("Start creating daily intakes for users.");

        // Retrieve all users from the database
        List<User> users = userRepository.findAll();

        for (User user : users) {
            // Skip users who have not provided all required details
            if (user.getWeight() == null || user.getHeight() == null || user.getGoal() == null || user.getActivityLevel() == null) {
                log.warn("Skipping user ID {}: required details are missing.", user.getId());
                continue;
            }

            // Check if a Recommended Daily Intake already exists for today
            LocalDate today = LocalDate.now();
            boolean hasTodayIntake = recommendedDailyIntakeRepository.existsByUser_IdAndCreatedAt(user.getId(), today);

            if (!hasTodayIntake) {
                // Create a new Recommended Daily Intake for the user
                log.info("Creating new RecommendedDailyIntake for user ID {} on date {}", user.getId(), today);
                RecommendedDailyIntake newIntake = DailyIntakeCalculatorUtil.calculateDailyIntake(user); // Calculate RDI
                newIntake.setCreatedAt(today);
                newIntake.setUser(user);
                recommendedDailyIntakeRepository.save(newIntake);
            } else {
                log.info("User ID {} already has an intake for today: {}", user.getId());
            }
        }

        log.info("Completed creating daily intakes for users.");
    }
}

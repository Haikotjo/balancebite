package balancebite.service;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.errorHandling.*;
import balancebite.mapper.RecommendedDailyIntakeMapper;
import balancebite.model.RecommendedDailyIntake;
import balancebite.model.User;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import balancebite.service.interfaces.IRecommendedDailyIntakeService;
import balancebite.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

/**
 * Service class responsible for managing Recommended Daily Intake logic.
 */
@Service
public class RecommendedDailyIntakeService implements IRecommendedDailyIntakeService {
    private static final Logger log = LoggerFactory.getLogger(RecommendedDailyIntakeService.class);

    private final RecommendedDailyIntakeMapper recommendedDailyIntakeMapper;
    private final RecommendedDailyIntakeRepository recommendedDailyIntakeRepository;
    private final UserRepository userRepository;

    public RecommendedDailyIntakeService(RecommendedDailyIntakeMapper recommendedDailyIntakeMapper,
                                         RecommendedDailyIntakeRepository recommendedDailyIntakeRepository,
                                         UserRepository userRepository) {
        this.recommendedDailyIntakeMapper = recommendedDailyIntakeMapper;
        this.recommendedDailyIntakeRepository = recommendedDailyIntakeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RecommendedDailyIntakeDTO getOrCreateDailyIntakeForUser(Long userId) {
        log.info("Attempting to retrieve or create recommended daily intake for user with ID: {}", userId);

        User user = findUserById(userId);

        // Validate user information using utility class
        UserValidationUtil.validateUserInformation(user);

        Optional<RecommendedDailyIntake> existingIntake = recommendedDailyIntakeRepository.findByUser_IdAndCreatedAt(user.getId(), LocalDate.now());
        if (existingIntake.isPresent()) {
            log.info("Found existing RecommendedDailyIntake for user ID {} on date {}", userId, LocalDate.now());
            return recommendedDailyIntakeMapper.toDTO(existingIntake.get());
        }

        log.info("No existing RecommendedDailyIntake found for user ID {} on date {}. Calculating a new intake.", userId, LocalDate.now());
        RecommendedDailyIntake recommendedDailyIntake = DailyIntakeCalculatorUtil.getOrCreateDailyIntakeForUser(user);
        recommendedDailyIntake.setUser(user);
        recommendedDailyIntake.setCreatedAt(LocalDate.now());

        recommendedDailyIntakeRepository.save(recommendedDailyIntake);
        log.info("Successfully created and saved RecommendedDailyIntake for user ID {} on date {}", userId, LocalDate.now());

        return recommendedDailyIntakeMapper.toDTO(recommendedDailyIntake);
    }

    @Override
    public Map<String, Double> getAdjustedWeeklyIntakeForUser(Long userId) {
        log.info("Calculating adjusted weekly intake for user with ID: {}", userId);

        User user = findUserById(userId);

        // Validate user information
        UserValidationUtil.validateUserInformation(user);

        Map<String, Double> weeklyIntake = WeeklyIntakeCalculatorUtil.calculateAdjustedWeeklyIntake(user);

        log.info("Successfully calculated weekly intake for user ID: {}", userId);
        return weeklyIntake;
    }

    @Override
    public Map<String, Double> getAdjustedMonthlyIntakeForUser(Long userId) {
        log.info("Calculating adjusted monthly intake for user with ID: {}", userId);

        User user = findUserById(userId);

        // Validate user information
        UserValidationUtil.validateUserInformation(user);

        Map<String, Double> monthlyIntake = MonthlyIntakeCalculatorUtil.calculateAdjustedMonthlyIntake(user);

        log.info("Successfully calculated monthly intake for user ID: {}", userId);
        return monthlyIntake;
    }

    @Override
    public RecommendedDailyIntakeDTO getDailyIntakeForDate(Long userId, LocalDate date) {
        log.info("Fetching recommended daily intake for user ID {} on date {}", userId, date);

        User user = findUserById(userId);

        Optional<RecommendedDailyIntake> intake = recommendedDailyIntakeRepository.findByUser_IdAndCreatedAt(userId, date);
        if (intake.isEmpty()) {
            log.warn("No RecommendedDailyIntake found for user ID {} on date {}", userId, date);
            throw new DailyIntakeNotFoundException("No recommended daily intake found for user ID " + userId + " on date " + date);
        }

        log.info("Successfully fetched RecommendedDailyIntake for user ID {} on date {}", userId, date);
        return recommendedDailyIntakeMapper.toDTO(intake.get());
    }


    @Override
    public void deleteRecommendedDailyIntakeForUser(Long userId) {
        log.info("Attempting to delete recommended daily intake for user with ID: {}", userId);

        User user = findUserById(userId);

        if (user.getRecommendedDailyIntakes() == null) {
            log.error("No RecommendedDailyIntake found for user with ID {}", userId);
            throw new DailyIntakeNotFoundException("No RecommendedDailyIntake found for user with ID " + userId);
        }

        user.setRecommendedDailyIntakes(null);
        userRepository.save(user);

        log.info("Successfully deleted recommended daily intake for user ID: {}", userId);
    }

    private User findUserById(Long userId) {
        log.info("Fetching user with ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", userId);
                    throw new UserNotFoundException("User with ID " + userId + " not found");
                });
    }
}

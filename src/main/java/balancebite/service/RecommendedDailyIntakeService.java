package balancebite.service;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.mapper.RecommendedDailyIntakeMapper;
import balancebite.model.RecommendedDailyIntake;
import balancebite.model.User;
import balancebite.repository.RecommendedDailyIntakeRepository;
import balancebite.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class responsible for managing Recommended Daily Intake logic.
 * Handles the logic of assigning and retrieving recommended daily intake for users.
 */
@Service
public class RecommendedDailyIntakeService {

    private final RecommendedDailyIntakeMapper intakeMapper;
    private final UserRepository userRepository;
    private final RecommendedDailyIntakeRepository intakeRepository;

    public RecommendedDailyIntakeService(RecommendedDailyIntakeMapper intakeMapper,
                                         UserRepository userRepository,
                                         RecommendedDailyIntakeRepository intakeRepository) {
        this.intakeMapper = intakeMapper;
        this.userRepository = userRepository;
        this.intakeRepository = intakeRepository;
    }

    /**
     * Retrieves the RecommendedDailyIntake for a specific user by their ID.
     * If the user doesn't have a recommended daily intake yet, a new one will be created.
     *
     * @param userId The ID of the user whose intake is being retrieved.
     * @return The RecommendedDailyIntakeDTO containing the recommended daily intake for the user.
     */
    public RecommendedDailyIntakeDTO getRecommendedDailyIntakeByUserId(Long userId) {
        // Zoek de user op basis van het ID
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }

        User user = userOptional.get();

        // Controleer of de user al een RecommendedDailyIntake heeft
        RecommendedDailyIntake dailyIntake = user.getRecommendedDailyIntake();

        // Als de user geen intake heeft, maak een nieuwe aan en koppel deze aan de user
        if (dailyIntake == null) {
            dailyIntake = new RecommendedDailyIntake();
            user.setRecommendedDailyIntake(dailyIntake);
            userRepository.save(user); // Sla de user met de nieuwe intake op
        }

        // Gebruik de mapper om het om te zetten naar DTO
        return intakeMapper.toDTO(dailyIntake);
    }
}

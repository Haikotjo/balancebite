package balancebite.service;

import balancebite.dto.RecommendedDailyIntakeDTO;
import balancebite.dto.RecommendedDailyIntakeInputDTO;
import balancebite.mapper.RecommendedDailyIntakeMapper;
import balancebite.model.RecommendedDailyIntake;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing Recommended Daily Intake logic.
 */
@Service
public class RecommendedDailyIntakeService {

    private final RecommendedDailyIntakeMapper intakeMapper;

    public RecommendedDailyIntakeService(RecommendedDailyIntakeMapper intakeMapper) {
        this.intakeMapper = intakeMapper;
    }

    /**
     * Gets the recommended daily intake data.
     *
     * @return The RecommendedDailyIntakeDTO.
     */
    public RecommendedDailyIntakeDTO getRecommendedDailyIntake() {
        // Maak een nieuw RecommendedDailyIntake object
        RecommendedDailyIntake dailyIntake = new RecommendedDailyIntake();

        // Gebruik de mapper om het om te zetten naar DTO
        return intakeMapper.toDTO(dailyIntake);
    }

    /**
     * (Future implementation) Adjusts the recommended daily intake based on user input.
     *
     * @param inputDTO Input data that affects the recommended intake.
     * @return The adjusted RecommendedDailyIntakeDTO.
     */
    public RecommendedDailyIntakeDTO adjustRecommendedDailyIntake(RecommendedDailyIntakeInputDTO inputDTO) {
        // Logic to adjust intake based on inputDTO (e.g., weight, gender)
        // For now, returns the default values
        return getRecommendedDailyIntake();
    }
}

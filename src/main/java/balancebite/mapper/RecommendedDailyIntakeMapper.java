package balancebite.mapper;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.model.RecommendedDailyIntake;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between RecommendedDailyIntake and RecommendedDailyIntakeDTO.
 * Handles the transformation of RecommendedDailyIntake data into its corresponding DTO representation.
 */
@Component
public class RecommendedDailyIntakeMapper {

    /**
     * Converts a RecommendedDailyIntake entity to a RecommendedDailyIntakeDTO.
     *
     * @param recommendedDailyIntake The entity to convert.
     * @return The corresponding RecommendedDailyIntakeDTO.
     */
    public RecommendedDailyIntakeDTO toDTO(RecommendedDailyIntake recommendedDailyIntake) {
        if (recommendedDailyIntake == null) {
            return null;
        }
        return new RecommendedDailyIntakeDTO(recommendedDailyIntake.getAllRecommendedIntakes());
    }

    /**
     * Converts a RecommendedDailyIntakeDTO back to a RecommendedDailyIntake entity.
     * This can be useful when applying changes or updates from the client.
     *
     * @param recommendedDailyIntakeDTO The DTO to convert.
     * @return A new RecommendedDailyIntake entity.
     */
    public RecommendedDailyIntake toEntity(RecommendedDailyIntakeDTO recommendedDailyIntakeDTO) {
        if (recommendedDailyIntakeDTO == null) {
            return null;
        }
        RecommendedDailyIntake entity = new RecommendedDailyIntake();
        entity.getAllRecommendedIntakes().putAll(recommendedDailyIntakeDTO.getIntakeMap());
        return entity;
    }
}

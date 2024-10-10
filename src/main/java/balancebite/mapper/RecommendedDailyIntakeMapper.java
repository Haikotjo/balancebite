package balancebite.mapper;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.model.Nutrient;
import balancebite.model.RecommendedDailyIntake;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

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

        // Return the DTO with the set of Nutrients and createdAt timestamp
        return new RecommendedDailyIntakeDTO(recommendedDailyIntake.getNutrients(), recommendedDailyIntake.getCreatedAt());
    }

    /**
     * Converts a RecommendedDailyIntakeDTO back to a RecommendedDailyIntake entity.
     *
     * @param recommendedDailyIntakeDTO The DTO to convert.
     * @return A new RecommendedDailyIntake entity.
     */
    public RecommendedDailyIntake toEntity(RecommendedDailyIntakeDTO recommendedDailyIntakeDTO) {
        if (recommendedDailyIntakeDTO == null) {
            return null;
        }

        // Create a new RecommendedDailyIntake entity and add the nutrients
        RecommendedDailyIntake entity = new RecommendedDailyIntake();
        Set<Nutrient> nutrients = recommendedDailyIntakeDTO.getNutrients();
        LocalDateTime createdAt = recommendedDailyIntakeDTO.getCreatedAt();

        entity.getNutrients().addAll(nutrients);
        entity.setCreatedAt(createdAt != null ? createdAt : LocalDateTime.now());
        return entity;
    }
}

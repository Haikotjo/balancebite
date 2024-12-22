package balancebite.mapper;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.model.RecommendedDailyIntake;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between RecommendedDailyIntake entity and RecommendedDailyIntakeDTO.
 * This class is responsible for transforming data from the database entity representation
 * into a Data Transfer Object (DTO) that can be safely exposed to the client.
 */
@Component
public class RecommendedDailyIntakeMapper {

    /**
     * Converts a RecommendedDailyIntake entity into a RecommendedDailyIntakeDTO.
     * This method is used to transform internal entity data into a format suitable
     * for returning to the client. The DTO includes a set of nutrients and a formatted
     * creation date (createdAtFormatted).
     *
     * @param recommendedDailyIntake The RecommendedDailyIntake entity to convert.
     *                               If null, the method returns null.
     * @return A RecommendedDailyIntakeDTO containing the nutrients and the formatted creation date,
     *         or null if the provided entity is null.
     */
    public RecommendedDailyIntakeDTO toDTO(RecommendedDailyIntake recommendedDailyIntake) {
        if (recommendedDailyIntake == null) {
            return null; // Return null if the input entity is null
        }

        // Create and return a DTO with the set of nutrients and the createdAt timestamp
        return new RecommendedDailyIntakeDTO(
                recommendedDailyIntake.getNutrients(), // Nutrients to be included in the DTO
                recommendedDailyIntake.getCreatedAt()  // Creation date to be formatted in the DTO
        );
    }

}

package balancebite.dto;

import java.util.Map;

/**
 * Data Transfer Object for Recommended Daily Intake.
 * This DTO is used to transfer the recommended daily intake data to the client.
 */
public class RecommendedDailyIntakeDTO {

    private Map<String, Double> intakeMap;

    /**
     * Default constructor for serialization/deserialization purposes.
     */
    public RecommendedDailyIntakeDTO() {
    }

    /**
     * Constructor to create a RecommendedDailyIntakeDTO.
     *
     * @param intakeMap The map containing nutrient names as keys and recommended intake values as values.
     */
    public RecommendedDailyIntakeDTO(Map<String, Double> intakeMap) {
        this.intakeMap = intakeMap;
    }

    /**
     * Gets the map of nutrient names and their recommended daily intake values.
     *
     * @return The intake map.
     */
    public Map<String, Double> getIntakeMap() {
        return intakeMap;
    }

    /**
     * Sets the map of nutrient names and their recommended daily intake values.
     *
     * @param intakeMap The intake map to set.
     */
    public void setIntakeMap(Map<String, Double> intakeMap) {
        this.intakeMap = intakeMap;
    }
}

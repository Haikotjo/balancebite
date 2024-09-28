package balancebite.dto.recommendeddailyintake;

import java.util.Map;

/**
 * Data Transfer Object (DTO) for Recommended Daily Intake.
 * This DTO is used to transfer the recommended daily intake data to and from the client.
 * It includes a map of nutrients and their respective recommended intake values.
 */
public class RecommendedDailyIntakeDTO {

    /**
     * A map of nutrient names and their recommended daily intake values.
     * The nutrient name serves as the key, and the recommended intake value is the value.
     */
    private Map<String, Double> intakeMap;

    /**
     * Default no-argument constructor for serialization/deserialization purposes.
     * This constructor is used by frameworks like Jackson to map incoming JSON data.
     */
    public RecommendedDailyIntakeDTO() {
        // Default constructor for serialization frameworks
    }

    /**
     * Full constructor to create a RecommendedDailyIntakeDTO with a specified nutrient intake map.
     *
     * @param intakeMap The map containing nutrient names as keys and their recommended daily intake values as values.
     */
    public RecommendedDailyIntakeDTO(Map<String, Double> intakeMap) {
        this.intakeMap = intakeMap;
    }

    /**
     * Retrieves the map of nutrient names and their corresponding recommended daily intake values.
     *
     * @return The intake map containing nutrient names as keys and their daily recommended values as values.
     */
    public Map<String, Double> getIntakeMap() {
        return intakeMap;
    }

    /**
     * Sets the map of nutrient names and their corresponding recommended daily intake values.
     *
     * @param intakeMap The intake map to set, with nutrient names as keys and their recommended daily values as values.
     */
    public void setIntakeMap(Map<String, Double> intakeMap) {
        this.intakeMap = intakeMap;
    }
}

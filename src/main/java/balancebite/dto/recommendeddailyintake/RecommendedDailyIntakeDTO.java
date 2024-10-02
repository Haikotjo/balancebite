package balancebite.dto.recommendeddailyintake;

import balancebite.model.Nutrient;

import java.util.Set;

/**
 * Data Transfer Object (DTO) for Recommended Daily Intake.
 * This DTO is used to transfer the recommended daily intake data to and from the client.
 * It includes a set of nutrients and their respective recommended intake values.
 */
public class RecommendedDailyIntakeDTO {

    /**
     * A set of nutrient DTOs representing the nutrients and their recommended daily intake values.
     */
    private Set<Nutrient> nutrients;

    /**
     * Default no-argument constructor for serialization/deserialization purposes.
     * This constructor is used by frameworks like Jackson to map incoming JSON data.
     */
    public RecommendedDailyIntakeDTO() {
        // Default constructor for serialization frameworks
    }

    /**
     * Full constructor to create a RecommendedDailyIntakeDTO with a specified set of nutrients.
     *
     * @param nutrients The set of NutrientDTO objects representing nutrient names and their recommended daily intake values.
     */
    public RecommendedDailyIntakeDTO(Set<Nutrient> nutrients) {
        this.nutrients = nutrients;
    }

    /**
     * Retrieves the set of nutrients and their corresponding recommended daily intake values.
     *
     * @return The set of nutrients.
     */
    public Set<Nutrient> getNutrients() {
        return nutrients;
    }

    /**
     * Sets the set of nutrients and their corresponding recommended daily intake values.
     *
     * @param nutrients The set of nutrients to set, each represented as a NutrientDTO object.
     */
    public void setNutrients(Set<Nutrient> nutrients) {
        this.nutrients = nutrients;
    }
}

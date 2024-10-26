package balancebite.dto.recommendeddailyintake;

import balancebite.model.Nutrient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
     * The timestamp when the recommended daily intake was created.
     */
    private LocalDate createdAt;

    /**
     * The formatted timestamp when the recommended daily intake was created.
     */
    private String createdAtFormatted;

    /**
     * Default no-argument constructor for serialization/deserialization purposes.
     * This constructor is used by frameworks like Jackson to map incoming JSON data.
     */
    public RecommendedDailyIntakeDTO() {
        // Default constructor for serialization frameworks
    }

    /**
     * Full constructor to create a RecommendedDailyIntakeDTO with a specified set of nutrients and creation timestamp.
     *
     * @param nutrients The set of NutrientDTO objects representing nutrient names and their recommended daily intake values.
     * @param createdAt The timestamp when the recommended daily intake was created.
     */
    public RecommendedDailyIntakeDTO(Set<Nutrient> nutrients, LocalDate createdAt) {
        this.nutrients = nutrients;
        this.createdAt = createdAt;
        this.createdAtFormatted = createdAt != null ? createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
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

    /**
     * Retrieves the timestamp when the recommended daily intake was created.
     *
     * @return The creation timestamp.
     */
    public LocalDate getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the recommended daily intake was created.
     *
     * @param createdAt The timestamp to set.
     */
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
        this.createdAtFormatted = createdAt != null ? createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
    }

    /**
     * Retrieves the formatted timestamp when the recommended daily intake was created.
     *
     * @return The formatted creation timestamp.
     */
    public String getCreatedAtFormatted() {
        return createdAtFormatted;
    }
}


package balancebite.dto.recommendeddailyintake;

import balancebite.model.Nutrient;
import balancebite.model.meal.consumedMeal.ConsumedMeal;

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
    private final Set<Nutrient> nutrients;

    /**
     * The timestamp when the recommended daily intake was created.
     */
    private final LocalDate createdAt;

    private final Set<ConsumedMeal> consumedMeals;

    /**
     * Full constructor to create a RecommendedDailyIntakeDTO with a specified set of nutrients and creation timestamp.
     *
     * @param nutrients The set of NutrientDTO objects representing nutrient names and their recommended daily intake values.
     * @param createdAt The timestamp when the recommended daily intake was created.
     */
    public RecommendedDailyIntakeDTO(Set<Nutrient> nutrients, LocalDate createdAt, Set<ConsumedMeal> consumedMeals) {
        this.nutrients = Set.copyOf(nutrients);
        this.createdAt = createdAt;
        this.consumedMeals = consumedMeals == null ? Set.of() : Set.copyOf(consumedMeals);
    }

    /**
     * Retrieves the set of nutrients and their corresponding recommended daily intake values.
     *
     * @return An immutable set of nutrients.
     */
    public Set<Nutrient> getNutrients() {
        return nutrients;
    }

    /**
     * Retrieves the formatted timestamp when the recommended daily intake was created.
     *
     * @return The formatted creation timestamp.
     */
    public String getCreatedAtFormatted() {
        return createdAt != null ? createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
    }

    public Set<ConsumedMeal> getConsumedMeals() {
        return consumedMeals;
    }
}

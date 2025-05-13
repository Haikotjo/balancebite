package balancebite.dto.diet;

import balancebite.dto.NutrientInfoDTO;
import balancebite.dto.meal.MealDTO;
import balancebite.model.meal.references.Diet;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Data Transfer Object (DTO) for transferring DietDay data.
 * Represents a single day in a diet plan with its associated meals and nutrient summary.
 */
public class DietDayDTO {

    private final Long id;
    private final String dayLabel;
    private final LocalDate date;
    private final List<MealDTO> meals;
    private final Map<String, NutrientInfoDTO> totalNutrients;
    private final String dietDayDescription;
    private final Set<Diet> diets;
    /**
     * Constructor for DietDayDTO.
     *
     * @param id              The unique identifier of the diet day.
     * @param dayLabel       The name of the day (e.g., "Monday").
     * @param date            The specific date (optional, may be null).
     * @param meals           List of meals associated with this diet day.
     * @param totalNutrients  Map of total nutrients calculated for all meals in this day.
     */
    public DietDayDTO(Long id, String dayLabel, LocalDate date, List<MealDTO> meals, Map<String, NutrientInfoDTO> totalNutrients, String dietDayDescription, Set<Diet> diets) {
        this.id = id;
        this.dayLabel = dayLabel;
        this.date = date;
        this.meals = meals != null ? List.copyOf(meals) : List.of();
        this.totalNutrients = totalNutrients != null ? Map.copyOf(totalNutrients) : Map.of();
        this.dietDayDescription = dietDayDescription;
        this.diets = diets != null ? Set.copyOf(diets) : Set.of();
    }

    public Long getId() {
        return id;
    }

    public String getDayLabel() {
        return dayLabel;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<MealDTO> getMeals() {
        return List.copyOf(meals);
    }

    public Map<String, NutrientInfoDTO> getTotalNutrients() {
        return totalNutrients;
    }

    public String getDietDayDescription() {
        return dietDayDescription;
    }

    public Set<Diet> getDiets() {
        return diets;
    }
}

package balancebite.dto.diet;

import balancebite.dto.meal.MealDTO;
import balancebite.model.meal.references.Diet;

import java.time.LocalDate;
import java.util.List;
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
    private final String dietDayDescription;
    private final Set<Diet> diets;

    private final Double totalCalories;
    private final Double totalProtein;
    private final Double totalCarbs;
    private final Double totalFat;
    private final Double totalSaturatedFat;
    private final Double totalUnsaturatedFat;
    private final Double totalSugars;

    public DietDayDTO(
            Long id,
            String dayLabel,
            LocalDate date,
            List<MealDTO> meals,
            String dietDayDescription,
            Set<Diet> diets,
            Double totalCalories,
            Double totalProtein,
            Double totalCarbs,
            Double totalFat,
            Double totalSaturatedFat,
            Double totalUnsaturatedFat,
            Double totalSugars
    ) {
        this.id = id;
        this.dayLabel = dayLabel;
        this.date = date;
        this.meals = meals != null ? List.copyOf(meals) : List.of();
        this.dietDayDescription = dietDayDescription;
        this.diets = diets != null ? Set.copyOf(diets) : Set.of();
        this.totalCalories = totalCalories;
        this.totalProtein = totalProtein;
        this.totalCarbs = totalCarbs;
        this.totalFat = totalFat;
        this.totalSaturatedFat = totalSaturatedFat;
        this.totalUnsaturatedFat = totalUnsaturatedFat;
        this.totalSugars = totalSugars;
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
        return meals;
    }

    public String getDietDayDescription() {
        return dietDayDescription;
    }

    public Set<Diet> getDiets() {
        return diets;
    }

    public Double getTotalCalories() {
        return totalCalories;
    }

    public Double getTotalProtein() {
        return totalProtein;
    }

    public Double getTotalCarbs() {
        return totalCarbs;
    }

    public Double getTotalFat() {
        return totalFat;
    }

    public Double getTotalSaturatedFat() {
        return totalSaturatedFat;
    }

    public Double getTotalUnsaturatedFat() {
        return totalUnsaturatedFat;
    }

    public Double getTotalSugars() {
        return totalSugars;
    }
}

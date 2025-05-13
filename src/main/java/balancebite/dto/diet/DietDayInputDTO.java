package balancebite.dto.diet;

import balancebite.model.meal.references.Diet;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class DietDayInputDTO {

    @Size(max = 50, message = "Day label must not exceed 50 characters.")
    private String dayLabel;

    private LocalDate date;

    @Size(max = 1000, message = "Day description must not exceed 1000 characters.")
    private String dietDayDescription;


    private Set<Diet> diets;

    @Size(min = 2, max = 15, message = "Each day must have between 2 and 15 meals.")
    @NotNull(message = "Meal IDs must be provided.")
    private List<Long> mealIds;

    public DietDayInputDTO() {}

    public DietDayInputDTO(String dayLabel, LocalDate date, List<Long> mealIds, String dietDayDescription, Set<Diet> diets) {
        this.dayLabel = dayLabel;
        this.date = date;
        this.mealIds = mealIds;
        this.dietDayDescription = dietDayDescription;
        this.diets = diets;
    }

    public String getDayLabel() {
        return dayLabel;
    }

    public void setDayLabel(String dayLabel) {
        this.dayLabel = dayLabel;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<Long> getMealIds() {
        return mealIds;
    }

    public void setMealIds(List<Long> mealIds) {
        this.mealIds = mealIds;
    }

    public String getDietDayDescription() {
        return dietDayDescription;
    }

    public void setDietDayDescription (String dietDayDescription) {
        this.dietDayDescription = dietDayDescription;
    }

    public Set<Diet> getDiets() {
        return diets;
    }

    public void setDiets(Set<Diet> diets) {
        this.diets = diets;
    }
}

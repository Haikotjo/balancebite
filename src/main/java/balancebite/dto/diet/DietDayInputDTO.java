package balancebite.dto.diet;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class DietDayInputDTO {

    private LocalDate date;

    @Size(max = 15, message = "A day cannot contain more than 15 meals.")
    @NotNull(message = "Meal IDs must be provided.")
    private List<Long> mealIds;

    public DietDayInputDTO() {}

    public DietDayInputDTO(LocalDate date, List<Long> mealIds) {
        this.date = date;
        this.mealIds = mealIds;
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
}

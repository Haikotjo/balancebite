package balancebite.dto.meal;

import balancebite.model.meal.Meal;

public class SavedMealDTO {

    private final Meal meal;
    private final long saves;

    public SavedMealDTO(Meal meal, long saves) {
        this.meal = meal;
        this.saves = saves;
    }

    public Meal getMeal() {
        return meal;
    }

    public long getSaves() {
        return saves;
    }
}

package balancebite.dto.diet;

import balancebite.model.diet.DietPlan;

public class SavedDietPlanDTO {

    private final DietPlan dietPlan;
    private final long saves;

    public SavedDietPlanDTO(DietPlan dietPlan, long saves) {
        this.dietPlan = dietPlan;
        this.saves = saves;
    }

    public DietPlan getDietPlan() {
        return dietPlan;
    }

    public long getSaves() {
        return saves;
    }
}

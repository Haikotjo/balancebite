package balancebite.specification;

import balancebite.model.meal.Meal;
import org.springframework.data.jpa.domain.Specification;

public class MealSpecifications {

    public static Specification<Meal> totalCaloriesBetween(Double min, Double max) {
        return (root, query, cb) -> cb.between(root.get("totalCalories"), min, max);
    }

    public static Specification<Meal> totalProteinBetween(Double min, Double max) {
        return (root, query, cb) -> cb.between(root.get("totalProtein"), min, max);
    }

    public static Specification<Meal> totalCarbsBetween(Double min, Double max) {
        return (root, query, cb) -> cb.between(root.get("totalCarbs"), min, max);
    }

    public static Specification<Meal> totalFatBetween(Double min, Double max) {
        return (root, query, cb) -> cb.between(root.get("totalFat"), min, max);
    }
}

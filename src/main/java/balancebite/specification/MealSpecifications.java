package balancebite.specification;

import balancebite.model.foodItem.FoodSource;
import balancebite.model.meal.Meal;
import balancebite.model.meal.references.Cuisine;
import balancebite.model.meal.references.Diet;
import balancebite.model.meal.references.MealType;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

public class MealSpecifications {

    public static Specification<Meal> isTemplateMeal() {
        return (root, query, cb) -> cb.isTrue(root.get("isTemplate"));
    }

    public static Specification<Meal> isNotPrivate() {
        return (root, query, cb) -> cb.isFalse(root.get("isPrivate"));
    }

    public static Specification<Meal> isNotRestricted() {
        return (root, query, cb) -> cb.isFalse(root.get("isRestricted"));
    }

    public static Specification<Meal> isVisibleToUser(Long userId) {
        return (root, query, cb) -> {
            if (userId == null) {
                return cb.and(cb.isFalse(root.get("isPrivate")), cb.isFalse(root.get("isRestricted")));
            }
            return cb.or(
                    cb.and(cb.isFalse(root.get("isPrivate")), cb.isFalse(root.get("isRestricted"))),
                    cb.equal(root.get("createdBy").get("id"), userId),
                    cb.equal(root.get("adjustedBy").get("id"), userId)
            );
        };
    }

    public static Specification<Meal> hasFoodSource(FoodSource foodSource) {
        return (root, query, cb) -> cb.equal(root.get("foodSource"), foodSource);
    }

    public static Specification<Meal> createdByUser(Long creatorId) {
        return (root, query, cb) -> cb.equal(root.get("createdBy").get("id"), creatorId);
    }

    public static Specification<Meal> hasCuisineIn(List<Cuisine> cuisines) {
        return (root, query, cb) -> {
            query.distinct(true);
            return root.join("cuisines").in(cuisines);
        };
    }

    public static Specification<Meal> hasDietIn(List<Diet> diets) {
        return (root, query, cb) -> {
            query.distinct(true);
            return root.join("diets").in(diets);
        };
    }

    public static Specification<Meal> hasMealTypeIn(List<MealType> mealTypes) {
        return (root, query, cb) -> {
            query.distinct(true);
            return root.join("mealTypes").in(mealTypes);
        };
    }

    public static Specification<Meal> hasAnyFoodItem(List<String> foodItems) {
        return (root, query, cb) -> {
            query.distinct(true);
            return root.join("mealIngredients").join("foodItem").get("name").in(foodItems);
        };
    }

    public static Specification<Meal> totalCaloriesMin(Double min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("totalCalories"), min);
    }

    public static Specification<Meal> totalCaloriesMax(Double max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("totalCalories"), max);
    }

    public static Specification<Meal> totalProteinMin(Double min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("totalProtein"), min);
    }

    public static Specification<Meal> totalProteinMax(Double max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("totalProtein"), max);
    }

    public static Specification<Meal> totalCarbsMin(Double min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("totalCarbs"), min);
    }

    public static Specification<Meal> totalCarbsMax(Double max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("totalCarbs"), max);
    }

    public static Specification<Meal> totalFatMin(Double min) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("totalFat"), min);
    }

    public static Specification<Meal> totalFatMax(Double max) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("totalFat"), max);
    }
}
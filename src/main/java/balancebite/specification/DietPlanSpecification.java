package balancebite.specification;

import balancebite.model.diet.DietPlan;
import balancebite.model.meal.references.Diet;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public class DietPlanSpecification {

    public static Specification<DietPlan> createdOrSavedBy(Long userId) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Object, Object> usersJoin = root.join("users", JoinType.LEFT);
            return cb.or(
                    cb.equal(root.get("createdBy").get("id"), userId),
                    cb.equal(usersJoin.get("id"), userId)
            );
        };
    }

    public static Specification<DietPlan> hasDiet(Diet diet) {
        return (root, query, cb) -> {
            Join<DietPlan, Diet> join = root.join("diets");
            return cb.equal(join, diet);
        };
    }

    public static Specification<DietPlan> totalCaloriesBetween(Double min, Double max) {
        return (root, query, cb) -> cb.between(root.get("totalCalories"), min, max);
    }

    public static Specification<DietPlan> totalProteinBetween(Double min, Double max) {
        return (root, query, cb) -> cb.between(root.get("totalProtein"), min, max);
    }

    public static Specification<DietPlan> totalCarbsBetween(Double min, Double max) {
        return (root, query, cb) -> cb.between(root.get("totalCarbs"), min, max);
    }

    public static Specification<DietPlan> totalFatBetween(Double min, Double max) {
        return (root, query, cb) -> cb.between(root.get("totalFat"), min, max);
    }

    public static Specification<DietPlan> avgCaloriesBetween(Double min, Double max) {
        return (root, query, cb) -> cb.between(root.get("avgCalories"), min, max);
    }

    public static Specification<DietPlan> avgProteinBetween(Double min, Double max) {
        return (root, query, cb) -> cb.between(root.get("avgProtein"), min, max);
    }

    public static Specification<DietPlan> avgCarbsBetween(Double min, Double max) {
        return (root, query, cb) -> cb.between(root.get("avgCarbs"), min, max);
    }

    public static Specification<DietPlan> avgFatBetween(Double min, Double max) {
        return (root, query, cb) -> cb.between(root.get("avgFat"), min, max);
    }

    public static Specification<DietPlan> isTemplateCreatedBy(Long userId) {
        return (root, query, cb) -> cb.and(
                cb.isTrue(root.get("isTemplate")),
                cb.equal(root.get("createdBy").get("id"), userId)
        );
    }

    public static Specification<DietPlan> isTemplate() {
        return (root, query, cb) -> cb.isTrue(root.get("isTemplate"));
    }

    public static Specification<DietPlan> createdBy(Long userId) {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get("createdBy").get("id"), userId),
                cb.isNull(root.get("adjustedBy"))
        );
    }

    public static Specification<DietPlan> savedBy(Long userId) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Object, Object> usersJoin = root.join("users", JoinType.LEFT);
            return cb.equal(usersJoin.get("id"), userId);
        };
    }

    public static Specification<DietPlan> mustIncludeAllDiets(Set<Diet> requiredDiets) {
        return (root, query, cb) -> {
            // Voor elk dieet een subquery maken die controleert of het erin zit
            Predicate[] predicates = requiredDiets.stream()
                    .map(diet -> cb.isMember(diet, root.get("diets")))
                    .toArray(Predicate[]::new);
            return cb.and(predicates);
        };
    }

    public static Specification<DietPlan> mustExcludeAllDiets(Set<Diet> excludedDiets) {
        return (root, query, cb) -> {
            Predicate[] predicates = excludedDiets.stream()
                    .map(diet -> cb.isNotMember(diet, root.get("diets")))
                    .toArray(Predicate[]::new);
            return cb.and(predicates);
        };
    }

}

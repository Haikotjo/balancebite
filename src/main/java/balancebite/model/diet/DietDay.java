package balancebite.model.diet;

import balancebite.dto.NutrientInfoDTO;
import balancebite.model.MealIngredient;
import balancebite.model.meal.Meal;
import balancebite.utils.NutrientCalculatorUtil;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;

@Entity
public class DietDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dayLabel; // e.g. "Day 1", "Day 2", ...

    private LocalDate date; // e.g. 2025-05-01

    @Column(length = 1000)
    private String dietDayDescription;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "diet_plan_id")
    private DietPlan diet;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "diet_day_meals",
            joinColumns = @JoinColumn(name = "diet_day_id"),
            inverseJoinColumns = @JoinColumn(name = "meal_id")
    )
    private List<Meal> meals = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "diet_day_diets", joinColumns = @JoinColumn(name = "diet_day_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "diet")
    private Set<balancebite.model.meal.references.Diet> diets = new HashSet<>();

    // Constructors
    public DietDay() {}

    public DietDay(String dayLabel, LocalDate date, DietPlan diet) {
        this.dayLabel = dayLabel;
        this.date = date;
        this.diet = diet;
    }

    // Getters and setters
    public Long getId() {
        return id;
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

    public DietPlan getDiet() {
        return diet;
    }

    public void setDiet(DietPlan diet) {
        this.diet = diet;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    @Transient
    public List<MealIngredient> getAllMealIngredients() {
        List<MealIngredient> all = new ArrayList<>();
        for (Meal meal : meals) {
            all.addAll(meal.getMealIngredients());
        }
        return all;
    }

    @Transient
    public Map<String, NutrientInfoDTO> getTotalNutrients() {
        return NutrientCalculatorUtil.calculateTotalNutrients(getAllMealIngredients());
    }

    public String getDietDayDescription() {
        return dietDayDescription;
    }

    public void setDietDayDescription(String dietDayDescription) {
        this.dietDayDescription = dietDayDescription;
    }

    public Set<balancebite.model.meal.references.Diet> getDiets() {
        return diets;
    }

    public void setDiets(Set<balancebite.model.meal.references.Diet> diets) {
        this.diets = diets;
    }
}

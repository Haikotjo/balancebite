package balancebite.model.diet;

import balancebite.model.meal.Meal;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private Double totalProtein;
    private Double totalCarbs;
    private Double totalFat;
    private Double totalCalories;
    private Double totalSaturatedFat;
    private Double totalUnsaturatedFat;
    private Double totalSugars;


    // Constructors
    public DietDay() {}

    public DietDay(String dayLabel, LocalDate date, DietPlan diet) {
        this.dayLabel = dayLabel;
        this.date = date;
        this.diet = diet;
    }

    public void updateNutrients() {
        if (meals == null || meals.isEmpty()) {
            this.totalCalories = 0.0;
            this.totalProtein = 0.0;
            this.totalCarbs = 0.0;
            this.totalFat = 0.0;
            this.totalSugars = 0.0;
            this.totalSaturatedFat = 0.0;
            this.totalUnsaturatedFat = 0.0;
            return;
        }

        this.totalCalories = meals.stream().mapToDouble(m -> perServing(m.getTotalCalories(), m.getServings())).sum();
        this.totalProtein = meals.stream().mapToDouble(m -> perServing(m.getTotalProtein(), m.getServings())).sum();
        this.totalCarbs = meals.stream().mapToDouble(m -> perServing(m.getTotalCarbs(), m.getServings())).sum();
        this.totalFat = meals.stream().mapToDouble(m -> perServing(m.getTotalFat(), m.getServings())).sum();
        this.totalSugars = meals.stream().mapToDouble(m -> perServing(m.getTotalSugars(), m.getServings())).sum();
        this.totalSaturatedFat = meals.stream().mapToDouble(m -> perServing(m.getTotalSaturatedFat(), m.getServings())).sum();
        this.totalUnsaturatedFat = meals.stream().mapToDouble(m -> perServing(m.getTotalUnsaturatedFat(), m.getServings())).sum();
    }

    private double perServing(double total, Integer servings) {
        return (servings != null && servings > 1) ? total / servings : total;
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

    public Double getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(Double totalProtein) {
        this.totalProtein = totalProtein;
    }

    public Double getTotalCarbs() {
        return totalCarbs;
    }

    public void setTotalCarbs(Double totalCarbs) {
        this.totalCarbs = totalCarbs;
    }

    public Double getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(Double totalFat) {
        this.totalFat = totalFat;
    }

    public Double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(Double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public Double getTotalSaturatedFat() {
        return totalSaturatedFat;
    }

    public void setTotalSaturatedFat(Double totalSaturatedFat) {
        this.totalSaturatedFat = totalSaturatedFat;
    }

    public Double getTotalUnsaturatedFat() {
        return totalUnsaturatedFat;
    }

    public void setTotalUnsaturatedFat(Double totalUnsaturatedFat) {
        this.totalUnsaturatedFat = totalUnsaturatedFat;
    }

    public Double getTotalSugars() {
        return totalSugars;
    }

    public void setTotalSugars(Double totalSugars) {
        this.totalSugars = totalSugars;
    }
}

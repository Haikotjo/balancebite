package balancebite.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meals")
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Velden voor macronutriënten
    private double proteins;
    private double carbohydrates;
    private double fats;
    private double kcals;

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealIngredient> mealIngredients = new ArrayList<>();

    // Nieuw veld voor vitaminen en mineralen
    @Embedded
    private VitaminsAndMinerals vitaminsAndMinerals;

    // No-argument constructor
    public Meal() {}

    // Parameterized constructor
    public Meal(String name) {
        this.name = name;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MealIngredient> getMealIngredients() {
        return mealIngredients;
    }

    public void addMealIngredient(MealIngredient mealIngredient) {
        mealIngredients.add(mealIngredient);
        mealIngredient.setMeal(this);
    }

    public void addMealIngredients(List<MealIngredient> mealIngredients) {
        for (MealIngredient mealIngredient : mealIngredients) {
            addMealIngredient(mealIngredient);
        }
    }

    // Getters en setters voor macronutriënten
    public double getProteins() {
        return proteins;
    }

    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public double getFats() {
        return fats;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public double getKcals() {
        return kcals;
    }

    public void setKcals(double kcals) {
        this.kcals = kcals;
    }

    // Getters en setters voor vitaminen en mineralen
    public VitaminsAndMinerals getVitaminsAndMinerals() {
        return vitaminsAndMinerals;
    }

    public void setVitaminsAndMinerals(VitaminsAndMinerals vitaminsAndMinerals) {
        this.vitaminsAndMinerals = vitaminsAndMinerals;
    }
}

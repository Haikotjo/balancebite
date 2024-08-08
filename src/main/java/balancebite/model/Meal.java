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

    @OneToMany(mappedBy = "meal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealIngredient> mealIngredients = new ArrayList<>();

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
}

package balancebite.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "food_items")
public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // This annotation maps the nutrients list to the "food_item_nutrients" table,
    // where each NutrientInfo is stored with a foreign key reference to this FoodItem.
    @ElementCollection
    @CollectionTable(name = "food_item_nutrients", joinColumns = @JoinColumn(name = "food_item_id"))
    private List<NutrientInfo> nutrients;

    @OneToMany(mappedBy = "foodItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealIngredient> mealIngredients = new ArrayList<>();

    // No-argument constructor
    public FoodItem() {}

    // Parameterized constructor
    public FoodItem(String name, List<NutrientInfo> nutrients) {
        this.name = name;
        this.nutrients = nutrients;
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

    public List<NutrientInfo> getNutrients() {
        return nutrients;
    }

    public void setNutrients(List<NutrientInfo> nutrients) {
        this.nutrients = nutrients;
    }

    public List<MealIngredient> getMealIngredients() {
        return mealIngredients;
    }

    public void setMealIngredients(List<MealIngredient> mealIngredients) {
        this.mealIngredients = mealIngredients;
    }
}

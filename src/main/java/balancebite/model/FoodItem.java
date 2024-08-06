package balancebite.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "food_items")
public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ElementCollection
    @CollectionTable(name = "food_item_nutrients", joinColumns = @JoinColumn(name = "food_item_id"))
    private List<NutrientInfo> nutrients;

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
}

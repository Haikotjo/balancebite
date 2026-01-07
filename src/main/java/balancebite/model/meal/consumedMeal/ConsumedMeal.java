package balancebite.model.meal.consumedMeal;

import balancebite.model.RecommendedDailyIntake;
import balancebite.model.meal.Meal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "consumed_meal")
public class ConsumedMeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "recommended_daily_intake_id", nullable = false)
    private RecommendedDailyIntake rdi;

    // Snapshot van de meal
    @Column(nullable = false)
    private Long mealId;

    @Column(nullable = false)
    private String mealName;

    @Column(nullable = false)
    private double totalCalories;

    @Column(nullable = false)
    private double totalProtein;

    @Column(nullable = false)
    private double totalCarbs;

    @Column(nullable = false)
    private double totalFat;

    private Double totalSugars;
    private Double totalSaturatedFat;
    private Double totalUnsaturatedFat;

    // Wanneer gegeten
    @Column(nullable = false)
    private LocalDate consumedDate;

    @Column(nullable = false)
    private LocalTime consumedTime;

    protected ConsumedMeal() {
        // JPA only
    }

    public ConsumedMeal(RecommendedDailyIntake rdi, Meal meal) {
        this.rdi = rdi;
        this.mealId = meal.getId();
        this.mealName = meal.getName();
        this.totalCalories = meal.getTotalCalories();
        this.totalProtein = meal.getTotalProtein();
        this.totalCarbs = meal.getTotalCarbs();
        this.totalFat = meal.getTotalFat();
        this.totalSugars = meal.getTotalSugars();
        this.totalSaturatedFat = meal.getTotalSaturatedFat();
        this.totalUnsaturatedFat = meal.getTotalUnsaturatedFat();
        this.consumedDate = LocalDate.now();
        this.consumedTime = LocalTime.now();
    }

    public Long getId() {
        return id;
    }

    public LocalDate getConsumedDate() {
        return consumedDate;
    }

    public LocalTime getConsumedTime() {
        return consumedTime;
    }

    public Long getMealId() {
        return mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public double getTotalCalories() {
        return totalCalories;
    }

    public double getTotalProtein() {
        return totalProtein;
    }

    public double getTotalCarbs() {
        return totalCarbs;
    }

    public double getTotalFat() {
        return totalFat;
    }

    public Double getTotalSugars() {
        return totalSugars;
    }

    public Double getTotalSaturatedFat() {
        return totalSaturatedFat;
    }

    public Double getTotalUnsaturatedFat() {
        return totalUnsaturatedFat;
    }

}

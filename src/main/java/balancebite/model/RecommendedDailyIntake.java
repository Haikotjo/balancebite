package balancebite.model;

import balancebite.model.meal.consumedMeal.ConsumedMeal;
import balancebite.model.user.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the recommended daily intake of nutrients for a user.
 * This entity stores a set of nutrients and their respective recommended intake values.
 */
@Entity
@Table(name = "recommended_daily_intake")
public class RecommendedDailyIntake {

    /**
     * The unique identifier for the recommended daily intake entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * A set of nutrients and their recommended daily intake values.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "recommended_daily_intake_id")
    private Set<Nutrient> nutrients = new HashSet<>();

    /**
     * The user to whom this recommended daily intake belongs.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * The timestamp when the recommended daily intake was created.
     */
    private LocalDate createdAt;

    @OneToMany(mappedBy = "rdi", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ConsumedMeal> consumedMeals = new HashSet<>();


    /**
     * Default constructor for creating a RecommendedDailyIntake entity.
     * Initializes the recommended daily intake map with default values for various nutrients.
     */
    public RecommendedDailyIntake() {
        this.createdAt = LocalDate.now();

        // Proximates
//        addNutrient("Water", 3700.0);
        addNutrient("Energy kcal", null);
        addNutrient("Protein", null);
        addNutrient("Total lipid (fat)", null);
        addNutrient("Carbohydrates", null);
//        addNutrient("Fiber, total dietary", 38.0);
//        addNutrient("Total Sugars", 50.0);
//        addNutrient("Carbohydrate, by difference", 130.0);
//        addNutrient("Ash", null);

        // Minerals
//        addNutrient("Calcium, Ca", 1300.0);
//        addNutrient("Iron, Fe", 18.0);
//        addNutrient("Magnesium, Mg", 420.0);
//        addNutrient("Phosphorus, P", 700.0);
//        addNutrient("Potassium, K", 4700.0);
//        addNutrient("Sodium, Na", 2300.0);
//        addNutrient("Zinc, Zn", 11.0);
//        addNutrient("Copper, Cu", 0.9);
//        addNutrient("Manganese, Mn", 2.3);
//        addNutrient("Selenium, Se", 55.0);

        // Vitamins and Other Components
//        addNutrient("Vitamin C, total ascorbic acid", 90.0);
//        addNutrient("Thiamin", 1.2);
//        addNutrient("Riboflavin", 1.3);
//        addNutrient("Niacin", 16.0);
//        addNutrient("Pantothenic acid", 5.0);
//        addNutrient("Vitamin B-6", 1.3);
//        addNutrient("Folate, total", 400.0);
//        addNutrient("Folic acid", 400.0);
//        addNutrient("Folate, food", 400.0);
//        addNutrient("Folate, DFE", 400.0);
//        addNutrient("Choline, total", 550.0);
//        addNutrient("Vitamin B-12", 2.4);
//        addNutrient("Vitamin B-12, added", null);
//        addNutrient("Vitamin A, RAE", 900.0);
//        addNutrient("Retinol", 900.0);
//        addNutrient("Carotene, beta", null);
//        addNutrient("Carotene, alpha", null);
//        addNutrient("Cryptoxanthin, beta", null);
//        addNutrient("Vitamin A, IU", 3000.0);
//        addNutrient("Lycopene", null);
//        addNutrient("Lutein + zeaxanthin", null);
//        addNutrient("Vitamin E (alpha-tocopherol)", 15.0);
//        addNutrient("Vitamin E, added", null);
//        addNutrient("Vitamin D (D2 + D3), International Units", 800.0);
//        addNutrient("Vitamin D (D2 + D3)", 20.0);
//        addNutrient("Vitamin K (phylloquinone)", 120.0);
//        addNutrient("Vitamin K (Dihydrophylloquinone)", null);

        // Lipids
        addNutrient("Saturated and Trans fats", null);
        addNutrient("Mono- and Polyunsaturated fats", null);
//        addNutrient("Fatty acids, total saturated", null);
//        addNutrient("SFA 4:0", null);
//        addNutrient("SFA 6:0", null);
//        addNutrient("SFA 8:0", null);
//        addNutrient("SFA 10:0", null);
//        addNutrient("SFA 12:0", null);
//        addNutrient("SFA 14:0", null);
//        addNutrient("SFA 16:0", null);
//        addNutrient("SFA 18:0", null);
//        addNutrient("Fatty acids, total monounsaturated", null);
//        addNutrient("MUFA 16:1", null);
//        addNutrient("MUFA 18:1", null);
//        addNutrient("MUFA 20:1", null);
//        addNutrient("MUFA 22:1", null);
//        addNutrient("Fatty acids, total polyunsaturated", null);
//        addNutrient("PUFA 18:2", null);
//        addNutrient("PUFA 18:3", 1.6);
//        addNutrient("PUFA 18:4", null);
//        addNutrient("PUFA 20:4", null);
//        addNutrient("PUFA 20:5 n-3 (EPA)", null);
//        addNutrient("PUFA 22:5 n-3 (DPA)", null);
//        addNutrient("PUFA 22:6 n-3 (DHA)", null);
//        addNutrient("Fatty acids, total trans", null);
//        addNutrient("Cholesterol", 300.0);

        // Amino acids
//        addNutrient("Tryptophan", 280.0);
//        addNutrient("Threonine", 1050.0);
//        addNutrient("Isoleucine", 1400.0);
//        addNutrient("Leucine", 2730.0);
//        addNutrient("Lysine", 2100.0);
//        addNutrient("Methionine", 728.0);
//        addNutrient("Cystine", 287.0);
//        addNutrient("Phenylalanine", 875.0);
//        addNutrient("Tyrosine", 875.0);
//        addNutrient("Valine", 1820.0);
//        addNutrient("Arginine", null);
//        addNutrient("Histidine", 700.0);
//        addNutrient("Alanine", null);
//        addNutrient("Aspartic acid", null);
//        addNutrient("Glutamic acid", null);
//        addNutrient("Glycine", null);
//        addNutrient("Proline", null);
//        addNutrient("Serine", null);

        // Other Components
//        addNutrient("Alcohol, ethyl", null);
//        addNutrient("Caffeine", 400.0);
//        addNutrient("Theobromine", null);
//        addNutrient("Sucrose", null);
//        addNutrient("Glucose", null);
//        addNutrient("Fructose", null);
//        addNutrient("Lactose", null);
//        addNutrient("Maltose", null);
//        addNutrient("Galactose", null);
//        addNutrient("Starch", null);
//        addNutrient("Betaine", null);
//        addNutrient("Tocopherol, beta", null);
//        addNutrient("Tocopherol, gamma", null);
//        addNutrient("Tocopherol, delta", null);
//        addNutrient("Tocotrienol, alpha", null);
//        addNutrient("Tocotrienol, beta", null);
//        addNutrient("Tocotrienol, gamma", null);
//        addNutrient("Tocotrienol, delta", null);
//        addNutrient("SFA 15:0", null);
//        addNutrient("SFA 17:0", null);
//        addNutrient("SFA 20:0", null);
//        addNutrient("SFA 22:0", null);
//        addNutrient("SFA 24:0", null);
//        addNutrient("MUFA 14:1", null);
//        addNutrient("Fluoride, F", 4.0);
//        addNutrient("Phytosterols", null);
    }

    /**
     * Adds a new nutrient with its recommended value to the nutrient set.
     *
     * @param name  The name of the nutrient.
     * @param value The recommended value for the nutrient.
     */
    private void addNutrient(String name, Double value) {
        nutrients.add(new Nutrient(name, value));
    }

    /**
     * Retrieves the set of all nutrients.
     *
     * @return A set of all nutrients and their recommended values.
     */
    public Set<Nutrient> getNutrients() {
        return nutrients;
    }

    /**
     * Sets the user to whom this recommended daily intake belongs.
     *
     * @param user The user to associate with this recommended daily intake.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Retrieves the user associated with this recommended daily intake.
     *
     * @return The user associated with this recommended daily intake.
     */
    public User getUser() {
        return user;
    }

    /**
     * Retrieves the timestamp when the recommended daily intake was created.
     *
     * @return The timestamp when this recommended daily intake was created.
     */
    public LocalDate getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the recommended daily intake was created.
     *
     * @param createdAt The timestamp to set for the recommended daily intake.
     */
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<ConsumedMeal> getConsumedMeals() {
        return consumedMeals;
    }
}

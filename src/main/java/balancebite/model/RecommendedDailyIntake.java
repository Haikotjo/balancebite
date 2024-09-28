package balancebite.model;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the recommended daily intake of nutrients for a user.
 * This entity stores a map of nutrients and their respective recommended intake values.
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
     * A map of nutrients and their recommended daily intake values.
     * The nutrient name is the key, and the corresponding value is the recommended intake.
     */
    @ElementCollection
    @MapKeyColumn(name = "nutrient_name")
    @Column(name = "nutrient_value")
    @CollectionTable(name = "intake_map", joinColumns = @JoinColumn(name = "intake_id"))
    private Map<String, Double> intakeMap = new HashMap<>();

    /**
     * Default constructor for creating a RecommendedDailyIntake entity.
     * Initializes the recommended daily intake map with default values for various nutrients.
     */
    public RecommendedDailyIntake() {

        // Proximates
        intakeMap.put("Water", 3700.0); // in ml
        intakeMap.put("Energy kcal", 2500.0); // in kcal
        intakeMap.put("Protein", 56.0); // in grams
        intakeMap.put("Total lipid (fat)", 78.0); // in grams
//        intakeMap.put("Ash", null);
        intakeMap.put("Carbohydrates", 130.0); // in grams
        intakeMap.put("Carbohydrate, by difference", 130.0); // in grams
        intakeMap.put("Fiber, total dietary", 38.0); // in grams
        intakeMap.put("Total Sugars", 50.0); // in grams

        // Minerals
        intakeMap.put("Calcium, Ca", 1300.0); // in mg
        intakeMap.put("Iron, Fe", 18.0); // in mg
        intakeMap.put("Magnesium, Mg", 420.0); // in mg
        intakeMap.put("Phosphorus, P", 700.0); // in mg
        intakeMap.put("Potassium, K", 4700.0); // in mg
        intakeMap.put("Sodium, Na", 2300.0); // in mg
        intakeMap.put("Zinc, Zn", 11.0); // in mg
        intakeMap.put("Copper, Cu", 0.9); // in mg
        intakeMap.put("Manganese, Mn", 2.3); // in mg
        intakeMap.put("Selenium, Se", 55.0); // in mcg

        // Vitamins and Other Components
        intakeMap.put("Vitamin C, total ascorbic acid", 90.0); // in mg
        intakeMap.put("Thiamin", 1.2); // in mg
        intakeMap.put("Riboflavin", 1.3); // in mg
        intakeMap.put("Niacin", 16.0); // in mg
        intakeMap.put("Pantothenic acid", 5.0); // in mg
        intakeMap.put("Vitamin B-6", 1.3); // in mg
        intakeMap.put("Folate, total", 400.0); // in mcg
        intakeMap.put("Folic acid", 400.0); // in mcg
        intakeMap.put("Folate, food", 400.0); // in mcg
        intakeMap.put("Folate, DFE", 400.0); // in mcg
        intakeMap.put("Choline, total", 550.0); // in mg
        intakeMap.put("Vitamin B-12", 2.4); // in mcg
//        intakeMap.put("Vitamin B-12, added", null);
        intakeMap.put("Vitamin A, RAE", 900.0); // in mcg
        intakeMap.put("Retinol", 900.0); // in mcg
//        intakeMap.put("Carotene, beta", null);
//        intakeMap.put("Carotene, alpha", null);
//        intakeMap.put("Cryptoxanthin, beta", null);
        intakeMap.put("Vitamin A, IU", 3000.0); // in IU
//        intakeMap.put("Lycopene", null);
//        intakeMap.put("Lutein + zeaxanthin", null);
        intakeMap.put("Vitamin E (alpha-tocopherol)", 15.0); // in mg
//        intakeMap.put("Vitamin E, added", null);
        intakeMap.put("Vitamin D (D2 + D3), International Units", 800.0); // in IU
        intakeMap.put("Vitamin D (D2 + D3)", 20.0); // in mcg
        intakeMap.put("Vitamin K (phylloquinone)", 120.0); // in mcg

        // Lipids
        intakeMap.put("Fatty acids, total saturated", 20.0); // in grams
//        intakeMap.put("SFA 4:0", null);
//        intakeMap.put("SFA 6:0", null);
//        intakeMap.put("SFA 8:0", null);
//        intakeMap.put("SFA 10:0", null);
//        intakeMap.put("SFA 12:0", null);
//        intakeMap.put("SFA 14:0", null);
//        intakeMap.put("SFA 16:0", null);
//        intakeMap.put("SFA 18:0", null);
//        intakeMap.put("Fatty acids, total monounsaturated", null);
//        intakeMap.put("MUFA 16:1", null);
//        intakeMap.put("MUFA 18:1", null);
//        intakeMap.put("MUFA 20:1", null);
//        intakeMap.put("MUFA 22:1", null);
//        intakeMap.put("Fatty acids, total polyunsaturated", 17.0); // in grams
//        intakeMap.put("PUFA 18:2", null);
//        intakeMap.put("PUFA 18:3", 1.6); // in grams
//        intakeMap.put("PUFA 18:4", null);
//        intakeMap.put("PUFA 20:4", null);
//        intakeMap.put("PUFA 20:5 n-3 (EPA)", null);
//        intakeMap.put("PUFA 22:5 n-3 (DPA)", null);
//        intakeMap.put("PUFA 22:6 n-3 (DHA)", null);
//        intakeMap.put("Fatty acids, total trans", null);
        intakeMap.put("Cholesterol", 300.0); // in mg

        // Amino acids
        intakeMap.put("Tryptophan", 280.0); // in mg
        intakeMap.put("Threonine", 1050.0); // in mg
        intakeMap.put("Isoleucine", 1400.0); // in mg
        intakeMap.put("Leucine", 2730.0); // in mg
        intakeMap.put("Lysine", 2100.0); // in mg
        intakeMap.put("Methionine", 728.0); // in mg
        intakeMap.put("Cystine", 287.0); // in mg
        intakeMap.put("Phenylalanine", 875.0); // in mg
        intakeMap.put("Tyrosine", 875.0); // in mg
        intakeMap.put("Valine", 1820.0); // in mg
//        intakeMap.put("Arginine", null);
        intakeMap.put("Histidine", 700.0); // in mg
//        intakeMap.put("Alanine", null);
//        intakeMap.put("Aspartic acid", null);
//        intakeMap.put("Glutamic acid", null);
//        intakeMap.put("Glycine", null);
//        intakeMap.put("Proline", null);
//        intakeMap.put("Serine", null);

        // Other Components
//        intakeMap.put("Alcohol, ethyl", null);
        intakeMap.put("Caffeine", 400.0); // in mg
//        intakeMap.put("Theobromine", null);
//        intakeMap.put("Sucrose", null);
//        intakeMap.put("Glucose", null);
//        intakeMap.put("Fructose", null);
//        intakeMap.put("Lactose", null);
//        intakeMap.put("Maltose", null);
//        intakeMap.put("Galactose", null);
//        intakeMap.put("Starch", null);
//        intakeMap.put("Betaine", null);
//        intakeMap.put("Tocopherol, beta", null);
//        intakeMap.put("Tocopherol, gamma", null);
//        intakeMap.put("Tocopherol, delta", null);
//        intakeMap.put("Tocotrienol, alpha", null);
//        intakeMap.put("Tocotrienol, beta", null);
//        intakeMap.put("Tocotrienol, gamma", null);
//        intakeMap.put("Tocotrienol, delta", null);
//        intakeMap.put("Vitamin K (Dihydrophylloquinone)", null);
//        intakeMap.put("SFA 15:0", null);
//        intakeMap.put("SFA 17:0", null);
//        intakeMap.put("SFA 20:0", null);
//        intakeMap.put("SFA 22:0", null);
//        intakeMap.put("SFA 24:0", null);
//        intakeMap.put("MUFA 14:1", null);
        intakeMap.put("Fluoride, F", 4.0); // in mg
//        intakeMap.put("Phytosterols", null);
    }

    /**
     * Retrieves the recommended intake value for a given nutrient.
     *
     * @param nutrient The name of the nutrient to retrieve.
     * @return The recommended intake value for the nutrient, or null if not found.
     */
    public Double getRecommendedIntake(String nutrient) {
        return intakeMap.getOrDefault(nutrient, null);
    }

    /**
     * Retrieves the entire map of recommended nutrient intake values.
     *
     * @return A map containing all nutrient names and their recommended intake values.
     */
    public Map<String, Double> getAllRecommendedIntakes() {
        return intakeMap;
    }

    /**
     * Updates the intake value for a specific nutrient.
     * If the nutrient does not exist in the map, the update will not take place.
     *
     * @param nutrientName The name of the nutrient to update.
     * @param updatedValue The new recommended intake value for the nutrient.
     */
    public void updateIntake(String nutrientName, Double updatedValue) {
        if (intakeMap.containsKey(nutrientName)) {
            intakeMap.put(nutrientName, updatedValue);
        } else {
            System.out.println("Nutrient not found: " + nutrientName);
        }
    }
}

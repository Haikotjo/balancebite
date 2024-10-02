package balancebite.model;

import jakarta.persistence.*;

/**
 * Represents a nutrient entity.
 * Each nutrient has a name (e.g., "Protein", "Carbohydrates") and a corresponding value.
 * This entity is used to store nutrient information for meals or recommended daily intake.
 */
@Entity
@Table(name = "nutrients")
public class Nutrient {

    /**
     * The unique identifier for the nutrient.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the nutrient (e.g., "Protein", "Carbohydrates").
     */
    private String name;

    /**
     * The value of the nutrient (e.g., 56.0 for protein in grams).
     */
    private Double value;

    /**
     * Default no-argument constructor for JPA.
     */
    public Nutrient() {
        // Default constructor for JPA
    }

    /**
     * Full constructor for creating a Nutrient entity.
     *
     * @param name  The name of the nutrient (e.g., "Protein").
     * @param value The value of the nutrient (e.g., 56.0 grams).
     */
    public Nutrient(String name, Double value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Gets the unique identifier of the nutrient.
     *
     * @return The ID of the nutrient.
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the name of the nutrient.
     *
     * @return The name of the nutrient (e.g., "Protein").
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the nutrient.
     *
     * @param name The name of the nutrient to set (e.g., "Carbohydrates").
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the value of the nutrient.
     *
     * @return The value of the nutrient (e.g., 56.0 grams).
     */
    public Double getValue() {
        return value;
    }

    /**
     * Sets the value of the nutrient.
     *
     * @param value The value of the nutrient to set (e.g., 130.0 grams).
     */
    public void setValue(Double value) {
        this.value = value;
    }
}

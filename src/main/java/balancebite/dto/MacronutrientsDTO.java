package balancebite.dto;

public class MacronutrientsDTO {
    private double proteins; // in grams
    private double carbohydrates; // in grams
    private double fats; // in grams

    // Constructor
    public MacronutrientsDTO() {}

    public MacronutrientsDTO(double proteins, double carbohydrates, double fats) {
        this.proteins = proteins;
        this.carbohydrates = carbohydrates;
        this.fats = fats;
    }

    // Getters and Setters
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
}

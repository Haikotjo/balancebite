package balancebite.model;

import jakarta.persistence.*;

@Entity
@Table(name = "daily_intakes")
public class DailyIntake {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    private Long userId;

    // Macronutriënten
    private double proteins = 60.0;
    private double carbohydrates = 300.0;
    private double fats = 80.0;
    private double fiber = 30.0;
    private double water = 2500.0; // in milliliters

    // Energy
    private double kcals = 2000.0; // standaard dagelijkse kcal
    private double kJ = 8374.0; // standaard dagelijkse kJ

    // Vitamines
    private double vitaminA = 900.0; // in µg
    private double vitaminC = 90.0; // in mg
    private double vitaminD = 20.0; // in µg
    private double vitaminE = 15.0; // in mg
    private double vitaminK = 120.0; // in µg

    // B-Vitamines
    private double thiamin = 1.2; // in mg
    private double riboflavin = 1.3; // in mg
    private double niacin = 16.0; // in mg
    private double vitaminB6 = 1.7; // in mg
    private double folate = 400.0; // in µg
    private double vitaminB12 = 2.4; // in µg
    private double pantothenicAcid = 5.0; // in mg
    private double biotin = 30.0; // in µg

    // Mineralen
    private double calcium = 1000.0; // in mg
    private double iron = 18.0; // in mg
    private double magnesium = 420.0; // in mg
    private double phosphorus = 700.0; // in mg
    private double potassium = 3500.0; // in mg
    private double sodium = 2300.0; // in mg
    private double zinc = 11.0; // in mg
    private double copper = 900.0; // in µg
    private double manganese = 2.3; // in mg
    private double selenium = 55.0; // in µg

    // Vetzuurprofiel
    private double saturatedFats = 20.0; // in g
    private double cholesterol = 300.0; // in mg

    // Methode om maaltijdinname van dagelijkse behoefte af te trekken
    public void subtractMealIntake(Meal meal) {
        // Hier moet je de maaltijd doorlopen en de corresponderende waarden aftrekken van de DailyIntake-velden
        for (MealIngredient ingredient : meal.getMealIngredients()) {
            FoodItem foodItem = ingredient.getFoodItem();

            // Trek de voedingswaarden van de maaltijd af van de dagelijkse waarden
            for (NutrientInfo nutrient : foodItem.getNutrients()) {
                double nutrientValue = nutrient.getValue() * (ingredient.getQuantity() / 100.0);
                String nutrientName = nutrient.getNutrientName().toLowerCase();
                String unitName = nutrient.getUnitName().toLowerCase();

                switch (nutrientName) {
                    case "protein":
                    case "protein (g)": // Correctie als er een eenheid in nutrient_name zit
                        proteins -= nutrientValue;
                        break;
                    case "carbohydrate, by difference":
                    case "carbohydrates":
                        carbohydrates -= nutrientValue;
                        break;
                    case "total lipid (fat)":
                    case "fat":
                        fats -= nutrientValue;
                        break;
                    case "fiber, total dietary":
                        fiber -= nutrientValue;
                        break;
                    case "water":
                        water -= nutrientValue;
                        break;
                    case "energy":
                        if (unitName.equals("kcal")) {
                            kcals -= nutrientValue;
                            System.out.println("Afgetrokken kcals: " + nutrientValue);
                            System.out.println("Resterende kcals: " + kcals);
                        } else if (unitName.equals("kj")) {
                            kJ -= nutrientValue;
                            System.out.println("Afgetrokken kJ: " + nutrientValue);
                            System.out.println("Resterende kJ: " + kJ);
                        }
                        break;
                    case "vitamin a, rae":
                        vitaminA -= nutrientValue;
                        break;
                    case "vitamin c, total ascorbic acid":
                        vitaminC -= nutrientValue;
                        break;
                    case "vitamin d (d2 + d3)":
                        vitaminD -= nutrientValue;
                        break;
                    case "vitamin e (alpha-tocopherol)":
                        vitaminE -= nutrientValue;
                        break;
                    case "vitamin k (phylloquinone)":
                        vitaminK -= nutrientValue;
                        break;
                    case "thiamin":
                        thiamin -= nutrientValue;
                        break;
                    case "riboflavin":
                        riboflavin -= nutrientValue;
                        break;
                    case "niacin":
                        niacin -= nutrientValue;
                        break;
                    case "vitamin b-6":
                        vitaminB6 -= nutrientValue;
                        break;
                    case "folate, total":
                        folate -= nutrientValue;
                        break;
                    case "vitamin b-12":
                        vitaminB12 -= nutrientValue;
                        break;
                    case "pantothenic acid":
                        pantothenicAcid -= nutrientValue;
                        break;
                    case "biotin":
                        biotin -= nutrientValue;
                        break;
                    case "calcium, ca":
                        calcium -= nutrientValue;
                        break;
                    case "iron, fe":
                        iron -= nutrientValue;
                        break;
                    case "magnesium, mg":
                        magnesium -= nutrientValue;
                        break;
                    case "phosphorus, p":
                        phosphorus -= nutrientValue;
                        break;
                    case "potassium, k":
                        potassium -= nutrientValue;
                        break;
                    case "sodium, na":
                        sodium -= nutrientValue;
                        break;
                    case "zinc, zn":
                        zinc -= nutrientValue;
                        break;
                    case "copper, cu":
                        copper -= nutrientValue;
                        break;
                    case "manganese, mn":
                        manganese -= nutrientValue;
                        break;
                    case "selenium, se":
                        selenium -= nutrientValue;
                        break;
                    case "fatty acids, total saturated":
                        saturatedFats -= nutrientValue;
                        break;
                    case "cholesterol":
                        cholesterol -= nutrientValue;
                        break;
                    default:
                        // Negeer andere nutriënten
                        break;
                }
            }
        }
    }

    public Long getId() {
        return id;
    }
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

    public double getFiber() {
        return fiber;
    }

    public void setFiber(double fiber) {
        this.fiber = fiber;
    }

    public double getWater() {
        return water;
    }

    public void setWater(double water) {
        this.water = water;
    }

    public double getKcals() {
        return kcals;
    }

    public void setKcals(double kcals) {
        this.kcals = kcals;
    }

    public double getkJ() {
        return kJ;
    }

    public void setkJ(double kJ) {
        this.kJ = kJ;
    }

    public double getVitaminA() {
        return vitaminA;
    }

    public void setVitaminA(double vitaminA) {
        this.vitaminA = vitaminA;
    }

    public double getVitaminC() {
        return vitaminC;
    }

    public void setVitaminC(double vitaminC) {
        this.vitaminC = vitaminC;
    }

    public double getVitaminD() {
        return vitaminD;
    }

    public void setVitaminD(double vitaminD) {
        this.vitaminD = vitaminD;
    }

    public double getVitaminE() {
        return vitaminE;
    }

    public void setVitaminE(double vitaminE) {
        this.vitaminE = vitaminE;
    }

    public double getVitaminK() {
        return vitaminK;
    }

    public void setVitaminK(double vitaminK) {
        this.vitaminK = vitaminK;
    }

    public double getThiamin() {
        return thiamin;
    }

    public void setThiamin(double thiamin) {
        this.thiamin = thiamin;
    }

    public double getRiboflavin() {
        return riboflavin;
    }

    public void setRiboflavin(double riboflavin) {
        this.riboflavin = riboflavin;
    }

    public double getNiacin() {
        return niacin;
    }

    public void setNiacin(double niacin) {
        this.niacin = niacin;
    }

    public double getVitaminB6() {
        return vitaminB6;
    }

    public void setVitaminB6(double vitaminB6) {
        this.vitaminB6 = vitaminB6;
    }

    public double getFolate() {
        return folate;
    }

    public void setFolate(double folate) {
        this.folate = folate;
    }

    public double getVitaminB12() {
        return vitaminB12;
    }

    public void setVitaminB12(double vitaminB12) {
        this.vitaminB12 = vitaminB12;
    }

    public double getPantothenicAcid() {
        return pantothenicAcid;
    }

    public void setPantothenicAcid(double pantothenicAcid) {
        this.pantothenicAcid = pantothenicAcid;
    }

    public double getBiotin() {
        return biotin;
    }

    public void setBiotin(double biotin) {
        this.biotin = biotin;
    }

    public double getCalcium() {
        return calcium;
    }

    public void setCalcium(double calcium) {
        this.calcium = calcium;
    }

    public double getIron() {
        return iron;
    }

    public void setIron(double iron) {
        this.iron = iron;
    }

    public double getMagnesium() {
        return magnesium;
    }

    public void setMagnesium(double magnesium) {
        this.magnesium = magnesium;
    }

    public double getPhosphorus() {
        return phosphorus;
    }

    public void setPhosphorus(double phosphorus) {
        this.phosphorus = phosphorus;
    }

    public double getPotassium() {
        return potassium;
    }

    public void setPotassium(double potassium) {
        this.potassium = potassium;
    }

    public double getSodium() {
        return sodium;
    }

    public void setSodium(double sodium) {
        this.sodium = sodium;
    }

    public double getZinc() {
        return zinc;
    }

    public void setZinc(double zinc) {
        this.zinc = zinc;
    }

    public double getCopper() {
        return copper;
    }

    public void setCopper(double copper) {
        this.copper = copper;
    }

    public double getManganese() {
        return manganese;
    }

    public void setManganese(double manganese) {
        this.manganese = manganese;
    }

    public double getSelenium() {
        return selenium;
    }

    public void setSelenium(double selenium) {
        this.selenium = selenium;
    }

    public double getSaturatedFats() {
        return saturatedFats;
    }

    public void setSaturatedFats(double saturatedFats) {
        this.saturatedFats = saturatedFats;
    }

    public double getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(double cholesterol) {
        this.cholesterol = cholesterol;
    }
}

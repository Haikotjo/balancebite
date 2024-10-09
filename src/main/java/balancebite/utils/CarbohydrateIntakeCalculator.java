package balancebite.utils;

import balancebite.model.User;

/**
 * Utility class for calculating the recommended daily carbohydrate intake.
 */
public class CarbohydrateIntakeCalculator {

    /**
     * Calculates the recommended daily carbohydrate intake based on the user's total energy intake,
     * and the already calculated energy contributions from protein and fat.
     *
     * @param totalEnergyKcal The total daily energy intake in kcal.
     * @param proteinIntakeGrams The amount of protein intake in grams.
     * @param fatIntakeGrams The amount of fat intake in grams.
     * @return The recommended daily carbohydrate intake in grams.
     */
    public static double calculateCarbohydrateIntake(double totalEnergyKcal, double proteinIntakeGrams, double fatIntakeGrams) {
        // Calculate the total kcal coming from protein and fat
        double proteinKcal = proteinIntakeGrams * 4; // 1 gram of protein = 4 kcal
        double fatKcal = fatIntakeGrams * 9; // 1 gram of fat = 9 kcal

        // Calculate the remaining kcal for carbohydrates
        double remainingKcal = totalEnergyKcal - (proteinKcal + fatKcal);

        // Convert the remaining kcal to grams of carbohydrates (1 gram of carbohydrate = 4 kcal)
        return remainingKcal / 4;
    }
}

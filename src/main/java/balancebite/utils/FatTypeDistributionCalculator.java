package balancebite.utils;

/**
 * Utility class for distributing the total fat intake into saturated and unsaturated fats.
 */
public class FatTypeDistributionCalculator {

    /**
     * Calculates the distribution of the total fat intake into saturated and unsaturated fats.
     *
     * @param totalFatGrams The total fat intake in grams.
     * @return A FatTypeDistribution object containing the amounts of saturated and unsaturated fats in grams.
     */
    public static FatTypeDistribution calculateFatDistribution(double totalFatGrams) {
        double saturatedFat = totalFatGrams * 0.30; // 30% of the total fat is saturated fat
        double unsaturatedFat = totalFatGrams * 0.70; // 70% of the total fat is unsaturated fat

        return new FatTypeDistribution(saturatedFat, unsaturatedFat);
    }

    /**
     * Class representing the recommended distribution of different types of fats.
     */
    public static class FatTypeDistribution {
        private final double saturatedFat;
        private final double unsaturatedFat;

        public FatTypeDistribution(double saturatedFat, double unsaturatedFat) {
            this.saturatedFat = saturatedFat;
            this.unsaturatedFat = unsaturatedFat;
        }

        public double getSaturatedFat() {
            return saturatedFat;
        }

        public double getUnsaturatedFat() {
            return unsaturatedFat;
        }
    }
}
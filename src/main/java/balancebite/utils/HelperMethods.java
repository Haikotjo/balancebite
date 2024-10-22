package balancebite.utils;

import java.util.Objects;

/**
 * Utility class providing common helper methods for the application.
 * This class includes methods to handle null-checks and normalization tasks.
 * Using these methods helps avoid code duplication and ensures consistency throughout the codebase.
 */
public class HelperMethods {

    /**
     * Retrieves the value of a {@link Double} safely, returning a default value if the input is null.
     * This method helps to avoid repetitive null-checks and provides a consistent way to handle nullable {@link Double} values.
     *
     * @param value The {@link Double} value to be checked.
     * @return The {@link Double} value if not null, or 0.0 if the input is null.
     */
    public static double getValueOrDefault(Double value) {
        return value != null ? value : 0.0;
    }

    /**
     * Normalizes nutrient names by converting them to lowercase, removing units like "g", "mg", and "µg"
     * only at the end of the string, and then removing all spaces.
     * This ensures consistency in nutrient name representation across the application.
     *
     * @param nutrientName The nutrient name to normalize.
     * @return The normalized nutrient name without units at the end and without spaces.
     * @throws NullPointerException if the input nutrientName is null.
     */
    public static String normalizeNutrientName(String nutrientName) {
        Objects.requireNonNull(nutrientName, "Nutrient name cannot be null");
        return nutrientName.toLowerCase()
                .replaceAll("\s(g|mg|µg)$", "")  // Remove " g", " mg", " µg" at the end
                .replace(" ", "");  // Remove all remaining spaces
    }
}

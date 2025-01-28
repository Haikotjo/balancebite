package balancebite.model.meal.references;

/**
 * Enum representing different dietary preferences and restrictions.
 * This helps categorize meals based on nutritional needs and exclusions.
 *
 * Examples include:
 * - VEGETARIAN: No meat, but may include dairy and eggs.
 * - VEGAN: No animal products at all.
 * - GLUTEN_FREE: No gluten-containing ingredients.
 */
public enum Diet {

    /**
     * Vegetarian diet, excludes meat but allows dairy and eggs.
     */
    VEGETARIAN,

    /**
     * Vegan diet, excludes all animal products, including dairy and eggs.
     */
    VEGAN,

    /**
     * Pescatarian diet, excludes meat but includes fish and seafood.
     */
    PESCATARIAN,

    /**
     * Gluten-free diet, excludes wheat, barley, and rye.
     */
    GLUTEN_FREE,

    /**
     * Dairy-free diet, excludes all milk-based products.
     */
    DAIRY_FREE,

    /**
     * Nut-free diet, excludes all tree nuts and peanuts.
     */
    NUT_FREE,

    /**
     * Soy-free diet, excludes soy and soy-derived products.
     */
    SOY_FREE,

    /**
     * Low-carb diet, reduces carbohydrate intake.
     */
    LOW_CARB,

    /**
     * Keto diet, high in fats and very low in carbohydrates.
     */
    KETO,

    /**
     * Paleo diet, focuses on whole, unprocessed foods similar to a hunter-gatherer diet.
     */
    PALEO,

    /**
     * Halal diet, follows Islamic dietary laws.
     */
    HALAL,

    /**
     * Kosher diet, follows Jewish dietary laws.
     */
    KOSHER,

    /**
     * Mediterranean diet, based on traditional eating habits of Mediterranean countries.
     */
    MEDITERRANEAN,

    /**
     * High-protein diet, focuses on increased protein intake.
     */
    HIGH_PROTEIN,

    /**
     * Low-fat diet, reduces total fat intake.
     */
    LOW_FAT,

    /**
     * Low-sodium diet, limits salt intake.
     */
    LOW_SODIUM
}

package balancebite.model.foodItem;

/**
 * Enum representing common food sources in the Netherlands.
 * Each source includes a display name and its official website URL.
 */
public enum FoodSource {

    /**
     * Albert Heijn supermarket.
     */
    ALBERT_HEIJN("Albert Heijn", "https://www.ah.nl"),

    /**
     * Jumbo supermarket.
     */
    JUMBO("Jumbo", "https://www.jumbo.com"),

    /**
     * Lidl supermarket.
     */
    LIDL("Lidl", "https://www.lidl.nl"),

    /**
     * Aldi supermarket.
     */
    ALDI("Aldi", "https://www.aldi.nl"),

    /**
     * Dirk van den Broek supermarket.
     */
    DIRK("Dirk van den Broek", "https://www.dirk.nl"),

    /**
     * Vomar supermarket.
     */
    VOMAR("Vomar", "https://www.vomar.nl"),

    /**
     * Holland & Barrett health store.
     */
    HOLLAND_BARETT("Holland & Barrett", "https://www.hollandandbarrett.nl"),

    /**
     * Plus supermarket.
     */
    PLUS("Plus", "https://www.plus.nl");

    private final String displayName;
    private final String website;

    /**
     * Constructor for initializing the enum values.
     *
     * @param displayName The name to be shown in the UI.
     * @param website     The official website URL for the source.
     */
    FoodSource(String displayName, String website) {
        this.displayName = displayName;
        this.website = website;
    }

    /**
     * Gets the display name of the food source.
     *
     * @return The human-readable name of the source.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the official website of the food source.
     *
     * @return The website URL as a String.
     */
    public String getWebsite() {
        return website;
    }
}

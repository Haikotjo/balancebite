package balancebite.model.userenums;

/**
 * Enum representing the gender of a user.
 * This is used to adjust nutritional or fitness calculations that might vary based on gender.
 *
 * Gender options include:
 * - MALE: Biological male
 * - FEMALE: Biological female
 * - OTHER: Non-binary or other genders
 */
public enum Gender {

    /**
     * Represents a biological male.
     */
    MALE,

    /**
     * Represents a biological female.
     */
    FEMALE,

    /**
     * Represents a non-binary or unspecified gender.
     * This option can be used when a user does not identify strictly as male or female.
     */
    OTHER
}

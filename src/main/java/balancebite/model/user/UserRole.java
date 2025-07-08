package balancebite.model.user;

/**
 * Enum representing the various roles a user can have in the system.
 */
public enum UserRole {
    /**
     * Represents a standard user role.
     */
    USER,

    /**
     * Represents a user with the chef role.
     */
    CHEF,

    /**
     * Represents a user with administrative privileges.
     */
    ADMIN,

    /**
     * Represents a user who is a dietitian.
     */
    DIETITIAN,

    /**
     * Represents a user who is a restaurant.
     */
    RESTAURANT,

    /**
     * Represents a user who is a supermarket.
     */
    SUPERMARKET
}

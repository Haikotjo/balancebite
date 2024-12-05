package balancebite.security;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a blacklisted token.
 * This class maps to the `token_blacklist` table in the database.
 */
@Entity
@Table(name = "token_blacklist")
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiry;

    /**
     * Gets the unique identifier for this blacklisted token.
     *
     * @return the ID of the blacklisted token.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this blacklisted token.
     *
     * @param id the ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the token value.
     *
     * @return the token.
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the token value.
     *
     * @param token the token to set.
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Gets the expiry date and time of the token.
     *
     * @return the expiry date and time.
     */
    public LocalDateTime getExpiry() {
        return expiry;
    }

    /**
     * Sets the expiry date and time of the token.
     *
     * @param expiry the expiry date and time to set.
     */
    public void setExpiry(LocalDateTime expiry) {
        this.expiry = expiry;
    }
}

package balancebite.dto.user;

import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.model.Role;

/**
 * Data Transfer Object (DTO) voor het creëren of updaten van een gebruiker.
 * Deze DTO wordt gebruikt om gebruikersgegevens van de client te ontvangen bij het aanmaken of updaten van een gebruiker.
 */
public class UserInputDTO {

    /**
     * De naam van de gebruiker.
     */
    private String name;

    /**
     * Het e-mailadres van de gebruiker.
     */
    private String email;

    /**
     * Het wachtwoord van de gebruiker. Dit moet gehashed worden voordat het in de database wordt opgeslagen.
     */
    private String password;

    /**
     * De rol van de gebruiker (bijvoorbeeld USER of ADMIN).
     */
    private Role role;

    /**
     * De aanbevolen dagelijkse inname van de gebruiker.
     * Dit vertegenwoordigt de gepersonaliseerde voedingsdoelen van de gebruiker,
     * zoals macronutriënten en micronutriënten die de gebruiker dagelijks moet consumeren.
     */
    private RecommendedDailyIntakeDTO recommendedDailyIntake;

    // Getters en setters

    /**
     * Haalt de naam van de gebruiker op.
     *
     * @return de naam van de gebruiker.
     */
    public String getName() {
        return name;
    }

    /**
     * Stelt de naam van de gebruiker in.
     *
     * @param name de naam om in te stellen.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Haalt het e-mailadres van de gebruiker op.
     *
     * @return het e-mailadres van de gebruiker.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Stelt het e-mailadres van de gebruiker in.
     *
     * @param email het e-mailadres om in te stellen.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Haalt het wachtwoord van de gebruiker op.
     *
     * @return het wachtwoord van de gebruiker.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Stelt het wachtwoord van de gebruiker in.
     *
     * @param password het wachtwoord om in te stellen.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Haalt de rol van de gebruiker op.
     *
     * @return de rol van de gebruiker.
     */
    public Role getRole() {
        return role;
    }

    /**
     * Stelt de rol van de gebruiker in.
     *
     * @param role de rol om in te stellen (bijvoorbeeld USER of ADMIN).
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Haalt de aanbevolen dagelijkse inname van de gebruiker op.
     *
     * @return de aanbevolen dagelijkse inname van de gebruiker.
     */
    public RecommendedDailyIntakeDTO getRecommendedDailyIntake() {
        return recommendedDailyIntake;
    }

    /**
     * Stelt de aanbevolen dagelijkse inname voor de gebruiker in.
     *
     * @param recommendedDailyIntake de aanbevolen dagelijkse inname om in te stellen.
     */
    public void setRecommendedDailyIntake(RecommendedDailyIntakeDTO recommendedDailyIntake) {
        this.recommendedDailyIntake = recommendedDailyIntake;
    }
}

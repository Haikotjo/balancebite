package balancebite.dto.recommendeddailyintake;

/**
 * Data Transfer Object (DTO) for input related to Recommended Daily Intake.
 * This DTO is used to transfer input data from the client that may affect the calculation of the recommended daily intake,
 * such as user-specific factors like weight and gender.
 */
public class RecommendedDailyIntakeInputDTO {

    /**
     * The weight of the user in kilograms.
     * This value is used to adjust the recommended daily intake of certain nutrients.
     */
    private Double weight;

    /**
     * The gender of the user, used to calculate gender-specific nutrient requirements.
     * Example values: "male", "female".
     */
    private String gender;

    /**
     * Default no-argument constructor for serialization/deserialization purposes.
     * Used by frameworks like Jackson to map incoming JSON data.
     */
    public RecommendedDailyIntakeInputDTO() {
        // Default constructor for frameworks
    }

    /**
     * Full constructor to create a RecommendedDailyIntakeInputDTO with weight and gender.
     *
     * @param weight The weight of the user in kilograms.
     * @param gender The gender of the user (e.g., "male", "female").
     */
    public RecommendedDailyIntakeInputDTO(Double weight, String gender) {
        this.weight = weight;
        this.gender = gender;
    }

    /**
     * Gets the weight of the user.
     *
     * @return The weight of the user in kilograms.
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Sets the weight of the user.
     *
     * @param weight The user's weight to set in kilograms.
     */
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     * Gets the gender of the user.
     *
     * @return The gender of the user.
     */
    public String getGender() {
        return gender;
    }

    /**
     * Sets the gender of the user.
     *
     * @param gender The user's gender to set.
     */
    public void setGender(String gender) {
        this.gender = gender;
    }
}

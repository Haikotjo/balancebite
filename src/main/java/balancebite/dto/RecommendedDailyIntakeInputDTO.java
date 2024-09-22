package balancebite.dto;

/**
 * Data Transfer Object for input related to Recommended Daily Intake.
 * This DTO is used to transfer input data from the client that may affect the recommended daily intake.
 */
public class RecommendedDailyIntakeInputDTO {

    private Double weight;  // User's weight in kg
    private String gender;  // User's gender (e.g., "male", "female")

    /**
     * Default constructor for serialization/deserialization purposes.
     */
    public RecommendedDailyIntakeInputDTO() {
    }

    /**
     * Constructor to create a RecommendedDailyIntakeInputDTO.
     *
     * @param weight The weight of the user in kilograms.
     * @param gender The gender of the user.
     */
    public RecommendedDailyIntakeInputDTO(Double weight, String gender) {
        this.weight = weight;
        this.gender = gender;
    }

    /**
     * Gets the weight of the user.
     *
     * @return The user's weight in kilograms.
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Sets the weight of the user.
     *
     * @param weight The weight to set in kilograms.
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
     * @param gender The gender to set.
     */
    public void setGender(String gender) {
        this.gender = gender;
    }
}

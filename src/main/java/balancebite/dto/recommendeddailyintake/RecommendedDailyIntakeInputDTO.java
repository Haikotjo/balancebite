//package balancebite.dto.recommendeddailyintake;
//
//import balancebite.model.Nutrient;
//
//import java.time.LocalDate;
//import java.util.List;
//
///**
// * Data Transfer Object (DTO) for input related to Recommended Daily Intake.
// * This DTO is used to transfer input data from the client that may affect the calculation of the recommended daily intake,
// * such as user-specific factors like weight, gender, and a list of specific nutrients.
// */
//public class RecommendedDailyIntakeInputDTO {
//
//    /**
//     * The weight of the user in kilograms.
//     * This value is used to adjust the recommended daily intake of certain nutrients.
//     */
//    private Double weight;
//
//    /**
//     * The gender of the user, used to calculate gender-specific nutrient requirements.
//     * Example values: "male", "female".
//     */
//    private String gender;
//
//    /**
//     * A list of specific nutrients that the user wants to include in the calculation.
//     * Each nutrient can be customized with a name and value.
//     */
//    private List<Nutrient> customNutrients;
//
//    /**
//     * The timestamp when the recommended daily intake was created.
//     */
//    private LocalDate createdAt;
//
//    /**
//     * Default no-argument constructor for serialization/deserialization purposes.
//     * Used by frameworks like Jackson to map incoming JSON data.
//     */
//    public RecommendedDailyIntakeInputDTO() {
//        // Default constructor for frameworks
//    }
//
//    /**
//     * Full constructor to create a RecommendedDailyIntakeInputDTO with weight, gender, custom nutrients, and creation timestamp.
//     *
//     * @param weight          The weight of the user in kilograms.
//     * @param gender          The gender of the user (e.g., "male", "female").
//     * @param customNutrients A list of custom nutrients specified by the user.
//     * @param createdAt       The timestamp when the recommended daily intake was created.
//     */
//    public RecommendedDailyIntakeInputDTO(Double weight, String gender, List<Nutrient> customNutrients, LocalDate createdAt) {
//        this.weight = weight;
//        this.gender = gender;
//        this.customNutrients = customNutrients;
//        this.createdAt = createdAt;
//    }
//
//    /**
//     * Gets the weight of the user.
//     *
//     * @return The weight of the user in kilograms.
//     */
//    public Double getWeight() {
//        return weight;
//    }
//
//    /**
//     * Sets the weight of the user.
//     *
//     * @param weight The user's weight to set in kilograms.
//     */
//    public void setWeight(Double weight) {
//        this.weight = weight;
//    }
//
//    /**
//     * Gets the gender of the user.
//     *
//     * @return The gender of the user.
//     */
//    public String getGender() {
//        return gender;
//    }
//
//    /**
//     * Sets the gender of the user.
//     *
//     * @param gender The user's gender to set.
//     */
//    public void setGender(String gender) {
//        this.gender = gender;
//    }
//
//    /**
//     * Gets the list of custom nutrients specified by the user.
//     *
//     * @return The list of custom nutrients.
//     */
//    public List<Nutrient> getCustomNutrients() {
//        return customNutrients;
//    }
//
//    /**
//     * Sets the list of custom nutrients specified by the user.
//     *
//     * @param customNutrients The list of custom nutrients to set.
//     */
//    public void setCustomNutrients(List<Nutrient> customNutrients) {
//        this.customNutrients = customNutrients;
//    }
//
//    /**
//     * Gets the timestamp when the recommended daily intake was created.
//     *
//     * @return The creation timestamp.
//     */
//    public LocalDate getCreatedAt() {
//        return createdAt;
//    }
//
//    /**
//     * Sets the timestamp when the recommended daily intake was created.
//     *
//     * @param createdAt The timestamp to set.
//     */
//    public void setCreatedAt(LocalDate createdAt) {
//        this.createdAt = createdAt;
//    }
//}

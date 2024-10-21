//package balancebite.dto.user;
//
//import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
//import balancebite.model.Role;
//import balancebite.model.userenums.ActivityLevel;
//import balancebite.model.userenums.Gender;
//import balancebite.model.userenums.Goal;
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
//
//import java.util.List;
//
///**
// * Data Transfer Object (DTO) for creating or updating a user.
// * This DTO is used to receive user data from the client during user creation or update.
// */
//public class UserInputDTO {
//
//    @NotBlank(message = "The user name cannot be blank. Please provide a valid name.")
//    @Size(min = 2, max = 50, message = "User name must be between 2 and 50 characters.")
//    private String userName;
//
//    @NotBlank(message = "The email cannot be blank. Please provide a valid email address.")
//    @Email(message = "Please provide a valid email address.")
//    private String email;
//
//    @NotBlank(message = "The password cannot be blank. Please provide a valid password.")
//    @Size(min = 4, message = "Password must be at least 4 characters long.")
//    private String password;
//
//    @NotNull(message = "Weight must be provided.")
//    private Double weight;
//
//    @NotNull(message = "Age must be provided.")
//    private Integer age;
//
//    @NotNull(message = "Height must be provided.")
//    private Double height;
//
//    @NotNull(message = "Gender must be provided.")
//    private Gender gender;
//
//    @NotNull(message = "Activity level must be provided.")
//    private ActivityLevel activityLevel;
//
//    @NotNull(message = "Goal must be provided.")
//    private Goal goal;
//
//    @NotNull(message = "Role must be provided.")
//    private Role role;
//
//    // Default constructor
//    public UserInputDTO() {}
//
//    // Constructor for creating user with only basic information
//    public UserInputDTO(String userName, String email, String password, Role role) {
//        this.userName = userName;
//        this.email = email;
//        this.password = password;
//        this.role = role;
//    }
//
//    // Full constructor
//    public UserInputDTO(String userName, String email, String password, Double weight, Integer age, Double height,
//                        Gender gender, ActivityLevel activityLevel, Goal goal, Role role) {
//        this.userName = userName;
//        this.email = email;
//        this.password = password;
//        this.weight = weight;
//        this.age = age;
//        this.height = height;
//        this.gender = gender;
//        this.activityLevel = activityLevel;
//        this.goal = goal;
//        this.role = role;
//    }
//
//    // Getters and setters
//
//    public String getUserName() {
//        return userName;
//    }
//
//    public void setUserName(String userName) {
//        this.userName = userName;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public Double getWeight() {
//        return weight;
//    }
//
//    public void setWeight(Double weight) {
//        this.weight = weight;
//    }
//
//    public Integer getAge() {
//        return age;
//    }
//
//    public void setAge(Integer age) {
//        this.age = age;
//    }
//
//    public Double getHeight() {
//        return height;
//    }
//
//    public void setHeight(Double height) {
//        this.height = height;
//    }
//
//    public Gender getGender() {
//        return gender;
//    }
//
//    public void setGender(Gender gender) {
//        this.gender = gender;
//    }
//
//    public ActivityLevel getActivityLevel() {
//        return activityLevel;
//    }
//
//    public void setActivityLevel(ActivityLevel activityLevel) {
//        this.activityLevel = activityLevel;
//    }
//
//    public Goal getGoal() {
//        return goal;
//    }
//
//    public void setGoal(Goal goal) {
//        this.goal = goal;
//    }
//
//    public Role getRole() {
//        return role;
//    }
//
//    public void setRole(Role role) {
//        this.role = role;
//    }
//}

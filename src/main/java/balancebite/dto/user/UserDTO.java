package balancebite.dto.user;

import balancebite.dto.meal.MealDTO;
import balancebite.dto.recommendeddailyintake.RecommendedDailyIntakeDTO;
import balancebite.model.user.Role;
import balancebite.model.user.userenums.ActivityLevel;
import balancebite.model.user.userenums.Gender;
import balancebite.model.user.userenums.Goal;
import java.util.Collection;
import java.util.List;

public class UserDTO {

    private final Long id;
    private final String userName;
    private final String email;
    private final Double weight;
    private final Integer age;
    private final Double height;
    private final List<WeightEntryDTO> weightHistory;
    private final Double targetWeight;
    private final Gender gender;
    private final ActivityLevel activityLevel;
    private final Goal goal;
    private final List<MealDTO> meals;
    private final Collection<Role> roles;
    private final List<RecommendedDailyIntakeDTO> recommendedDailyIntakes;
    private final RecommendedDailyIntakeDTO baseRecommendedDailyIntake;
    private final List<UserDTO> clients;
    private final List<UserDTO> dietitians;


    public UserDTO(Long id, String userName, String email, Double weight,List<WeightEntryDTO> weightHistory,
                   Double targetWeight, Integer age, Double height, Gender gender,
                   ActivityLevel activityLevel, Goal goal, List<MealDTO> meals, Collection<Role> roles,
                   List<RecommendedDailyIntakeDTO> recommendedDailyIntakes, RecommendedDailyIntakeDTO baseRecommendedDailyIntake, List<UserDTO> clients, List<UserDTO> dietitians) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.weight = weight;
        this.weightHistory = (weightHistory != null) ? List.copyOf(weightHistory) : List.of();
        this.targetWeight = targetWeight;
        this.age = age;
        this.height = height;
        this.gender = gender;
        this.activityLevel = activityLevel;
        this.goal = goal;
        this.meals = (meals != null) ? List.copyOf(meals) : List.of();
        this.roles = (roles != null) ? List.copyOf(roles) : List.of();
        this.recommendedDailyIntakes = (recommendedDailyIntakes != null) ? List.copyOf(recommendedDailyIntakes) : List.of();
        this.baseRecommendedDailyIntake = baseRecommendedDailyIntake;
        this.clients = (clients != null) ? List.copyOf(clients) : List.of();
        this.dietitians = (dietitians != null) ? List.copyOf(dietitians) : List.of();
    }

    public UserDTO(Long id, String userName, String email) {
        this(id, userName, email, null, null, null,null ,null, null, null, null, List.of(), List.of(), List.of(), null,   List.of(), List.of());
    }

    public UserDTO(Long id, String userName) {
        this(id, userName, null, null, null, null, null, null, null, null, null, List.of(), List.of(), List.of(), null,    List.of(), List.of());
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getTargetWeight() {
        return targetWeight;
    }

    public List<WeightEntryDTO> getWeightHistory() {
        return weightHistory;
    }

    public Integer getAge() {
        return age;
    }

    public Double getHeight() {
        return height;
    }

    public Gender getGender() {
        return gender;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public Goal getGoal() {
        return goal;
    }

    public List<MealDTO> getMeals() {
        return meals;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public List<RecommendedDailyIntakeDTO> getRecommendedDailyIntakes() {
        return recommendedDailyIntakes;
    }

    public RecommendedDailyIntakeDTO getBaseRecommendedDailyIntake() {
        return baseRecommendedDailyIntake;
    }

    public List<UserDTO> getClients() {
        return clients;
    }

    public List<UserDTO> getDietitians() {
        return dietitians;
    }
}

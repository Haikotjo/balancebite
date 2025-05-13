package balancebite.model.user;

import balancebite.model.diet.DietPlan;
import balancebite.model.meal.Meal;
import balancebite.model.RecommendedDailyIntake;
import balancebite.model.user.userenums.ActivityLevel;
import balancebite.model.user.userenums.Gender;
import balancebite.model.user.userenums.Goal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private Double weight;
    private Integer age;
    private Double height;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    private Goal goal;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_meals",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "meal_id")
    )
    @JsonIgnore
    private Set<Meal> meals = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_rolename")
    )
    private Set<Role> roles;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<RecommendedDailyIntake> recommendedDailyIntakes = new HashSet<>();

    /**
     * The base recommended daily intake for the user.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "base_rdi_id", referencedColumnName = "id")
    private RecommendedDailyIntake baseRecommendedDailyIntake;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DietPlan> dietPlans = new ArrayList<>();

    public User() {}

    public User(String userName, String email, String password, Set<Role> roles) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public Set<Meal> getMeals() {
        return meals;
    }

    public void setMeals(Set<Meal> meals) {
        this.meals = meals != null ? meals : new HashSet<>();
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<RecommendedDailyIntake> getRecommendedDailyIntakes() {
        return recommendedDailyIntakes;
    }

    public void setRecommendedDailyIntakes(Set<RecommendedDailyIntake> recommendedDailyIntakes) {
        this.recommendedDailyIntakes = recommendedDailyIntakes != null ? recommendedDailyIntakes : new HashSet<>();
    }

    public RecommendedDailyIntake getBaseRecommendedDailyIntake() {
        return baseRecommendedDailyIntake;
    }

    public void setBaseRecommendedDailyIntake(RecommendedDailyIntake baseRecommendedDailyIntake) {
        this.baseRecommendedDailyIntake = baseRecommendedDailyIntake;
    }

    public List<DietPlan> getDietPlans() {
        return dietPlans;
    }

    public void setDiets(List<DietPlan> dietPlans) {
        this.dietPlans = dietPlans;
    }
}

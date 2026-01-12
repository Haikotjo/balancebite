package balancebite.model.user;

import balancebite.model.diet.DietPlan;
import balancebite.model.meal.Meal;
import balancebite.model.RecommendedDailyIntake;
import balancebite.model.user.userenums.ActivityLevel;
import balancebite.model.user.userenums.Gender;
import balancebite.model.user.userenums.Goal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;


    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private Double weight;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("date DESC")
    private List<WeightEntry> weightHistory = new ArrayList<>();

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

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.PERSIST) // of gewoon geen cascade
    private List<DietPlan> dietPlans = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_saved_diet_plans",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "diet_plan_id")
    )
    private Set<DietPlan> savedDietPlans = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_saved_meals",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "meal_id")
    )
    private Set<Meal> savedMeals = new HashSet<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "dietitian_clients",
            joinColumns = @JoinColumn(name = "dietitian_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private Set<User> clients = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "clients")
    private Set<User> dietitians = new HashSet<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "user_following",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "followed_id")
    )
    private Set<User> following = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "following")
    private Set<User> followers = new HashSet<>();

    @Column(length = 1000)
    private String description;

    @Column
    private String image; // Base64-encoded image

    @Column
    private String imageUrl; // URL naar afbeelding op server of CDN

    @Column(length = 255)
    private String phone;

    @Column(length = 500)
    private String address;

    @Column(length = 255)
    private String website;

    @Column(length = 255)
    private String instagram;

    @Column(length = 255)
    private String facebook;

    @Column(length = 255)
    private String linkedin;



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

    public List<WeightEntry> getWeightHistory() {
        return weightHistory;
    }

    public void setWeightHistory(List<WeightEntry> weightHistory) {
        this.weightHistory = weightHistory;
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

    public void setDietPlans(List<DietPlan> dietPlans) {
        this.dietPlans = dietPlans;
    }

    public Set<DietPlan> getSavedDietPlans() {
        return savedDietPlans;
    }

    public void setSavedDietPlans(Set<DietPlan> savedDietPlans) {
        this.savedDietPlans = savedDietPlans != null ? savedDietPlans : new HashSet<>();
    }

    public Set<Meal> getSavedMeals() {
        return savedMeals;
    }

    public void setSavedMeals(Set<Meal> savedMeals) {
        this.savedMeals = savedMeals;
    }

    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }


    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public Set<User> getClients() {
        return clients;
    }

    public void setClients(Set<User> clients) {
        this.clients = clients;
    }

    public Set<User> getDietitians() {
        return dietitians;
    }

    public void setDietitians(Set<User> dietitians) {
        this.dietitians = dietitians;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getInstagram() { return instagram; }
    public void setInstagram(String instagram) { this.instagram = instagram; }

    public String getFacebook() { return facebook; }
    public void setFacebook(String facebook) { this.facebook = facebook; }

    public String getLinkedin() { return linkedin; }
    public void setLinkedin(String linkedin) { this.linkedin = linkedin; }


    public void addWeight(Double weightValue) {
        this.weight = weightValue; // Update altijd het actuele gewicht veld
        LocalDate today = LocalDate.now();

        // Check of er vandaag al een entry bestaat in de lijst
        Optional<WeightEntry> existingEntry = this.weightHistory.stream()
                .filter(entry -> entry.getDate().equals(today))
                .findFirst();

        if (existingEntry.isPresent()) {
            // Er is al een meting vandaag: update alleen de waarde
            existingEntry.get().setWeight(weightValue);
        } else {
            // Er is nog geen meting vandaag: voeg een nieuwe toe aan de lijst
            WeightEntry newEntry = new WeightEntry(weightValue, today, this);
            this.weightHistory.add(newEntry);
        }
    }
}

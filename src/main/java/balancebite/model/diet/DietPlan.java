package balancebite.model.diet;

import balancebite.model.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Entity
public class DietPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long originalDietId;

    private boolean isTemplate = true;

    @Column(nullable = true)
    private boolean isPrivate = false;

    @Column(name = "isRestricted")
    private boolean isRestricted = false;

    @Column(length = 1000)
    private String dietDescription;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adjusted_by_user_id")
    private User adjustedBy;

    @OneToMany(mappedBy = "diet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DietDay> dietDays = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "dietplan_diets", joinColumns = @JoinColumn(name = "dietplan_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "diet")
    private Set<balancebite.model.meal.references.Diet> diets = new HashSet<>();

    @ManyToMany(mappedBy = "savedDietPlans")
    private Set<User> users = new HashSet<>();

    private Double totalProtein;
    private Double totalCarbs;
    private Double totalFat;
    private Double totalCalories;
    private Double totalSaturatedFat;
    private Double totalUnsaturatedFat;
    private Double totalSugars;
    private Double avgProtein;
    private Double avgCarbs;
    private Double avgFat;
    private Double avgCalories;
    private Double avgSaturatedFat;
    private Double avgUnsaturatedFat;
    private Double avgSugars;
    @Column(name = "save_count")
    private Long saveCount = 0L;

    @Column(name = "weekly_save_count")
    private Long weeklySaveCount = 0L;

    @Column(name = "monthly_save_count")
    private Long monthlySaveCount = 0L;

    // Constructors
    public DietPlan() {}

    public DietPlan(String name, User createdBy) {
        this.name = name;
        this.createdBy = createdBy;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOriginalDietId() {
        return originalDietId;
    }

    public void setOriginalDietId(Long originalDietId) {
        this.originalDietId = originalDietId;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(boolean template) {
        isTemplate = template;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public boolean isRestricted() {
        return isRestricted;
    }

    public void setRestricted(boolean restricted) {
        isRestricted = restricted;
    }

    public String getDietDescription() {
        return dietDescription;
    }

    public void setDietDescription(String dietDescription) {
        this.dietDescription = dietDescription;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getAdjustedBy() {
        return adjustedBy;
    }

    public void setAdjustedBy(User adjustedBy) {
        this.adjustedBy = adjustedBy;
    }

    public List<DietDay> getDietDays() {
        return dietDays;
    }

    public void setDietDays(List<DietDay> dietDays) {
        this.dietDays = dietDays;
    }

    public Set<balancebite.model.meal.references.Diet> getDiets() {
        return diets;
    }

    public void setDiets(Set<balancebite.model.meal.references.Diet> diets) {
        this.diets = diets;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users != null ? users : new HashSet<>();
    }

    public Double getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(Double totalProtein) {
        this.totalProtein = totalProtein;
    }

    public Double getTotalCarbs() {
        return totalCarbs;
    }

    public void setTotalCarbs(Double totalCarbs) {
        this.totalCarbs = totalCarbs;
    }

    public Double getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(Double totalFat) {
        this.totalFat = totalFat;
    }

    public Double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(Double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public Double getTotalSaturatedFat() {
        return totalSaturatedFat;
    }

    public void setTotalSaturatedFat(Double totalSaturatedFat) {
        this.totalSaturatedFat = totalSaturatedFat;
    }

    public Double getTotalUnsaturatedFat() {
        return totalUnsaturatedFat;
    }

    public void setTotalUnsaturatedFat(Double totalUnsaturatedFat) {
        this.totalUnsaturatedFat = totalUnsaturatedFat;
    }

    public Double getTotalSugars() {
        return totalSugars;
    }

    public void setTotalSugars(Double totalSugars) {
        this.totalSugars = totalSugars;
    }


    public Double getAvgProtein() {
        return avgProtein;
    }

    public void setAvgProtein(Double avgProtein) {
        this.avgProtein = avgProtein;
    }

    public Double getAvgCarbs() {
        return avgCarbs;
    }

    public void setAvgCarbs(Double avgCarbs) {
        this.avgCarbs = avgCarbs;
    }

    public Double getAvgFat() {
        return avgFat;
    }

    public void setAvgFat(Double avgFat) {
        this.avgFat = avgFat;
    }

    public Double getAvgCalories() {
        return avgCalories;
    }

    public void setAvgCalories(Double avgCalories) {
        this.avgCalories = avgCalories;
    }

    public Double getAvgSaturatedFat() {
        return avgSaturatedFat;
    }

    public void setAvgSaturatedFat(Double avgSaturatedFat) {
        this.avgSaturatedFat = avgSaturatedFat;
    }

    public Double getAvgUnsaturatedFat() {
        return avgUnsaturatedFat;
    }

    public void setAvgUnsaturatedFat(Double avgUnsaturatedFat) {
        this.avgUnsaturatedFat = avgUnsaturatedFat;
    }

    public Double getAvgSugars() {
        return avgSugars;
    }

    public void setAvgSugars(Double avgSugars) {
        this.avgSugars = avgSugars;
    }

    public Long getSaveCount() {
        return saveCount;
    }

    public void setSaveCount(Long saveCount) {
        this.saveCount = saveCount;
    }

    public Long getWeeklySaveCount() {
        return weeklySaveCount;
    }

    public void setWeeklySaveCount(Long weeklySaveCount) {
        this.weeklySaveCount = weeklySaveCount;
    }

    public Long getMonthlySaveCount() {
        return monthlySaveCount;
    }

    public void setMonthlySaveCount(Long monthlySaveCount) {
        this.monthlySaveCount = monthlySaveCount;
    }
}

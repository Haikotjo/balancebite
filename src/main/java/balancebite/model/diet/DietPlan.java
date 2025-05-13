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
}

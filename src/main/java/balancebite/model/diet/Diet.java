package balancebite.model.diet;

import balancebite.model.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Diet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long originalDietId;

    private boolean isTemplate = true;

    private LocalDateTime version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = true)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adjusted_by_user_id", nullable = true)
    private User adjustedBy;

    @OneToMany(mappedBy = "diet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DietDay> dietDays = new ArrayList<>();

    // Constructors
    public Diet() {}

    public Diet(String name, User createdBy) {
        this.name = name;
        this.createdBy = createdBy;
        this.version = LocalDateTime.now();
    }

    // Getters & setters
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

    public LocalDateTime getVersion() {
        return version;
    }

    public void setVersion(LocalDateTime version) {
        this.version = version;
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
}

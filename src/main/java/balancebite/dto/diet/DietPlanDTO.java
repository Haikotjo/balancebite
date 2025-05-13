package balancebite.dto.diet;

import balancebite.dto.user.PublicUserDTO;
import balancebite.dto.user.UserDTO;
import balancebite.model.meal.references.Diet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class DietPlanDTO {

    private final Long id;
    private final String name;
    private final Long originalDietId;
    private final boolean isTemplate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final PublicUserDTO createdBy;
    private final PublicUserDTO adjustedBy;

    private final List<DietDayDTO> dietDays;
    private final String dietDescription;
    private final Set<Diet> diets;

    public DietPlanDTO(Long id,
                       String name,
                       Long originalDietId,
                       boolean isTemplate,
                       LocalDateTime createdAt,
                       LocalDateTime updatedAt,
                       PublicUserDTO createdBy,
                       PublicUserDTO adjustedBy,
                       List<DietDayDTO> dietDays,
                       String dietDescription,
                       Set<Diet> diets) {
        this.id = id;
        this.name = name;
        this.originalDietId = originalDietId;
        this.isTemplate = isTemplate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.adjustedBy = adjustedBy;
        this.dietDays = (dietDays != null) ? List.copyOf(dietDays) : List.of();
        this.dietDescription = dietDescription;
        this.diets = diets;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getOriginalDietId() {
        return originalDietId;
    }

    public boolean isTemplate() {
        return isTemplate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public PublicUserDTO getCreatedBy() {
        return createdBy;
    }

    public PublicUserDTO getAdjustedBy() {
        return adjustedBy;
    }

    public List<DietDayDTO> getDietDays() {
        return dietDays;
    }

    public String getDietDescription() {
        return dietDescription;
    }

    public Set<Diet> getDiets() {
        return diets;
    }
}

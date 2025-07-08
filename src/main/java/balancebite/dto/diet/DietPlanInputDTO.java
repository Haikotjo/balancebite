package balancebite.dto.diet;

import balancebite.model.meal.references.Diet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public class DietPlanInputDTO {

    @NotBlank(message = "Diet plan name is required.")
    @Size(max = 100, message = "Diet plan name must not exceed 100 characters.")
    private String name;

    private Long originalDietId;

    private Long createdByUserId;

    private Long adjustedByUserId;

    // Optioneel: alleen gebruiken als je vanuit frontend wilt bepalen of het een template is
    private boolean isTemplate = true;

    private boolean isPrivate = false;

    private boolean isRestricted;

    @NotEmpty(message = "At least one diet day is required.")
    private List<DietDayInputDTO> dietDays;

    @Size(max = 1000, message = "Diet description must not exceed 1000 characters.")
    private String dietDescription;

    private Set<Diet> diets;

    public DietPlanInputDTO() {}

    public DietPlanInputDTO(String name, Long originalDietId, Long createdByUserId, Long adjustedByUserId,
                            boolean isTemplate, boolean isPrivate, boolean isRestricted, List<DietDayInputDTO> dietDays,
                            String dietDescription, Set<Diet> diets) {
        this.name = name;
        this.originalDietId = originalDietId;
        this.createdByUserId = createdByUserId;
        this.adjustedByUserId = adjustedByUserId;
        this.isTemplate = isTemplate;
        this.isPrivate = isPrivate;
        this.isRestricted = isRestricted;
        this.dietDays = dietDays;
        this.dietDescription = dietDescription;
        this.diets = diets;
    }

    // Getters & Setters
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

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public Long getAdjustedByUserId() {
        return adjustedByUserId;
    }

    public void setAdjustedByUserId(Long adjustedByUserId) {
        this.adjustedByUserId = adjustedByUserId;
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

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public boolean isRestricted() {
        return isRestricted;
    }

    public void setRestricted(boolean restricted) {
        isRestricted = restricted;
    }

    public List<DietDayInputDTO> getDietDays() {
        return dietDays;
    }

    public void setDietDays(List<DietDayInputDTO> dietDays) {
        this.dietDays = dietDays;
    }

    public String getDietDescription() {
        return dietDescription;
    }

    public void setDietDescription(String dietDescription) {
        this.dietDescription = dietDescription;
    }

    public Set<Diet> getDiets() {
        return diets;
    }

    public void setDiets(Set<Diet> diets) {
        this.diets = diets;
    }
}

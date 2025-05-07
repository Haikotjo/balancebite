package balancebite.dto.diet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public class DietInputDTO {

    @NotBlank(message = "Diet name is required.")
    @Size(max = 100, message = "Diet name must not exceed 100 characters.")
    private String name;

    private Long originalDietId;

    private Long createdByUserId;

    private Long adjustedByUserId;

    private boolean isTemplate = true;

    @NotEmpty(message = "At least one diet day is required.")
    private List<DietDayInputDTO> dietDays;

    public DietInputDTO() {}

    public DietInputDTO(String name, Long originalDietId, Long createdByUserId, Long adjustedByUserId, boolean isTemplate, List<DietDayInputDTO> dietDays) {
        this.name = name;
        this.originalDietId = originalDietId;
        this.createdByUserId = createdByUserId;
        this.adjustedByUserId = adjustedByUserId;
        this.isTemplate = isTemplate;
        this.dietDays = dietDays;
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

    public List<DietDayInputDTO> getDietDays() {
        return dietDays;
    }

    public void setDietDays(List<DietDayInputDTO> dietDays) {
        this.dietDays = dietDays;
    }
}

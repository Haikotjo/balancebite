package balancebite.dto.diet;

import balancebite.dto.user.UserDTO;

import java.time.LocalDateTime;
import java.util.List;

public class DietDTO {

    private final Long id;
    private final String name;
    private final Long originalDietId;
    private final boolean isTemplate;
    private final LocalDateTime version;
    private final UserDTO createdBy;
    private final UserDTO adjustedBy;
    private final List<DietDayDTO> dietDays;

    public DietDTO(Long id, String name, Long originalDietId, boolean isTemplate,
                   LocalDateTime version, UserDTO createdBy, UserDTO adjustedBy, List<DietDayDTO> dietDays) {
        this.id = id;
        this.name = name;
        this.originalDietId = originalDietId;
        this.isTemplate = isTemplate;
        this.version = version;
        this.createdBy = createdBy;
        this.adjustedBy = adjustedBy;
        this.dietDays = (dietDays != null) ? List.copyOf(dietDays) : List.of();
    }

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

    public LocalDateTime getVersion() {
        return version;
    }

    public UserDTO getCreatedBy() {
        return createdBy;
    }

    public UserDTO getAdjustedBy() {
        return adjustedBy;
    }

    public List<DietDayDTO> getDietDays() {
        return List.copyOf(dietDays);
    }
}

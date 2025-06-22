package balancebite.dto.stickyitem;

import balancebite.model.stickyitem.StickyType;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating a StickyItem.
 * Used as input from admin requests.
 */
public class StickyItemInputDTO {

    @NotNull
    private StickyType type;

    @NotNull
    private Long referenceId;

    // Constructors
    public StickyItemInputDTO() {}

    public StickyItemInputDTO(StickyType type, Long referenceId) {
        this.type = type;
        this.referenceId = referenceId;
    }

    // Getters and Setters
    public StickyType getType() {
        return type;
    }

    public void setType(StickyType type) {
        this.type = type;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }
}

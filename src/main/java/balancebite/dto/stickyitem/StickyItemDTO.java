package balancebite.dto.stickyitem;

import balancebite.model.stickyitem.StickyType;

import java.time.LocalDateTime;

/**
 * DTO for returning StickyItem data.
 */
public class StickyItemDTO {

    private Long id;
    private StickyType type;
    private Long referenceId;
    private LocalDateTime pinnedAt;
    private Long pinnedByUserId;
    private String pinnedByEmail;

    // Constructors
    public StickyItemDTO() {}

    public StickyItemDTO(Long id, StickyType type, Long referenceId, LocalDateTime pinnedAt, Long pinnedByUserId, String pinnedByEmail) {
        this.id = id;
        this.type = type;
        this.referenceId = referenceId;
        this.pinnedAt = pinnedAt;
        this.pinnedByUserId = pinnedByUserId;
        this.pinnedByEmail = pinnedByEmail;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public LocalDateTime getPinnedAt() {
        return pinnedAt;
    }

    public void setPinnedAt(LocalDateTime pinnedAt) {
        this.pinnedAt = pinnedAt;
    }

    public Long getPinnedByUserId() {
        return pinnedByUserId;
    }

    public void setPinnedByUserId(Long pinnedByUserId) {
        this.pinnedByUserId = pinnedByUserId;
    }

    public String getPinnedByEmail() {
        return pinnedByEmail;
    }

    public void setPinnedByEmail(String pinnedByEmail) {
        this.pinnedByEmail = pinnedByEmail;
    }
}

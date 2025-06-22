package balancebite.model.stickyitem;

import balancebite.model.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity class representing a sticky (pinned) item.
 * Used to highlight a Meal or DietPlan at the top of a list.
 * Only admins can create sticky items.
 */
@Entity
@Table(name = "sticky_items")
public class StickyItem {

    /**
     * Unique identifier for the sticky item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The time when the item was pinned.
     */
    @CreationTimestamp
    private LocalDateTime pinnedAt;

    /**
     * Type of the pinned item (MEAL or DIET_PLAN).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StickyType type;

    /**
     * ID of the referenced Meal or DietPlan.
     */
    @Column(nullable = false)
    private Long referenceId;

    /**
     * The admin user who pinned the item.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pinned_by_user_id")
    private User pinnedBy;

    /**
     * No-args constructor required by JPA.
     */
    public StickyItem() {}

    /**
     * Constructor with fields.
     */
    public StickyItem(StickyType type, Long referenceId, User pinnedBy) {
        this.type = type;
        this.referenceId = referenceId;
        this.pinnedBy = pinnedBy;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public LocalDateTime getPinnedAt() {
        return pinnedAt;
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

    public User getPinnedBy() {
        return pinnedBy;
    }

    public void setPinnedBy(User pinnedBy) {
        this.pinnedBy = pinnedBy;
    }
}

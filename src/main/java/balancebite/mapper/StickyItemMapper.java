package balancebite.mapper;

import balancebite.dto.stickyitem.StickyItemDTO;
import balancebite.dto.stickyitem.StickyItemInputDTO;
import balancebite.model.stickyitem.StickyItem;
import balancebite.model.user.User;
import org.springframework.stereotype.Component;

/**
 * Mapper class to convert between StickyItem entities and DTOs.
 */
@Component
public class StickyItemMapper {

    /**
     * Maps input DTO to StickyItem entity.
     */
    public StickyItem toEntity(StickyItemInputDTO dto, User pinnedBy) {
        return new StickyItem(dto.getType(), dto.getReferenceId(), pinnedBy);
    }

    /**
     * Maps StickyItem entity to output DTO.
     */
    public StickyItemDTO toDTO(StickyItem entity) {
        return new StickyItemDTO(
                entity.getId(),
                entity.getType(),
                entity.getReferenceId(),
                entity.getPinnedAt(),
                entity.getPinnedBy() != null ? entity.getPinnedBy().getId() : null,
                entity.getPinnedBy() != null ? entity.getPinnedBy().getEmail() : null
        );
    }
}

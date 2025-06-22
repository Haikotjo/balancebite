package balancebite.service.interfaces;

import balancebite.dto.stickyitem.StickyItemDTO;
import balancebite.dto.stickyitem.StickyItemInputDTO;
import balancebite.model.stickyitem.StickyType;
import balancebite.model.user.User;

import java.util.List;

public interface IStickyItemService {

    StickyItemDTO createStickyItem(StickyItemInputDTO inputDTO, User admin);

    List<StickyItemDTO> getAllByType(StickyType type);

    List<StickyItemDTO> getLatest(int limit);

    List<StickyItemDTO> getAll();
}

package balancebite.repository;

import balancebite.model.stickyitem.StickyItem;
import balancebite.model.stickyitem.StickyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StickyItemRepository extends JpaRepository<StickyItem, Long> {

    Optional<StickyItem> findByTypeAndReferenceId(StickyType type, Long referenceId);

    List<StickyItem> findAllByTypeOrderByPinnedAtDesc(StickyType type);

    List<StickyItem> findAllByOrderByPinnedAtDesc();
}

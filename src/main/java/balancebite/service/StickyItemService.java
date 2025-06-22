package balancebite.service;

import balancebite.dto.stickyitem.StickyItemDTO;
import balancebite.dto.stickyitem.StickyItemInputDTO;
import balancebite.mapper.StickyItemMapper;
import balancebite.model.stickyitem.StickyItem;
import balancebite.model.stickyitem.StickyType;
import balancebite.model.user.User;
import balancebite.repository.StickyItemRepository;
import balancebite.service.interfaces.IStickyItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StickyItemService implements IStickyItemService {

    private static final Logger log = LoggerFactory.getLogger(StickyItemService.class);
    private static final int MAX_ITEMS_PER_TYPE = 20;

    private final StickyItemRepository repository;
    private final StickyItemMapper mapper;

    public StickyItemService(StickyItemRepository repository, StickyItemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Creates a new sticky item. If max per type is reached, deletes the oldest.
     * If the same item is already sticky, it will be replaced with a new pinnedAt timestamp.
     */
    @Override
    @Transactional
    public StickyItemDTO createStickyItem(StickyItemInputDTO inputDTO, User admin) {
        StickyType type = inputDTO.getType();
        Long refId = inputDTO.getReferenceId();

        // Check and remove existing sticky for same type + refId (prevent duplicates)
        repository.findByTypeAndReferenceId(type, refId).ifPresent(existing -> {
            log.info("Sticky already exists for type={}, referenceId={}. Removing old sticky with ID={}", type, refId, existing.getId());
            repository.delete(existing);
        });

        // Enforce max 20 per type
        List<StickyItem> existing = repository.findAllByTypeOrderByPinnedAtDesc(type);
        if (existing.size() >= MAX_ITEMS_PER_TYPE) {
            StickyItem oldest = existing.get(existing.size() - 1);
            log.info("Sticky limit reached ({}). Removing oldest sticky with ID={}", MAX_ITEMS_PER_TYPE, oldest.getId());
            repository.delete(oldest);
        }

        // Create new sticky
        StickyItem entity = mapper.toEntity(inputDTO, admin);
        StickyItem saved = repository.save(entity);

        log.info("New sticky created by admin {} for type={}, referenceId={}", admin.getEmail(), type, refId);
        return mapper.toDTO(saved);
    }

    /**
     * Returns all sticky items of a given type, sorted by newest first.
     */
    @Override
    @Transactional(readOnly = true)
    public List<StickyItemDTO> getAllByType(StickyType type) {
        List<StickyItem> items = repository.findAllByTypeOrderByPinnedAtDesc(type);
        log.debug("Fetched {} sticky items for type={}", items.size(), type);
        return items.stream().map(mapper::toDTO).toList();
    }

    /**
     * Returns latest sticky items across all types, limited by given count.
     */
    @Override
    @Transactional(readOnly = true)
    public List<StickyItemDTO> getLatest(int limit) {
        List<StickyItem> items = repository.findAllByOrderByPinnedAtDesc()
                .stream()
                .limit(limit)
                .toList();
        log.debug("Fetched latest {} sticky items across all types", items.size());
        return items.stream().map(mapper::toDTO).toList();
    }

    /**
     * Returns all sticky items across all types, sorted by newest first.
     */
    @Override
    @Transactional(readOnly = true)
    public List<StickyItemDTO> getAll() {
        List<StickyItem> items = repository.findAllByOrderByPinnedAtDesc();
        log.debug("Fetched {} sticky items (no type filter)", items.size());
        return items.stream().map(mapper::toDTO).toList();
    }

}

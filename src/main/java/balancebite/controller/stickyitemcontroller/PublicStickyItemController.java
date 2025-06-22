package balancebite.controller.stickyitemcontroller;

import balancebite.dto.stickyitem.StickyItemDTO;
import balancebite.model.stickyitem.StickyType;
import balancebite.service.interfaces.IStickyItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sticky-items")
public class PublicStickyItemController {

    private static final Logger log = LoggerFactory.getLogger(PublicStickyItemController.class);
    private final IStickyItemService stickyItemService;

    public PublicStickyItemController(IStickyItemService stickyItemService) {
        this.stickyItemService = stickyItemService;
    }

    /**
     * Returns all sticky items, optionally filtered by type.
     */
    @GetMapping
    public ResponseEntity<?> getStickyItems(@RequestParam(required = false) StickyType type) {
        try {
            List<StickyItemDTO> items = (type != null)
                    ? stickyItemService.getAllByType(type)
                    : stickyItemService.getAll();

            if (items.isEmpty()) return ResponseEntity.noContent().build();
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            log.error("Unexpected error while fetching sticky items: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Returns latest sticky items, across all types, limited by count.
     */
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestStickyItems(@RequestParam(defaultValue = "5") int limit) {
        try {
            List<StickyItemDTO> items = stickyItemService.getLatest(limit);
            if (items.isEmpty()) return ResponseEntity.noContent().build();
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            log.error("Unexpected error while fetching latest sticky items: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllStickyItems() {
        try {
            List<StickyItemDTO> items = stickyItemService.getAll();
            if (items.isEmpty()) return ResponseEntity.noContent().build();
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            log.error("Unexpected error while fetching all sticky items: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

}

package balancebite.controller.fooditem;

import balancebite.dto.fooditem.PromotedFoodItemDTO;
import balancebite.dto.fooditem.PromotedFoodItemInputDTO;
import balancebite.errorHandling.EntityNotFoundException;
import balancebite.mapper.PromotedFoodItemMapper;
import balancebite.model.foodItem.PromotedFoodItem;
import balancebite.service.fooditem.PromotedFoodItemService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/promotions")
public class PromotedFoodItemController {

    private static final Logger log = LoggerFactory.getLogger(PromotedFoodItemController.class);

    private final PromotedFoodItemService promotedFoodItemService;
    private final PromotedFoodItemMapper promotedFoodItemMapper;

    public PromotedFoodItemController(PromotedFoodItemService promotedFoodItemService,
                                      PromotedFoodItemMapper promotedFoodItemMapper) {
        this.promotedFoodItemService = promotedFoodItemService;
        this.promotedFoodItemMapper = promotedFoodItemMapper;
    }

    /**
     * Create a new promotion for a FoodItem.
     * Returns DTO (calculated fields computed in mapper).
     */
    @PreAuthorize("hasAnyRole('ADMIN','SUPERMARKET')")
    @PostMapping
    public ResponseEntity<?> createPromotion(@Valid @RequestBody PromotedFoodItemInputDTO inputDTO) {
        log.info("Create promotion for foodItemId={}", inputDTO.getFoodItemId());
        try {
            PromotedFoodItem saved = promotedFoodItemService.createPromotion(inputDTO);
            PromotedFoodItemDTO dto = promotedFoodItemMapper.toDTO(saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) { // already promoted, overlap, etc.
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) { // window invalid, validation, etc.
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Create promotion failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error"));
        }
    }

    /**
     * Update an existing promotion by ID (keeps same FoodItem).
     */
    @PreAuthorize("hasAnyRole('ADMIN','SUPERMARKET')")
    @PutMapping("/{promotionId}")
    public ResponseEntity<?> updatePromotion(@PathVariable Long promotionId,
                                             @Valid @RequestBody PromotedFoodItemInputDTO inputDTO) {
        log.info("Update promotion id={} for foodItemId={}", promotionId, inputDTO.getFoodItemId());
        try {
            PromotedFoodItem updated = promotedFoodItemService.updatePromotion(promotionId, inputDTO);
            PromotedFoodItemDTO dto = promotedFoodItemMapper.toDTO(updated);
            return ResponseEntity.ok(dto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) { // window invalid, foodItemId mismatch, etc.
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Update promotion failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error"));
        }
    }

    /**
     * Delete a promotion by ID.
     */
    @PreAuthorize("hasAnyRole('ADMIN','SUPERMARKET')")
    @DeleteMapping("/{promotionId}")
    public ResponseEntity<?> deletePromotion(@PathVariable Long promotionId) {
        log.info("Delete promotion id={}", promotionId);
        try {
            promotedFoodItemService.deletePromotion(promotionId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Delete promotion failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error"));
        }
    }
}

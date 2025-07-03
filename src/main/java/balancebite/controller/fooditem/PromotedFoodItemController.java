package balancebite.controller.fooditem;

import balancebite.dto.fooditem.PromotedFoodItemInputDTO;
import balancebite.errorHandling.EntityNotFoundException;
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

/**
 * REST controller for managing promoted food items.
 */
@RestController
@RequestMapping("admin/promotions")
public class PromotedFoodItemController {

    private static final Logger log = LoggerFactory.getLogger(PromotedFoodItemController.class);

    private final PromotedFoodItemService promotedFoodItemService;

    public PromotedFoodItemController(PromotedFoodItemService promotedFoodItemService) {
        this.promotedFoodItemService = promotedFoodItemService;
    }

    /**
     * Creates a new promotion for a food item.
     *
     * @param inputDTO The input data for the promotion.
     * @return The created PromotedFoodItem with 201 status code.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createPromotion(@Valid @RequestBody PromotedFoodItemInputDTO inputDTO) {
        log.info("Creating new promotion for foodItemId={}", inputDTO.getFoodItemId());

        try {
            PromotedFoodItem promotion = promotedFoodItemService.createPromotion(inputDTO);
            log.info("Promotion successfully created with ID: {}", promotion.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(promotion);
        } catch (EntityNotFoundException e) {
            log.warn("Food item not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            log.warn("Promotion conflict: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during promotion creation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Deletes an existing promotion by its ID.
     *
     * @param promotionId The ID of the promotion to delete.
     * @return 204 No Content if successful.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{promotionId}")
    public ResponseEntity<?> deletePromotion(@PathVariable Long promotionId) {
        log.info("Deleting promotion with ID: {}", promotionId);

        try {
            promotedFoodItemService.deletePromotion(promotionId);
            log.info("Promotion with ID={} successfully deleted", promotionId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.warn("Promotion not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during promotion deletion", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }
}

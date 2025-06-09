package balancebite.controller.diet;

import balancebite.dto.diet.DietPlanAdminListDTO;
import balancebite.service.interfaces.diet.IAdminDietPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller responsible for admin operations on diet plans.
 */
@RestController
@RequestMapping("/admin/dietplans")
@PreAuthorize("hasRole('ADMIN')") // Admin only
public class AdminDietPlanController {

    private static final Logger log = LoggerFactory.getLogger(AdminDietPlanController.class);
    private final IAdminDietPlanService adminDietPlanService;

    public AdminDietPlanController(IAdminDietPlanService adminDietPlanService) {
        this.adminDietPlanService = adminDietPlanService;
    }

    /**
     * Endpoint to retrieve all diet plans with creator and adjustedBy info.
     *
     * @return A list of DietPlanAdminListDTOs or an appropriate error response.
     */
    @GetMapping
    public ResponseEntity<?> getAllDietPlansForAdmin() {
        log.info("Admin requested all diet plans with creator info.");
        try {
            List<DietPlanAdminListDTO> plans = adminDietPlanService.getAllDietPlansWithCreator();
            if (plans.isEmpty()) {
                log.info("No diet plans found.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            log.info("Retrieved {} diet plans for admin.", plans.size());
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            log.error("Unexpected error while retrieving diet plans for admin", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred."));
        }
    }

    /**
     * Endpoint to delete a diet plan by ID (admin only).
     *
     * @param dietPlanId The ID of the diet plan to delete.
     * @return 204 No Content if successful, or 404 if not found.
     */
    @DeleteMapping("/{dietPlanId}")
    public ResponseEntity<?> deleteDietPlanById(@PathVariable Long dietPlanId) {
        log.info("Admin requested deletion of diet plan with ID: {}", dietPlanId);
        try {
            adminDietPlanService.deleteDietPlanById(dietPlanId);
            log.info("Successfully deleted diet plan with ID: {}", dietPlanId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete diet plan with ID {}: {}", dietPlanId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }


}

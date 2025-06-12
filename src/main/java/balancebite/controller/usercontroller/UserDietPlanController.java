package balancebite.controller.usercontroller;

import balancebite.dto.diet.DietPlanDTO;
import balancebite.dto.diet.DietPlanInputDTO;
import balancebite.errorHandling.DietPlanNotFoundException;
import balancebite.errorHandling.DuplicateDietPlanException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.model.meal.references.Diet;
import balancebite.security.JwtService;
import balancebite.service.interfaces.user.IUserDietPlanService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserDietPlanController {

    private static final Logger log = LoggerFactory.getLogger(UserDietPlanController.class);

    private final IUserDietPlanService userDietPlanService;
    private final JwtService jwtService;

    public UserDietPlanController(IUserDietPlanService userDietPlanService, JwtService jwtService) {
        this.userDietPlanService = userDietPlanService;
        this.jwtService = jwtService;
    }

    @PostMapping("/diet-plans")
    public ResponseEntity<?> createUserDietPlan(@RequestBody @Valid DietPlanInputDTO input,
                                                @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.extractUserId(authHeader.substring(7));
            DietPlanDTO created = userDietPlanService.createDietPlan(input, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (UserNotFoundException e) {
            log.warn("User not found during dietPlan creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input during dietPlan creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during dietPlan creation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Creation failed."));
        }
    }

    @PatchMapping("add-diet-plan/{dietPlanId}")
    public ResponseEntity<?> addDietPlanToUser(@PathVariable Long dietPlanId,
                                               @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.extractUserId(authHeader.substring(7));
            DietPlanDTO copiedDiet = userDietPlanService.addDietPlanToUser(userId, dietPlanId);
            log.info("✅ DietPlan {} successfully copied and assigned to user {}", dietPlanId, userId);
            return ResponseEntity.ok(copiedDiet);
        } catch (DietPlanNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (DuplicateDietPlanException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            Long userId = jwtService.extractUserId(authHeader.substring(7));
            log.error("❌ Unexpected error in addDietPlanToUser – dietPlanId={}, userId={}", dietPlanId, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to copy diet plan"));
        }
    }

    @PutMapping("/update-diet-plans/{dietPlanId}")
    public ResponseEntity<?> updateDietPlan(@PathVariable Long dietPlanId,
                                            @RequestBody @Valid DietPlanInputDTO input,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.extractUserId(authHeader.substring(7));
            DietPlanDTO updated = userDietPlanService.updateDietPlan(dietPlanId, input, userId);
            return ResponseEntity.ok(updated);
        } catch (DietPlanNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error while updating diet plan", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to update diet plan"));
        }
    }

    @PostMapping("/diet-plans/{dietPlanId}/days/{dayIndex}/meals/{mealId}")
    public ResponseEntity<?> addMealToDietDay(@PathVariable Long dietPlanId,
                                              @PathVariable int dayIndex,
                                              @PathVariable Long mealId,
                                              @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.extractUserId(authHeader.substring(7));
            DietPlanDTO updated = userDietPlanService.addMealToDietDay(userId, dietPlanId, dayIndex, mealId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Failed to add meal to diet day", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/diet-plans/{dietPlanId}/days/{dayIndex}/meals/{mealId}")
    public ResponseEntity<?> removeMealFromDietDay(@PathVariable Long dietPlanId,
                                                   @PathVariable int dayIndex,
                                                   @PathVariable Long mealId,
                                                   @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.extractUserId(authHeader.substring(7));
            DietPlanDTO updated = userDietPlanService.removeMealFromDietDay(userId, dietPlanId, dayIndex, mealId);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Failed to remove meal from diet day", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to remove meal"));
        }
    }

    @GetMapping("/diet-plans/{dietPlanId}")
    public ResponseEntity<?> getDietPlanById(@PathVariable Long dietPlanId,
                                             @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.extractUserId(authHeader.substring(7));
            DietPlanDTO dietPlan = userDietPlanService.getDietPlanById(dietPlanId, userId);
            return ResponseEntity.ok(dietPlan);
        } catch (DietPlanNotFoundException e) {
            log.warn("Diet plan not found: {}", e.getMessage());
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to fetch diet plan", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to retrieve diet plan"));
        }
    }

    @GetMapping("/diet-plans/{dietPlanId}/shopping-cart")
    public ResponseEntity<?> getShoppingCart(@PathVariable Long dietPlanId,
                                             @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.extractUserId(authHeader.substring(7));
            List<Map<String, Object>> shoppingList = userDietPlanService.getShoppingListForDietPlan(dietPlanId, userId);
            return ResponseEntity.ok(shoppingList);
        } catch (DietPlanNotFoundException e) {
            log.warn("Diet plan not found: {}", e.getMessage());
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to fetch shopping cart", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to retrieve shopping cart"));
        }
    }

    @GetMapping("/diet-plans")
    public ResponseEntity<Page<DietPlanDTO>> getFilteredDietPlans(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "all") String mode,
            @RequestParam(required = false) List<String> requiredDiets,
            @RequestParam(required = false) List<String> excludedDiets,
            @RequestParam(required = false) Diet dietFilter,
            @RequestParam(required = false) Double minCalories,
            @RequestParam(required = false) Double maxCalories,
            @RequestParam(required = false) Double minProtein,
            @RequestParam(required = false) Double maxProtein,
            @RequestParam(required = false) Double minCarbs,
            @RequestParam(required = false) Double maxCarbs,
            @RequestParam(required = false) Double minFat,
            @RequestParam(required = false) Double maxFat,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        try {
            Long userId = jwtService.extractUserId(authHeader.substring(7));

            Map<String, String> sortFieldMap = Map.ofEntries(
                    Map.entry("avgProtein", "avgProtein"),
                    Map.entry("avgCarbs", "avgCarbs"),
                    Map.entry("avgFat", "avgFat"),
                    Map.entry("avgCalories", "avgCalories"),
                    Map.entry("saveCount", "saveCount"),
                    Map.entry("weeklySaveCount", "weeklySaveCount"),
                    Map.entry("monthlySaveCount", "monthlySaveCount"),
                    Map.entry("createdAt", "createdAt"),
                    Map.entry("name", "name")
            );


            String mappedSortBy = sortFieldMap.get(sortBy);
            if (mappedSortBy == null) {
                log.warn("Invalid sortBy value '{}', falling back to default 'createdAt'", sortBy);
                mappedSortBy = "createdAt";
            }

            Pageable pageable = PageRequest.of(page, size);

            Page<DietPlanDTO> plans = userDietPlanService.getFilteredDietPlans(
                    requiredDiets,
                    excludedDiets,
                    userId, mode, dietFilter,
                    minCalories, maxCalories,
                    minProtein, maxProtein,
                    minCarbs, maxCarbs,
                    minFat, maxFat,
                    sortBy,
                    sortOrder,
                    pageable
            );

            if (plans.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(plans);

        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Page.empty());
        } catch (Exception e) {
            log.error("Error retrieving filtered diet plans", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Page.empty());
        }
    }

    @DeleteMapping("/diet-plans/{dietPlanId}/days/{dayIndex}")
    public ResponseEntity<?> removeDietDay(@PathVariable Long dietPlanId,
                                           @PathVariable int dayIndex,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.extractUserId(authHeader.substring(7));
            DietPlanDTO updatedPlan = userDietPlanService.removeDietDay(userId, dietPlanId, dayIndex);
            return ResponseEntity.ok(updatedPlan);
        } catch (DietPlanNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error while removing diet day", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to remove diet day"));
        }
    }

    @DeleteMapping("/diet-plan/{dietPlanId}")
    public ResponseEntity<?> removeDietPlanFromAuthenticatedUser(
            @PathVariable Long dietPlanId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            log.info("Received request to remove dietPlan ID {} from the authenticated user's list.", dietPlanId);

            Long userId = jwtService.extractUserId(authHeader.substring(7));
            userDietPlanService.removeDietPlanFromUser(userId, dietPlanId);

            log.info("Successfully removed dietPlan ID {} from authenticated user ID: {}", dietPlanId, userId);
            return ResponseEntity.noContent().build();

        } catch (UserNotFoundException | DietPlanNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during dietPlan removal for authenticated user", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to remove diet plan"));
        }
    }

}

package balancebite.controller.usercontroller;

import balancebite.dto.diet.DietPlanDTO;
import balancebite.dto.diet.DietPlanInputDTO;
import balancebite.dto.user.UserDTO;
import balancebite.errorHandling.DietPlanNotFoundException;
import balancebite.errorHandling.DuplicateDietPlanException;
import balancebite.errorHandling.MealNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.security.JwtService;
import balancebite.service.interfaces.diet.IUserDietPlanService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @PostMapping("/diet-plans/{dietPlanId}")
    public ResponseEntity<?> addDietPlanToUser(@PathVariable Long dietPlanId,
                                                             @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.extractUserId(authHeader.substring(7));
            UserDTO updatedUser = userDietPlanService.addDietPlanToUser(userId, dietPlanId);
            return ResponseEntity.ok(updatedUser);
        } catch (DietPlanNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (DuplicateDietPlanException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to copy diet plan"));
        }
    }

    @PutMapping("/diet-plans/{dietPlanId}")
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

    @GetMapping("/diet-plans")
    public ResponseEntity<?> getAllDietPlansForUser(@RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.extractUserId(authHeader.substring(7));
            List<DietPlanDTO> plans = userDietPlanService.getAllDietPlansForUser(userId);
            if (plans.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(plans);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to fetch diet plans", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to retrieve diet plans"));
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

    @DeleteMapping("/diet-plans/{dietPlanId}")
    public ResponseEntity<?> deleteDietPlan(@PathVariable Long dietPlanId,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.extractUserId(authHeader.substring(7));
            userDietPlanService.deleteDietPlan(dietPlanId, userId);
            return ResponseEntity.noContent().build();
        } catch (DietPlanNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error while deleting diet plan", e);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to delete diet plan"));
        }
    }
}

package balancebite.controller.usercontroller;

import balancebite.dto.diet.DietDTO;
import balancebite.dto.diet.DietInputDTO;
import balancebite.errorHandling.DietNotFoundException;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.security.JwtService;
import balancebite.service.interfaces.diet.IUserDietService;
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
public class UserDietController {

    private static final Logger log = LoggerFactory.getLogger(UserDietController.class);

    private final IUserDietService userDietService;
    private final JwtService jwtService;

    public UserDietController(IUserDietService dietService, JwtService jwtService) {
        this.userDietService = dietService;
        this.jwtService = jwtService;
    }

    @PostMapping("/create-diet")
    public ResponseEntity<?> createDiet(@RequestBody @Valid DietInputDTO input,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.extractUserId(authHeader.substring(7));
            DietDTO created = userDietService.createDiet(input, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (UserNotFoundException e) {
            log.warn("User not found during diet creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during diet creation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Creation failed."));
        }
    }

    @GetMapping("/diet/{id}")
    public ResponseEntity<?> getUserDietById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.extractUserId(authHeader.substring(7));
            DietDTO dto = userDietService.getDietById(id, userId);
            return ResponseEntity.ok(dto);
        } catch (DietNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/diets")
    public ResponseEntity<?> getAllDietsForAuthenticatedUser(@RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.extractUserId(authHeader.substring(7));
            List<DietDTO> diets = userDietService.getAllDietsForUser(userId);
            if (diets.isEmpty()) return ResponseEntity.noContent().build();
            return ResponseEntity.ok(diets);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/diet/{id}")
    public ResponseEntity<?> updateDiet(@PathVariable Long id,
                                        @RequestBody @Valid DietInputDTO input,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.extractUserId(authHeader.substring(7));
            DietDTO updated = userDietService.updateDiet(id, input, userId);
            return ResponseEntity.ok(updated);
        } catch (DietNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during diet update", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Update failed."));
        }
    }

    @DeleteMapping("/diet/{id}")
    public ResponseEntity<?> deleteDiet(@PathVariable Long id,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            Long userId = jwtService.extractUserId(authHeader.substring(7));
            userDietService.deleteDiet(id, userId);
            return ResponseEntity.noContent().build();
        } catch (DietNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }
}

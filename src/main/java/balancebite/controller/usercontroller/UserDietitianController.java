package balancebite.controller.usercontroller;

import balancebite.dto.diet.DietPlanDTO;
import balancebite.dto.diet.DietPlanInputDTO;
import balancebite.dto.meal.MealDTO;
import balancebite.dto.meal.MealInputDTO;
import balancebite.dto.user.ClientLinkRequestDTO;
import balancebite.errorHandling.DuplicateMealException;
import balancebite.errorHandling.EntityNotFoundException;
import balancebite.errorHandling.ForbiddenActionException;
import balancebite.errorHandling.InvalidFoodItemException;
import balancebite.model.user.User;
import balancebite.repository.UserRepository;
import balancebite.service.user.UserDietitianService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import balancebite.security.JwtService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/dietitian")
public class UserDietitianController {

    private static final Logger log = LoggerFactory.getLogger(UserDietitianController.class);

    private final UserDietitianService userDietitianService;

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public UserDietitianController(UserDietitianService userDietitianService, JwtService jwtService, UserRepository userRepository) {
        this.userDietitianService = userDietitianService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    /**
     * Endpoint for dietitians to invite a client using their email.
     *
     * @param requestDTO   the email of the client to invite
     * @return HTTP 200 if successful
     */
    @PostMapping("/invite-client")
    @PreAuthorize("hasRole('DIETITIAN')")
    public ResponseEntity<Void> inviteClient(
            @Valid @RequestBody ClientLinkRequestDTO requestDTO,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.substring(7); // strip "Bearer "
        Long userId = jwtService.extractUserId(token);

        log.info("Dietitian ID {} is inviting client with email {}", userId, requestDTO.getClientEmail());

        // Haal User op uit database
        User dietitian = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Dietitian not found"));

        userDietitianService.inviteClientByEmail(requestDTO, dietitian);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/create-meal", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('DIETITIAN') or hasRole('ADMIN')")
    public ResponseEntity<?> createMealAsDietitian(
            @RequestPart("mealInputDTO") String mealInputDTOJson,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestParam(value = "sharedUserIds", required = false) List<Long> sharedUserIds,
            @RequestParam(value = "sharedEmails", required = false) List<String> sharedEmails,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            log.info("Received request to create a private meal by dietitian");

            ObjectMapper objectMapper = new ObjectMapper();
            MealInputDTO mealInputDTO = objectMapper.readValue(mealInputDTOJson, MealInputDTO.class);

            if (imageFiles != null && !imageFiles.isEmpty()) {
                mealInputDTO.setImageFiles(imageFiles);
            }

            String token = authorizationHeader.substring(7);
            Long dietitianId = jwtService.extractUserId(token);

            MealDTO createdMeal = userDietitianService.createMealAsDietitian(
                    mealInputDTO, dietitianId, sharedUserIds, sharedEmails
            );

            log.info("Successfully created meal as dietitian ID: {}", dietitianId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMeal);

        } catch (EntityNotFoundException e) {
            log.warn("Dietitian not found or invalid: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));

        } catch (DuplicateMealException e) {
            log.warn("Duplicate meal detected: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));

        } catch (InvalidFoodItemException e) {
            log.warn("Invalid food item in meal: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error occurred during dietitian meal creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/create-dietplan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('DIETITIAN') or hasRole('ADMIN')")
    public ResponseEntity<?> createDietPlanAsDietitian(
            @RequestPart("dietPlanInputDTO") String inputJson,
            @RequestParam(value = "sharedUserIds", required = false) List<Long> sharedUserIds,
            @RequestParam(value = "sharedEmails", required = false) List<String> sharedEmails,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String token = authorizationHeader.substring(7);
            Long dietitianId = jwtService.extractUserId(token);

            ObjectMapper objectMapper = new ObjectMapper();
            DietPlanInputDTO input = objectMapper.readValue(inputJson, DietPlanInputDTO.class);

            DietPlanDTO createdPlan = userDietitianService.createDietPlanAsDietitian(
                    input, dietitianId, sharedUserIds, sharedEmails
            );

            log.info("Successfully created diet plan as dietitian ID: {}", dietitianId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlan);

        } catch (EntityNotFoundException e) {
            log.warn("Dietitian not found or invalid: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error during diet plan creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }


    public static class MealAccessRequest {
        public Long mealId;
        public List<Long> sharedUserIds;
        public List<String> sharedEmails;
    }

    @PostMapping("/add-meal-access")
    @PreAuthorize("hasRole('DIETITIAN') or hasRole('ADMIN')")
    public ResponseEntity<?> addAccessToMeal(
            @RequestBody MealAccessRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String token = authorizationHeader.substring(7);
            Long dietitianId = jwtService.extractUserId(token);

            userDietitianService.addSharedAccessToMeal(
                    request.mealId,
                    request.sharedUserIds,
                    request.sharedEmails,
                    dietitianId
            );
            return ResponseEntity.ok(Map.of("message", "Meal access updated successfully"));
        } catch (ForbiddenActionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    public static class DietPlanAccessRequest {
        public Long dietPlanId;
        public List<Long> sharedUserIds;
        public List<String> sharedEmails;
    }

    @PostMapping("/add-dietplan-access")
    @PreAuthorize("hasRole('DIETITIAN') or hasRole('ADMIN')")
    public ResponseEntity<?> addAccessToDietPlan(
            @RequestBody DietPlanAccessRequest request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try {
            String token = authorizationHeader.substring(7);
            Long dietitianId = jwtService.extractUserId(token);

            userDietitianService.addSharedAccessToDietPlan(
                    request.dietPlanId,
                    request.sharedUserIds,
                    request.sharedEmails,
                    dietitianId
            );

            log.info("Shared access added to diet plan ID {} by dietitian ID {}", request.dietPlanId, dietitianId);
            return ResponseEntity.ok(Map.of("message", "Diet plan access updated successfully"));
        } catch (ForbiddenActionException e) {
            log.warn("Forbidden access attempt for diet plan ID {}: {}", request.dietPlanId, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error while adding shared access to diet plan ID {}: {}", request.dietPlanId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }


}

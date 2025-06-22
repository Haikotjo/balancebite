package balancebite.controller.stickyitemcontroller;

import balancebite.dto.stickyitem.StickyItemDTO;
import balancebite.dto.stickyitem.StickyItemInputDTO;
import balancebite.errorHandling.UserNotFoundException;
import balancebite.model.user.User;
import balancebite.security.JwtService;
import balancebite.service.interfaces.IStickyItemService;
import balancebite.service.user.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/sticky-items")
public class AdminStickyItemController {

    private static final Logger log = LoggerFactory.getLogger(AdminStickyItemController.class);
    private final IStickyItemService stickyItemService;
    private final JwtService jwtService;
    private final UserService userService;

    public AdminStickyItemController(IStickyItemService stickyItemService, JwtService jwtService, UserService userService) {
        this.stickyItemService = stickyItemService;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    /**
     * Creates a new sticky item. Only accessible by admin.
     */
    @PostMapping
    public ResponseEntity<?> createStickyItem(@RequestBody StickyItemInputDTO inputDTO, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.substring(7);
            Long adminId = jwtService.extractUserId(token);
            User admin = userService.findUserById(adminId);

            StickyItemDTO result = stickyItemService.createStickyItem(inputDTO, admin);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);

        } catch (UserNotFoundException | EntityNotFoundException e) {
            log.warn("Admin user not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error while creating sticky item: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred."));
        }
    }
}

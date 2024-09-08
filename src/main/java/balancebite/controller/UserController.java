package balancebite.controller;

import balancebite.dto.user.UserDTO;
import balancebite.dto.user.UserInputDTO;
import balancebite.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing users.
 * Provides endpoints for CRUD operations.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Retrieves all users.
     *
     * @return a list of all UserDTOs.
     */
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve.
     * @return the UserDTO if found, or 404 Not Found if not.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<UserDTO> userDTO = userService.getUserById(id);
        return userDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Creates a new user.
     *
     * @param inputDTO the user data to create.
     * @return the created UserDTO.
     */
    @PostMapping
    public UserDTO createUser(@RequestBody UserInputDTO inputDTO) {
        return userService.createUser(inputDTO);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete.
     * @return 204 No Content if successful, or 404 Not Found if the user does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<UserDTO> userDTO = userService.getUserById(id);
        if (userDTO.isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Adds a meal to the user's personal meal list.
     *
     * @param userId the ID of the user.
     * @param mealId the ID of the meal to add.
     * @return HTTP response indicating success or failure.
     */
    @PostMapping("/{userId}/meals/{mealId}")
    public ResponseEntity<Void> addMealToUser(@PathVariable Long userId, @PathVariable Long mealId) {
        userService.addMealToUser(userId, mealId);
        return ResponseEntity.ok().build(); // Return 200 OK
    }
}

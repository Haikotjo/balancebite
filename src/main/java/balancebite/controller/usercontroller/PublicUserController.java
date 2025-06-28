package balancebite.controller.usercontroller;
import balancebite.dto.user.UserSearchDTO;
import balancebite.service.user.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/users")
public class PublicUserController {

    private final UserService userService;

    public PublicUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/search")
    public List<UserSearchDTO> searchUsers(@RequestParam String query) {
        return userService.searchUsersByName(query);
    }
}

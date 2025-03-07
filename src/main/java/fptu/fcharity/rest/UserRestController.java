package fptu.fcharity.rest;

import fptu.fcharity.entity.User;
import fptu.fcharity.service.user.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/me")
    public Optional<User> me(@RequestAttribute("userEmail") String email) {
        System.out.println("call get me: " + email);
        System.out.println("result: " + userService.findUserByEmail(email));
        return userService.findUserByEmail(email);
    }

    @GetMapping("/users")
    public List<User> users() {
        return userService.getAllUsers();
    }
}

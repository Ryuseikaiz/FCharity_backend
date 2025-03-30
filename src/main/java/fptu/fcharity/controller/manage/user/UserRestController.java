package fptu.fcharity.controller.manage.user;

import fptu.fcharity.entity.User;

import fptu.fcharity.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserRestController {
    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/users/me")
    public Optional<User> me(@RequestAttribute("userEmail") String email) {
        System.out.println("call get me: " + email);
        System.out.println("result: " + userService.findByEmail(email));
        return userService.findByEmail(email);
    }

    @GetMapping("/users")
    public List<User> users() {
        return userService.findAll();
    }

    @GetMapping("/users/outside/{organizationId}")
    public List<User> getUser(@PathVariable UUID organizationId) {
        return userService.findAllUsersNotInOrganization(organizationId);
    }
}

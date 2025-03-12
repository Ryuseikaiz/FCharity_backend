package fptu.fcharity.rest;

import fptu.fcharity.entity.User;
import fptu.fcharity.mapper.UserResponseMapper;
import fptu.fcharity.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserRestController {
    private final UserService userService;
    private final UserResponseMapper userResponseMapper;

    @Autowired
    public UserRestController(UserService userService, UserResponseMapper userResponseMapper) {
        this.userService = userService;
        this.userResponseMapper = userResponseMapper;
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

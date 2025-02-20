package fptu.fcharity.controller;

import fptu.fcharity.dto.authentication.ChangePasswordDto;
import fptu.fcharity.mapper.UserResponseMapper;
import fptu.fcharity.entity.User;
import fptu.fcharity.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;
    private final UserResponseMapper userResponseMapper;

    @Autowired
    public UserController(UserService userService, UserResponseMapper userResponseMapper) {
        this.userService = userService;
        this.userResponseMapper = userResponseMapper;
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userResponseMapper.toDTO(currentUser));
    }

    @GetMapping("/all-user")
    public ResponseEntity<List<User>> allUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }
    @GetMapping
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello it's me");
    }
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
            return ResponseEntity.ok(userService.updatePassword(changePasswordDto.getEmail(), changePasswordDto.getNewPassword(), changePasswordDto.getOldPassword()));
    }
}
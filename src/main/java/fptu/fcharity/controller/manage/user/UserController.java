package fptu.fcharity.controller.manage.user;

import fptu.fcharity.dto.authentication.ChangePasswordDto;
import fptu.fcharity.dto.project.ProjectRequestDto;
import fptu.fcharity.entity.ProjectRequest;
import fptu.fcharity.entity.TaskPlan;
import fptu.fcharity.entity.User;
import fptu.fcharity.response.project.ProjectRequestResponse;
import fptu.fcharity.service.manage.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> authenticatedUser(Authentication authentication) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/all-user")
    public ResponseEntity<List<User>> allUsers() {
        return ResponseEntity.ok(userService.allUsers());
    }

    @GetMapping
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello it's me");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        return ResponseEntity.ok(userService.changePassword(changePasswordDto));
    }
    @GetMapping("/organizations/{organization_id}")
    public ResponseEntity<?> getUser(@PathVariable UUID organization_id) {
        return ResponseEntity.ok(userService.getAllUsersNotInOrganization(organization_id));
    }
    @GetMapping("/{user_id}/invitations")
    public ResponseEntity<?> getInvitationsOfUser(@PathVariable UUID user_id) {
        List<ProjectRequestResponse> projectRequests = userService.getInvitationsOfUserId(user_id);
        return ResponseEntity.ok(projectRequests);
    }
    @GetMapping("/{user_id}/task-plans")
    public ResponseEntity<?> getTaskPlansOfUser(@PathVariable UUID user_id) {
        return ResponseEntity.ok(userService.getTasksOfUserId(user_id));
    }
    @GetMapping("/{project_id}/task-plans")
    public ResponseEntity<?> getTaskPlansOfProject(@PathVariable UUID project_id) {
        return ResponseEntity.ok(userService.getTasksOfProjectId(project_id));
    }
}
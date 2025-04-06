package fptu.fcharity.controller.manage.user;

import fptu.fcharity.dto.authentication.ChangePasswordDto;
import fptu.fcharity.dto.project.ProjectRequestDto;
import fptu.fcharity.entity.ProjectRequest;
import fptu.fcharity.entity.TaskPlan;
import fptu.fcharity.dto.user.UpdateProfileDto;
import fptu.fcharity.entity.TransactionHistory;
import fptu.fcharity.entity.User;
import fptu.fcharity.response.project.ProjectRequestResponse;
import fptu.fcharity.response.user.TransactionHistoryResponse;
import fptu.fcharity.service.manage.user.UserService;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.UserResponseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;
    private final UserResponseMapper userResponseMapper;

    public UserController(UserService userService, @Qualifier("userResponseMapperImpl") UserResponseMapper userResponseMapper) {
        this.userService = userService;
        this.userResponseMapper = userResponseMapper;
    }

    @GetMapping("/my-profile")
    public ResponseEntity<?> viewMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userResponseMapper.toDTO(currentUser));
    }
    @GetMapping("/current-wallet")
    public ResponseEntity<?> getMyWallet() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(currentUser.getWalletAddress());
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

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileDto updateProfileDto) {
        // Lấy người dùng hiện tại từ context security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        User updatedUser = userService.updateProfile(currentUser.getId(), updateProfileDto);
        return ResponseEntity.ok(userResponseMapper.toDTO(updatedUser));
    }

    @GetMapping("/organizations/{organization_id}")
    public ResponseEntity<?> getUser(@PathVariable UUID organization_id) {
        List<User> users = userService.getAllUsersNotInOrganization(organization_id);
        return ResponseEntity.ok(users);
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
    @GetMapping("/{user_id}/transaction-history")
    public ResponseEntity<?> getTransactionHistoryOfUser(@PathVariable UUID user_id) {
        List<TransactionHistoryResponse> l = userService.getTransactionHistoryOfUserId(user_id);
        return ResponseEntity.ok(l);
    }
    @GetMapping("/{project_id}/task-plans")
    public ResponseEntity<?> getTaskPlansOfProject(@PathVariable UUID project_id) {
        return ResponseEntity.ok(userService.getTasksOfProjectId(project_id));
    }
}
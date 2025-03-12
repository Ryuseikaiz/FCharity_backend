package fptu.fcharity.controller.admindashboard;

import fptu.fcharity.dto.admindashboard.UserDTO;
import fptu.fcharity.service.admindashboard.ManageUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class ManageUserController {
    private final ManageUserService manageUserService;

    // Lấy danh sách user
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(manageUserService.getAllUsers());
    }

    // Lấy user theo ID
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok(manageUserService.getUserById(userId));
    }

    // Xóa user
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID userId) {
        manageUserService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully.");
    }

//    // Duyệt user lên Founder
//    @PutMapping("/{userId}/approve-founder")
//    public ResponseEntity<Void> approveUserToFounder(@PathVariable UUID userId) {
//        manageUserService.approveUserToFounder(userId);
//        return ResponseEntity.ok().build();
//    }

    // Ban user
    @PutMapping("/ban/{userId}")
    public ResponseEntity<String> banUser(@PathVariable UUID userId) {
        manageUserService.banUser(userId);
        return ResponseEntity.ok("User has been banned successfully.");
    }

    @PutMapping("/unban/{userId}")
    public ResponseEntity<String> unbanUser(@PathVariable UUID userId) {
        manageUserService.unbanUser(userId);
        return ResponseEntity.ok("User has been unbanned successfully.");
    }

}

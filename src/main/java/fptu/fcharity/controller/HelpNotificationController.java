package fptu.fcharity.controller;

import fptu.fcharity.dto.notification.NotificationDto;
import fptu.fcharity.repository.HelpNotificationRepository;
import fptu.fcharity.service.HelpNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class HelpNotificationController {

    private final HelpNotificationService notificationService;
    private final HelpNotificationRepository notificationRepository;

    // ✅ GET: Lấy danh sách thông báo theo userId hoặc role
    @GetMapping
    public List<NotificationDto> getNotifications(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String role) {

        if (userId != null) {
            return notificationService.getNotificationsByUser(userId);
        } else if (role != null) {
            return notificationService.getNotificationsByRole(role);
        } else {
            throw new IllegalArgumentException("You must provide either userId or role");
        }
    }

    @PutMapping("/mark-read/{id}")
    public void markAsRead(@PathVariable UUID id) {
        notificationService.markAsRead(id);
    }

    @PutMapping("/mark-all")
    public void markAllAsRead(@RequestParam UUID userId) {
        notificationService.markAllAsRead(userId);
    }

}

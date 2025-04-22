package fptu.fcharity.service;

import fptu.fcharity.dto.notification.NotificationDto;
import fptu.fcharity.entity.HelpNotification;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.HelpNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HelpNotificationService {

    private final HelpNotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void notifyUser(User user, String title, String targetRole, String content, String link) {
        HelpNotification notification = HelpNotification.builder()
                .user(user)
                .title(title)
                .targetRole(targetRole)
                .content(content)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .link(link)
                .build();

        notificationRepository.save(notification);

        NotificationDto dto = mapToDto(notification);
        messagingTemplate.convertAndSend("/topic/help-notifications/" + user.getId(), dto);
    }

    // ✅ Lấy danh sách theo userId
    public List<NotificationDto> getNotificationsByUser(UUID userId) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ✅ Lấy danh sách theo role
    public List<NotificationDto> getNotificationsByRole(String role) {
        return notificationRepository.findByTargetRoleOrderByCreatedAtDesc(role)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ✅ Đánh dấu đã đọc theo ID
    public void markAsRead(UUID notificationId) {
        HelpNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // ✅ Đánh dấu tất cả đã đọc theo userId
    public void markAllAsRead(UUID userId) {
        List<HelpNotification> notifications = notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    public NotificationDto mapToDto(HelpNotification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .title(notification.getTitle())
                .targetRole(notification.getTargetRole())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .isRead(notification.isRead())
                .link(notification.getLink())
                .build();
    }
}

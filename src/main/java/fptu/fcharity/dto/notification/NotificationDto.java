package fptu.fcharity.dto.notification;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private UUID id;
    private UUID userId;
    private String title;
    private String targetRole;
    private String content;
    private LocalDateTime createdAt;
    private boolean isRead;
    private String link;
}


package fptu.fcharity.repository;

import fptu.fcharity.entity.HelpNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HelpNotificationRepository extends JpaRepository<HelpNotification, UUID> {
    List<HelpNotification> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<HelpNotification> findByUser_IdOrderByCreatedAtDesc(UUID userId);

    List<HelpNotification> findByTargetRoleOrderByCreatedAtDesc(String targetRole);
}


package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "notification_id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Nationalized
    @Column(name = "message")
    private String message;

    @Column(name = "notification_date")
    private Instant notificationDate;

    @Nationalized
    @Column(name = "notification_status", length = 50)
    private String notificationStatus;

    @Nationalized
    @Column(name = "link")
    private String link;

}
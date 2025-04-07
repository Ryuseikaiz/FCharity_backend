package fptu.fcharity.entity;

import fptu.fcharity.utils.constants.request.RequestStatus;
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
@Table(name = "help_requests")
public class HelpRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("newid()")
    @Column(name = "request_id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Nationalized
    @Column(name = "title")
    private String title;

    @Nationalized
    @Column(name = "content",  length = 1000)
    private String content;

    @Column(name = "creation_date")
    private Instant creationDate;

    @Nationalized
    @Column(name = "phone", length = 15)
    private String phone;

    @Nationalized
    @Column(name = "email")
    private String email;

    @Nationalized
    @Column(name = "location")
    private String location;

    @Column(name = "is_emergency")
    private Boolean isEmergency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Nationalized
    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "reason")
    private String reason;

    public HelpRequest() {
    }
    public HelpRequest(User user, String title,
                       String content,
                       String phone, String email, String location,
                       Boolean isEmergency,
                       Category category,
                       String reason
    ) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.creationDate = Instant.now();
        this.phone = phone;
        this.email = email;
        this.location = location;
        this.isEmergency = isEmergency;
        this.category = category;
        this.status = RequestStatus.PENDING;
        this.reason = reason;
    }

}
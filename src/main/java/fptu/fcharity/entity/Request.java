package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "request_id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Nationalized
    @Column(name = "title")
    private String title;

    @Nationalized
    @Column(name = "content")
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

    @Nationalized
    @Column(name = "attachment")
    private String attachment;

    @Column(name = "is_emergency")
    private Boolean isEmergency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private RequestStatus requestStatus;

    public Request() {
    }
    public Request(UUID id, User user, String title,
                   String content,
                   String phone, String email, String location,
                   String attachment, Boolean isEmergency,
                   Category category, Tag tag
                   ) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.content = content;
        this.creationDate = Instant.now();
        this.phone = phone;
        this.email = email;
        this.location = location;
        this.attachment = attachment;
        this.isEmergency = isEmergency;
        this.category = category;
        this.tag = tag;
        this.requestStatus = RequestStatus.PENDING;
    }

    public enum RequestStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        COMPLETED
    }
}
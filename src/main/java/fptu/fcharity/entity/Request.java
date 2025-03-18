package fptu.fcharity.entity;

import fptu.fcharity.utils.constants.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Request {
    @Id
    @GeneratedValue(generator = "UUID")
    @ColumnDefault("newid()")
    @Column(name="request_id", unique = true, updatable = false, nullable = false)
    private UUID id;

    @Column(name = "organization_id")
    private UUID organizationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Nationalized
    @Column(name = "title")
    private String title;

    @Nationalized
    @Column(length = 1000)
    private String content;

    @Column(name = "creation_date")
    private Instant creationDate;

    @Nationalized
    @Column(length = 15)
    private String phone;

    @Nationalized
    @Column(nullable = false)
    private String email;

    @Nationalized
    @Column(nullable = false)
    private String location;

    @Column(name="attachment")
    private String attachment;

    @Column(name = "is_emergency")
    private boolean isEmergency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tagId;

    @Nationalized
    @Column(nullable = false, length = 50)
    private String status;



    public Request( User user, String title,
                    String content,
                    String phone, String email, String location,
                    Boolean isEmergency,
                    Category category
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
    }
}
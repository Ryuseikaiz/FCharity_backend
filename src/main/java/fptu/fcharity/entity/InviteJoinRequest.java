package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "invite_join_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class InviteJoinRequest {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "invite_join_request_id", unique = true, updatable = false, nullable = false)
    private UUID inviteJoinRequestId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user; // Thay userId bằng đối tượng User

    @Column(name = "organization_id")
    private UUID organizationId;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "cv_location")
    private String cvLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type")
    private RequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RequestStatus status;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    public enum RequestType {
        Request, Invitation
    }

    public enum RequestStatus {
        Pending, Approved, Rejected
    }
}

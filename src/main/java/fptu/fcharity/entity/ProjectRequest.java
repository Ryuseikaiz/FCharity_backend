package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "project_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class ProjectRequest {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "project_request_id", unique = true, updatable = false, nullable = false)
    private UUID projectRequestId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type")
    private ProjectRequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProjectRequestStatus status;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum ProjectRequestType {
        Request, Invitation
    }

    public enum ProjectRequestStatus {
        Pending, Accepted, Rejected
    }
}

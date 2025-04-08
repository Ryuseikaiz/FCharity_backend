package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "organization_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class OrganizationRequest {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "organization_request_id", unique = true, updatable = false, nullable = false)
    private UUID organizationRequestId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "organization_id", referencedColumnName = "organization_id")
    private Organization organization;


    @Enumerated(EnumType.STRING)
    @Column(name = "request_type")
    private OrganizationRequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrganizationRequestStatus status;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public enum OrganizationRequestType {
        Join, Invitation
    }

    public enum OrganizationRequestStatus {
        Pending, Approved, Rejected
    }
}

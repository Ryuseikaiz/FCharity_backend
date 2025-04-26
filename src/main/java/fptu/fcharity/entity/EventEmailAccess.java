package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "event_email_access")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EventEmailAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "event_email_access_id")
    private UUID eventEmailAccessId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_event_id", referencedColumnName = "organization_event_id", nullable = false)
    private OrganizationEvent organizationEvent;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "access_type", nullable = false)
    private AccessType accessType;

    public enum AccessType {
        INCLUDE, EXCLUDE
    }
}


package fptu.fcharity.entity;

import com.nimbusds.openid.connect.sdk.assurance.evidences.Organization;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "organization_members")
public class OrganizationMember {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "membership_id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Column(name = "join_date")
    private Instant joinDate;

    @Column(name = "leave_date")
    private Instant leaveDate;

}
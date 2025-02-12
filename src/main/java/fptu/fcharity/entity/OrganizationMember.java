package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="organization_members")
@Getter
@Setter
public class OrganizationMember {

    @Id
    @Column(name="membership_id", unique = true, updatable = false, nullable = false)
    private UUID membershipId;

    @Column(name="user_id")
    private UUID userId;

    @Column(name="organization_id")
    private UUID organizationId;

    @Column(name="join_date")
    private Date joinDate;

    @Column(name="leave_date")
    private Date leaveDate;

    public OrganizationMember() {
    }

    public OrganizationMember(UUID membershipId, UUID userId, UUID organizationId, Date joinDate, Date leaveDate) {
        this.membershipId = membershipId;
        this.userId = userId;
        this.organizationId = organizationId;
        this.joinDate = joinDate;
        this.leaveDate = leaveDate;
    }
}

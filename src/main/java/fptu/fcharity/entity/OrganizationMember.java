package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="organization_members")
@Getter
@Setter
public class OrganizationMember {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name="membership_id", unique = true, updatable = false, nullable = false)
    private UUID membershipId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "join_date", nullable = false)
    private LocalDateTime joinDate;

    @Column(name = "leave_date")
    private LocalDateTime leaveDate;

    public OrganizationMember() {
    }

    public OrganizationMember(UUID membershipId, User user, Organization organization, LocalDateTime joinDate, LocalDateTime leaveDate) {
        this.membershipId = membershipId;
        this.user = user;
        this.organization = organization;
        this.joinDate = joinDate;
        this.leaveDate = leaveDate;
    }
}

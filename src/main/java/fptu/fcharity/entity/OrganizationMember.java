package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="organization_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrganizationMember {

    @Id
    @GeneratedValue(generator = "UUID")
    @ColumnDefault("newid()")
    @Column(name="membership_id", unique = true, updatable = false, nullable = false)
    private UUID membershipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "member_role")
    private OrganizationMemberRole memberRole;

    @Column(name = "join_date")
    private LocalDateTime joinDate;

    @Column(name = "leave_date")
    private LocalDateTime leaveDate;

    public enum OrganizationMemberRole {
        CEO, Manager, Member
    }
}

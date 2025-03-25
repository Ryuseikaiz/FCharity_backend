package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "organization_members")
public class OrganizationMember {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @Nationalized
    @Column(name = "member_role", length = 50)
    private String memberRole;

}
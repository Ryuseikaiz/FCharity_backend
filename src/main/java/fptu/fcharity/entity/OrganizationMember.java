package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="organization_members")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "join_date")
    private LocalDateTime joinDate;

    @Column(name = "leave_date")
    private LocalDateTime leaveDate;
}

package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "project_members")
@Getter
@Setter
public class ProjectMember {
    @Id
    @Column(name = "membership_id", columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID membershipId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "join_date", nullable = false)
    private LocalDateTime joinDate;

    @Column(name = "leave_date")
    private LocalDateTime leaveDate;

    @Column(name = "member_role", nullable = false)
    private String memberRole;
}
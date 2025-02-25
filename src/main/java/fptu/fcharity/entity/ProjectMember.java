package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "project_members")
public class ProjectMember {
    @Id
    @ColumnDefault("newid()")
    @Column(name = "membership_id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "join_date")
    private Instant joinDate;

    @Column(name = "leave_date")
    private Instant leaveDate;

    @Column(name = "member_role", length = 36)
    private String memberRole;

}
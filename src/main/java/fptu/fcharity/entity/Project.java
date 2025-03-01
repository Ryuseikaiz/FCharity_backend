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
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("newid()")
    @Column(name = "project_id", nullable = false)
    private UUID id;

    @Nationalized
    @Column(name = "project_name")
    private String projectName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id")
    private User leader;

    @Nationalized
    @Column(name = "email")
    private String email;

    @Nationalized
    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Nationalized
    @Column(name = "project_description")
    private String projectDescription;

    @Nationalized
    @Column(name = "project_status", length = 50)
    private String projectStatus;

    @Nationalized
    @Column(name = "report_file")
    private String reportFile;

    @Column(name = "planned_start_time")
    private Instant plannedStartTime;

    @Column(name = "planned_end_time")
    private Instant plannedEndTime;

    @Column(name = "actual_start_time")
    private Instant actualStartTime;

    @Column(name = "actual_end_time")
    private Instant actualEndTime;

    @Nationalized
    @Column(name = "shutdown_reason")
    private String shutdownReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_address")
    private Wallet walletAddress;

}
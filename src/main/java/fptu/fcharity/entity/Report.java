package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reports")
@Getter
@Setter
public class Report {
    @Id
    @Column(name = "report_id", columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID reportId;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "report_date", nullable = false)
    private LocalDateTime reportDate;
}
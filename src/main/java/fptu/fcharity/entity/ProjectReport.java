package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "project_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class ProjectReport {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "report_id", unique = true, updatable = false, nullable = false)
    private UUID reportId;

    @ManyToOne
    @JoinColumn(name = "reporter_id", referencedColumnName = "user_id")
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    private Project project;

    @Column(name = "reason")
    private String reason;

    @Column(name = "report_date")
    private Instant reportDate;
}

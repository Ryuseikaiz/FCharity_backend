package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "post_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
public class PostReport {
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "report_id", unique = true, updatable = false, nullable = false)
    private UUID reportId;

    @ManyToOne
    @JoinColumn(name = "reporter_id", referencedColumnName = "user_id")
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    private Post post;

    @Column(name = "reason")
    private String reason;

    @Column(name = "report_date")
    private Instant reportDate;
}

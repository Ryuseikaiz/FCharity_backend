package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "timeline")
@Getter
@Setter
public class Timeline {
    @Id
    @Column(name = "phase_id", columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID phaseId;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "content", nullable = false)
    private String content;
}
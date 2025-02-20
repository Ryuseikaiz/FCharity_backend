package fptu.fcharity.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "task_plan")
@Getter
@Setter
public class TaskPlan {
    @Id
    @Column(name = "task_plan_id", columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID taskPlanId;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "task_name", nullable = false)
    private String taskName;

    @Column(name = "task_plan_description", nullable = false)
    private String taskPlanDescription;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "task_plan_status", nullable = false)
    private String taskPlanStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
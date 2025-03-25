package fptu.fcharity.response.project;

import fptu.fcharity.entity.TaskPlan;
import fptu.fcharity.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
public class TaskPlanResponse {
    private UUID id;

    private UUID projectId;

    private User user;

    @Nationalized
    private String taskName;

    @Nationalized
    private String taskPlanDescription;

    private Instant startTime;

    private Instant endTime;

    private String taskPlanStatus;

    private Instant createdAt;

    private Instant updatedAt;
    public TaskPlanResponse(TaskPlan taskPlan) {
        this.id =taskPlan.getId();
        this.user = taskPlan.getUser();
        this.projectId = taskPlan.getProject().getId();
        this.taskName = taskPlan.getTaskName();
        this.taskPlanDescription = taskPlan.getTaskPlanDescription();
        this.startTime = taskPlan.getStartTime();
        this.endTime = taskPlan.getEndTime();
        this.taskPlanStatus = taskPlan.getTaskPlanStatus();
        this.createdAt = Instant.now();
        this.updatedAt = taskPlan.getUpdatedAt();
    }
}

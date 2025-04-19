package fptu.fcharity.response.project;

import fptu.fcharity.entity.TaskPlan;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
public class TaskPlanResponse {
    private UUID id;
    private UUID phaseId;
    private UUID userId;
    private String taskName;
    private String taskPlanDescription;
    private Instant startTime;
    private Instant endTime;
    private UUID taskPlanStatusId;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID parentTaskId;
    public TaskPlanResponse(TaskPlan t){
        this.id = t.getId();
        this.phaseId = t.getPhase().getId();
        if(t.getUser() != null){
            this.userId = t.getUser().getId();
        }
        this.taskName = t.getTaskName();
        this.taskPlanDescription = t.getTaskPlanDescription();
        this.startTime = t.getStartTime();
        this.endTime = t.getEndTime();
        this.taskPlanStatusId = t.getStatus().getId();
        this.createdAt = t.getCreatedAt();
        this.updatedAt = t.getUpdatedAt();
        if(t.getParentTask() != null){
            this.parentTaskId = t.getParentTask().getId();
        }
    }
}

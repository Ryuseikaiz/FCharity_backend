package fptu.fcharity.response.project;

import fptu.fcharity.entity.TaskPlan;
import fptu.fcharity.entity.TaskPlanStatus;
import fptu.fcharity.response.authentication.UserResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
public class TaskPlanResponse {
    private UUID id;
    private UUID phaseId;
    private UserResponse user;
    private String taskName;
    private String taskPlanDescription;
    private Instant startTime;
    private Instant endTime;
    private TaskPlanStatusResponse status;
    private Instant createdAt;
    private Instant updatedAt;
    private TaskPlanResponse parentTask;
    public TaskPlanResponse(TaskPlan t){
        this.id = t.getId();
        this.phaseId = t.getPhase().getId();
        if(t.getUser() != null){
            this.user = new UserResponse(t.getUser());
        }
        this.taskName = t.getTaskName();
        this.taskPlanDescription = t.getTaskPlanDescription();
        this.startTime = t.getStartTime();
        this.endTime = t.getEndTime();
        if(t.getStatus()!=null){
            this.status = new TaskPlanStatusResponse(t.getStatus());
        }
        this.createdAt = t.getCreatedAt();
        this.updatedAt = t.getUpdatedAt();
        if(t.getParentTask() != null){
            this.parentTask = new TaskPlanResponse(t.getParentTask());
        }
    }
}

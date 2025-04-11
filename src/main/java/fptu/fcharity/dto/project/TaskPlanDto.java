package fptu.fcharity.dto.project;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
public class TaskPlanDto {
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
}

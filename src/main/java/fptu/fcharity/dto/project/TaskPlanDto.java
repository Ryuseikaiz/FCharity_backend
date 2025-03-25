package fptu.fcharity.dto.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class TaskPlanDto {
    private UUID id;
    private UUID projectId;

    private UUID userId;

    private String taskName;

    private String taskPlanDescription;

    private Instant startTime;

    private Instant endTime;

    private String taskPlanStatus;

    private Instant createdAt;

    private Instant updatedAt;
}

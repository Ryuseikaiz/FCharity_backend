package fptu.fcharity.dto.project;

import fptu.fcharity.entity.TaskPlan;
import fptu.fcharity.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class SubTaskDto {
    private UUID id;

    private UUID taskPlanId;

    private String subTaskName;

    private UUID userId;

    private String subTaskDescription;

    private Instant startTime;

    private Instant endTime;

    private String subTaskStatus;

    private Instant createdAt;

    private Instant updatedAt;
}

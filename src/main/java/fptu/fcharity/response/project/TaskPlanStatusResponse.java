package fptu.fcharity.response.project;

import fptu.fcharity.entity.TaskPlanStatus;
import fptu.fcharity.entity.Timeline;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
public class TaskPlanStatusResponse {
    private UUID id;

    private String statusName;

    private TimelineResponse phase;
    public TaskPlanStatusResponse(TaskPlanStatus status) {
        this.id = status.getId();
        this.statusName = status.getStatusName();
        if (status.getPhase() != null) {
            this.phase = new TimelineResponse(status.getPhase());
        }
    }
}

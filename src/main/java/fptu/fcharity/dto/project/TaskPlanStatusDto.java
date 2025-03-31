package fptu.fcharity.dto.project;

import fptu.fcharity.entity.TaskPlanStatus;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.UUID;
@Getter
@Setter
public class TaskPlanStatusDto {
    private UUID id;
    private String statusName;

}

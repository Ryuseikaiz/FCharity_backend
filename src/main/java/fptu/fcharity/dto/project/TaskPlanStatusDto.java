package fptu.fcharity.dto.project;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class TaskPlanStatusDto {
    private UUID id;
    private String statusName;
    private UUID phaseId;
}

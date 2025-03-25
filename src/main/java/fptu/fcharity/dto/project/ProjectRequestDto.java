package fptu.fcharity.dto.project;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class ProjectRequestDto {
    private UUID id;
    private UUID userId;
    private UUID projectId;
}

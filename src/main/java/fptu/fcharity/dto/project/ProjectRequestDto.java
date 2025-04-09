package fptu.fcharity.dto.project;

import fptu.fcharity.entity.ProjectRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequestDto {
    private UUID id;
    private UUID userId;
    private UUID projectId;
    private String status;
    public ProjectRequestDto(ProjectRequest r) {
        this.id = r.getId();
        this.userId = r.getUser().getId();
        this.projectId = r.getProject().getId();
        this.status = r.getStatus();
    }
}

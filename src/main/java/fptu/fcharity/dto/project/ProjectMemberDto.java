package fptu.fcharity.dto.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ProjectMemberDto {
    private UUID id;
    private UUID userId;
    private UUID projectId;
    private String role;
    public ProjectMemberDto( UUID userId, UUID projectId, String role) {
        this.userId = userId;
        this.projectId = projectId;
        this.role = role;
    }
}

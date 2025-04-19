package fptu.fcharity.dto.project;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProjectMemberDto {
    private UUID id;
    private UUID userId;
    private UUID projectId;
    private String role;
}

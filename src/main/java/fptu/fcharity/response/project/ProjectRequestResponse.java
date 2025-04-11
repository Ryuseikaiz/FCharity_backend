package fptu.fcharity.response.project;

import fptu.fcharity.entity.ProjectRequest;
import fptu.fcharity.response.authentication.UserResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
public class ProjectRequestResponse {
    private UUID id;
    private UUID projectId;
    private String requestType;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;

    private UserResponse user;      // đã có
    private ProjectResponse project; // thêm cái này

    public ProjectRequestResponse(ProjectRequest entity) {
        this.id = entity.getId();
        this.projectId = entity.getProject().getId();
        this.requestType = entity.getRequestType();
        this.status = entity.getStatus();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();

        this.user = new UserResponse(entity.getUser());

        // map thêm nếu bạn có ProjectResponse
        this.project = new ProjectResponse(entity.getProject());
    }
}


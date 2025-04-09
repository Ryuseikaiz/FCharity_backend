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
        private UserResponse user;
        private UUID projectId;

        private String requestType;

        private String status;

        private Instant createdAt;

        private Instant updatedAt;
        public ProjectRequestResponse(ProjectRequest pr) {
            this.id = pr.getId();
            this.user = new UserResponse(pr.getUser());
            this.projectId = pr.getProject().getId();
            this.requestType = pr.getRequestType();
            this.status = pr.getStatus();
            this.createdAt = pr.getCreatedAt();
            this.updatedAt = pr.getUpdatedAt();
        }
}

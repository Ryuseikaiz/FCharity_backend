package fptu.fcharity.response.project;

import fptu.fcharity.entity.HelpRequest;
import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.ProjectConfirmationRequest;
import fptu.fcharity.response.request.HelpRequestResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
public class ProjectConfirmationRequestResponse {
    private UUID id;
    private ProjectResponse project;
    private UUID requestId;
    private String requestName;
    private Instant createdAt;
    private Boolean isConfirmed;
    private String note;
    public ProjectConfirmationRequestResponse(ProjectConfirmationRequest projectConfirmationRequest) {
        this.id = projectConfirmationRequest.getId();
        this.project = new ProjectResponse(projectConfirmationRequest.getProject());
        if(projectConfirmationRequest.getRequest() != null) {
            this.requestId = projectConfirmationRequest.getRequest().getId();
            this.requestName = projectConfirmationRequest.getRequest().getTitle();

        }
        this.createdAt = projectConfirmationRequest.getCreatedAt();
        this.isConfirmed = projectConfirmationRequest.getIsConfirmed();
        this.note = projectConfirmationRequest.getNote();
    }
}

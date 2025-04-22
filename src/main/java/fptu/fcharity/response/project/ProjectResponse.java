package fptu.fcharity.response.project;

import fptu.fcharity.entity.*;
import fptu.fcharity.response.authentication.UserResponse;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
public class ProjectResponse {
    private UUID id;

    private String projectName;

    private UUID organizationId;
    private String organizationName;

    private UUID requestId;

    private UserResponse leader;

    private String email;

    private String phoneNumber;

    private String projectDescription;
    private String projectStatus;

    private String location;

    private String reportFile;

    private Instant plannedStartTime;

    private Instant plannedEndTime;

    private Instant actualStartTime;

    private Instant actualEndTime;
    private Instant createdAt;

    private Instant updatedAt;

    private String shutdownReason;

    private UUID categoryId;
    private String categoryName;

    private UUID walletId;
    public ProjectResponse(Project project) {
        this.id = project.getId();
        this.projectName = project.getProjectName();
        this.organizationId = project.getOrganization().getOrganizationId();
        this.organizationName = project.getOrganization().getOrganizationName();
       if(project.getRequest()!= null){
           this.requestId = project.getRequest().getId();
       }
        this.leader = new UserResponse(project.getLeader());
        this.email = project.getEmail();
        this.phoneNumber = project.getPhoneNumber();
        this.projectDescription = project.getProjectDescription();
        this.projectStatus = project.getProjectStatus();
        this.location = project.getLocation();
        this.reportFile = project.getReportFile();
        this.plannedStartTime = project.getPlannedStartTime();
        this.plannedEndTime = project.getPlannedEndTime();
        this.actualStartTime = project.getActualStartTime();
        this.actualEndTime = project.getActualEndTime();
        this.shutdownReason = project.getShutdownReason();
        this.categoryId = project.getCategory().getId();
        this.categoryName = project.getCategory().getCategoryName();
        this.walletId = project.getWalletAddress().getId();
        this.createdAt = project.getCreatedAt();
        this.updatedAt = project.getUpdatedAt();
    }
}

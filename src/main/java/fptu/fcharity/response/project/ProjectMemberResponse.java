package fptu.fcharity.response.project;

import fptu.fcharity.entity.ProjectMember;
import fptu.fcharity.entity.User;
import fptu.fcharity.response.authentication.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
public class ProjectMemberResponse {
    private UUID id;

    private UserResponse user;

    private UUID projectId;

    private Instant joinDate;

    private Instant leaveDate;

    private String memberRole;
    public ProjectMemberResponse(ProjectMember projectMember) {
        this.id =projectMember.getId();
        this.user = new UserResponse(projectMember.getUser());
        this.projectId = projectMember.getProject().getId();
        this.joinDate = projectMember.getJoinDate();
        this.leaveDate = projectMember.getLeaveDate();
        this.memberRole = projectMember.getMemberRole();
    }
}

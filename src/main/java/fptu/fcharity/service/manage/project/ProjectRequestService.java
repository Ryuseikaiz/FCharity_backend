package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.ProjectMemberDto;
import fptu.fcharity.dto.project.ProjectRequestDto;
import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.ProjectRequest;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.project.ProjectRequestRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.response.project.ProjectRequestResponse;
import fptu.fcharity.service.HelpNotificationService;
import fptu.fcharity.service.manage.user.UserService;
import fptu.fcharity.utils.constants.project.ProjectMemberRole;
import fptu.fcharity.service.HelpNotificationService;
import fptu.fcharity.service.manage.user.UserService;
import fptu.fcharity.utils.constants.project.ProjectRequestStatus;
import fptu.fcharity.utils.constants.project.ProjectRequestType;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ProjectRequestService {
    @Autowired
    ProjectRequestRepository projectRequestRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ProjectService projectService;
    @Autowired
    private ProjectMemberService projectMemberService;

    @Autowired
    private HelpNotificationService notificationService;
    @Autowired
    private UserService userService;

    /*   private UUID id;
    private UUID userId;
    private UUID projectId;

    private String requestType;

    private String status;

    private Instant createdAt;

    private Instant updatedAt;
    *
    * */

    //***************COMMON ACTION***************

    public void takeObject(ProjectRequest pr, ProjectRequestDto prDto){

        if (prDto.getUserId() != null) {
            User user = userRepository.findWithDetailsById(prDto.getUserId() );
            pr.setUser(user);
        }
        if (prDto.getProjectId() != null) {
            Project project = projectRepository.findWithEssentialById(prDto.getProjectId());
            pr.setProject(project);
        }
    }
    public List<ProjectRequestResponse> getAllProjectRequests(UUID projectId){
        List<ProjectRequest> l = projectRequestRepository.findWithEssentialByProjectId(projectId);
        return l.stream().map(ProjectRequestResponse::new).toList();
    }
    public boolean checkCannotEditRequest(UUID id){
        ProjectRequest pr = projectRequestRepository.findWithEssentialById(id);
        return !pr.getStatus().equals(ProjectRequestStatus.PENDING);
    }
    public boolean checkExistingRequest(UUID userId, UUID projectId, String requestType){
        List<ProjectRequest> pr = projectRequestRepository.findExistingRequestByUserIdAndProjectId(userId, projectId,requestType);
        return !pr.isEmpty();
    }
    public void sendProjectRequest(ProjectRequest pr, String prRequestType){
        pr.setRequestType(prRequestType);
        pr.setStatus(ProjectRequestStatus.PENDING);
        pr.setCreatedAt(Instant.now());
    }
    public void cancelProjectRequest(ProjectRequest pr){
        pr.setStatus(ProjectRequestStatus.CANCELLED);
        pr.setUpdatedAt(Instant.now());
    }
    //hủy yêu cầu tham gia, yêu cầu rời nhoóm
    public ProjectRequestResponse cancelRequest(UUID requestId){
        ProjectRequest pr = projectRequestRepository.findWithEssentialById(requestId);
        if(checkCannotEditRequest(pr.getId())){
            throw new ApiRequestException("Request is already reviewed or cancelled");
        }
        cancelProjectRequest(pr);
        ProjectRequest p = projectRequestRepository.save(pr);
        return new ProjectRequestResponse(p);
    }
    //user review invitation
    //leader review join request
    public ProjectRequestResponse reviewJoinRequest(String decision, UUID id) {
        ProjectRequest pr =  projectRequestRepository.findWithEssentialById(id);
        if(checkCannotEditRequest(pr.getId())){
            throw new ApiRequestException("Request is already reviewed or cancelled");
        }
        String formattedDecision = decision.toUpperCase(Locale.ROOT);
        if (formattedDecision.equals(ProjectRequestStatus.APPROVED) || formattedDecision.equals(ProjectRequestStatus.REJECTED)) {
            pr.setStatus(formattedDecision);
            ProjectMemberDto pmDto = new ProjectMemberDto();
            pmDto.setProjectId(pr.getProject().getId());
            pmDto.setUserId(pr.getUser().getId());
            pmDto.setRole(ProjectMemberRole.MEMBER);
            if(formattedDecision.equals(ProjectRequestStatus.APPROVED)){
                projectMemberService.addProjectMember(pmDto);
            }
            // Kiểm tra xem người dùng hiện tại có phải là leader của dự án hay không
            User currentUser = userService.getCurrentUser();
            if (currentUser.getId().equals(pr.getProject().getLeader().getId())) {
                // Nếu người dùng hiện tại là leader, thông báo cho người dùng đã gửi yêu cầu
                notificationService.notifyUser(
                        pr.getUser(),
                        "Your invitation request has been " + formattedDecision.toLowerCase(),
                        null,
                        "Your invitation request to join the project \"" + pr.getProject().getProjectName() + "\" has been " + formattedDecision.toLowerCase() + ".",
                        "/manage-project/" + pr.getProject().getId() + "/home"
                );
            } else {
                // Nếu người dùng hiện tại là user và thực hiện quyết định, thông báo cho leader
                notificationService.notifyUser(
                        pr.getProject().getLeader(),
                        "User has responded to your invitation",
                        null,
                        "User \"" + pr.getUser().getFullName() + "\" has " + formattedDecision.toLowerCase() + " your invitation to join the project \"" + pr.getProject().getProjectName() + "\".",
                        "/manage-project/" + pr.getProject().getId() + "/home"
                );
            }
        } else {
            throw new ApiRequestException("Invalid action");
        }
        pr.setUpdatedAt(Instant.now());
        ProjectRequest p = projectRequestRepository.save(pr);
        return new ProjectRequestResponse(p);
    }
  //review leave request
  public ProjectRequestResponse reviewLeaveRequest(String decision, UUID id) {
      ProjectRequest pr =  projectRequestRepository.findWithEssentialById(id);
      if(checkCannotEditRequest(pr.getId())){
          throw new ApiRequestException("Request is already reviewed or cancelled");
      }
      String formattedDecision = decision.toUpperCase(Locale.ROOT);
      if (formattedDecision.equals(ProjectRequestStatus.APPROVED) || formattedDecision.equals(ProjectRequestStatus.REJECTED)) {
          pr.setStatus(formattedDecision);
          ProjectMemberDto pmDto = new ProjectMemberDto();
          pmDto.setProjectId(pr.getProject().getId());
          pmDto.setUserId(pr.getUser().getId());
          if(formattedDecision.equals(ProjectRequestStatus.APPROVED)){
              projectMemberService.removeProjectMember(pmDto);
          }
          // Kiểm tra xem người dùng hiện tại có phải là leader của dự án hay không
          User currentUser = userService.getCurrentUser();
          if (currentUser.getId().equals(pr.getProject().getLeader().getId())) {
              // Nếu người dùng hiện tại là leader, thông báo cho người dùng đã gửi yêu cầu
              notificationService.notifyUser(
                      pr.getUser(),
                      "Your leave request has been " + formattedDecision.toLowerCase(),
                      "USER",
                      "Your leave request from the project \"" + pr.getProject().getProjectName() + "\" has been " + formattedDecision.toLowerCase() + ".",
                      "/manage-project"
              );
          } else {
              // Nếu người dùng hiện tại là user và thực hiện quyết định, thông báo cho leader
              notificationService.notifyUser(
                      pr.getProject().getLeader(),
                      "User has responded to your leave request",
                      "LEADER",
                      "User \"" + pr.getUser().getFullName() + "\" has " + formattedDecision.toLowerCase() + " your leave request from the project \"" + pr.getProject().getProjectName() + "\".",
                      "/manage-project"
              );
          }
      } else {
          throw new ApiRequestException("Invalid action");
      }
      pr.setUpdatedAt(Instant.now());
      ProjectRequest p = projectRequestRepository.save(pr);
      return new ProjectRequestResponse(p);
  }
    //***************USER ACTION***************

    //gửi yêu cầu tham gia
    public ProjectRequestResponse sendJoinRequest(ProjectRequestDto prDto){
        if(checkExistingRequest(prDto.getUserId(), prDto.getProjectId(),ProjectRequestType.JOIN_REQUEST)){
            throw new ApiRequestException("Request already exists");
        }
        ProjectRequest pr = new ProjectRequest();
        sendProjectRequest(pr, ProjectRequestType.JOIN_REQUEST);
        takeObject(pr, prDto);
        ProjectRequest p = projectRequestRepository.save(pr);
        // Gửi thông báo cho leader của dự án
        User currentUser = userService.getCurrentUser();
        Project project = projectRepository.findWithEssentialById(prDto.getProjectId());
        User leader = project.getLeader();

        notificationService.notifyUser(
                leader,
                "New join request for your project",
                null,
                "User \"" + currentUser.getFullName() + "\" has requested to join your project \"" + project.getProjectName() + "\".",
                "/manage-project/" + project.getId() + "/members"
        );
       return new ProjectRequestResponse(p);
    }

    //gửi yêu cầu rời nhóm
    public ProjectRequestResponse sendLeaveRequest(ProjectRequestDto prDto){
        if(checkExistingRequest(prDto.getUserId(), prDto.getProjectId(),ProjectRequestType.LEAVE_REQUEST)){
            throw new ApiRequestException("Request already exists");
        }
        ProjectRequest pr = new ProjectRequest();
        sendProjectRequest(pr, ProjectRequestType.LEAVE_REQUEST);
        takeObject(pr, prDto);
        ProjectRequest p = projectRequestRepository.save(pr);
        return new ProjectRequestResponse(p);
    }

    //***************FOUNDER ACTION***************
    //gửi lời mời tham gia
    public ProjectRequestResponse sendJoinInvitation(ProjectRequestDto prDto){
        if(checkExistingRequest(prDto.getUserId(), prDto.getProjectId(),ProjectRequestType.INVITATION)){
            throw new ApiRequestException("Request already exists");
        }
        ProjectRequest pr = new ProjectRequest();
        sendProjectRequest(pr, ProjectRequestType.INVITATION);
        takeObject(pr, prDto);
        ProjectRequest p = projectRequestRepository.save(pr);
        User invitedUser = userRepository.findWithEssentialById(prDto.getUserId());
        Project project = projectRepository.findById(prDto.getProjectId()).orElseThrow(() -> new ApiRequestException("Project not found"));

        notificationService.notifyUser(
                invitedUser,
                "Invitation to Join Project",
                null,
                "You have received an invitation to join the project \"" + project.getProjectName() + "\".",
                "/user/manage-profile/invitations"
        );
        return new ProjectRequestResponse(p);
    }


}

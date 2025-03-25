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
import fptu.fcharity.utils.constants.project.ProjectRequestStatus;
import fptu.fcharity.utils.constants.project.ProjectRequestType;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    public ProjectRequest findProjectRequestByProjectIdAndUserId(UUID projectId, UUID userId){
        return projectRequestRepository.findByProjectIdAndUserId(projectId, userId);
    }
    public void takeObject(ProjectRequest pr, ProjectRequestDto prDto){
        if (prDto.getUserId() != null) {
            User user = userRepository.findWithDetailsById(prDto.getUserId() );
            pr.setUser(user);
        }
        if (prDto.getProjectId() != null) {
            Project project = projectRepository.findById(prDto.getProjectId())
                    .orElseThrow(() -> new ApiRequestException("ProjectRequest not found"));
            pr.setProject(project);
        }
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
    public ProjectRequestResponse cancelRequest(ProjectRequestDto prDto){
        ProjectRequest pr = findProjectRequestByProjectIdAndUserId(prDto.getProjectId(), prDto.getUserId());
        cancelProjectRequest(pr);
        takeObject(pr, prDto);
        ProjectRequest p = projectRequestRepository.save(pr);
        return new ProjectRequestResponse(p);
    }
    //user review invitation
    //leader review join request
    public ProjectRequestResponse reviewJoinRequest(String decision, ProjectRequestDto prDto) {
        ProjectRequest pr = findProjectRequestByProjectIdAndUserId(prDto.getProjectId(), prDto.getUserId());
        String formattedDecision = decision.toUpperCase(Locale.ROOT);
        if (formattedDecision.equals(ProjectRequestStatus.APPROVED) || formattedDecision.equals(ProjectRequestStatus.REJECTED)) {
            pr.setStatus(formattedDecision);
            ProjectMemberDto pmDto = new ProjectMemberDto();
            pmDto.setProjectId(prDto.getProjectId());
            pmDto.setUserId(prDto.getUserId());
            if(formattedDecision.equals(ProjectRequestStatus.APPROVED)){
                projectMemberService.addProjectMember(pmDto);
            }
        } else {
            throw new ApiRequestException("Invalid action");
        }
        pr.setUpdatedAt(Instant.now());
        takeObject(pr, prDto);
        ProjectRequest p = projectRequestRepository.save(pr);
        return new ProjectRequestResponse(p);
    }
  //review leave request
  public ProjectRequestResponse reviewLeaveRequest(String decision, ProjectRequestDto prDto) {
      ProjectRequest pr = findProjectRequestByProjectIdAndUserId(prDto.getProjectId(), prDto.getUserId());
      String formattedDecision = decision.toUpperCase(Locale.ROOT);
      if (formattedDecision.equals(ProjectRequestStatus.APPROVED) || formattedDecision.equals(ProjectRequestStatus.REJECTED)) {
          pr.setStatus(formattedDecision);
          ProjectMemberDto pmDto = new ProjectMemberDto();
          pmDto.setProjectId(prDto.getProjectId());
          pmDto.setUserId(prDto.getUserId());
          if(formattedDecision.equals(ProjectRequestStatus.APPROVED)){
              projectMemberService.removeProjectMember(pmDto);
          }
      } else {
          throw new ApiRequestException("Invalid action");
      }
      pr.setUpdatedAt(Instant.now());
      takeObject(pr, prDto);
      ProjectRequest p = projectRequestRepository.save(pr);
      return new ProjectRequestResponse(p);
  }
    //***************USER ACTION***************

    //gửi yêu cầu tham gia
    public ProjectRequestResponse sendJoinRequest(ProjectRequestDto prDto){
        ProjectRequest pr = new ProjectRequest();
        sendProjectRequest(pr, ProjectRequestType.JOIN_REQUEST);
        takeObject(pr, prDto);
        ProjectRequest p = projectRequestRepository.save(pr);
       return new ProjectRequestResponse(p);
    }

    //gửi yêu cầu rời nhóm
    public ProjectRequestResponse sendLeaveRequest(ProjectRequestDto prDto){
        ProjectRequest pr = new ProjectRequest();
        sendProjectRequest(pr, ProjectRequestType.LEAVE_REQUEST);
        takeObject(pr, prDto);
        ProjectRequest p = projectRequestRepository.save(pr);
        return new ProjectRequestResponse(p);
    }

    //***************FOUNDER ACTION***************
    //gửi lời mời tham gia
    public ProjectRequestResponse sendJoinInvitation(ProjectRequestDto prDto){
        ProjectRequest pr = new ProjectRequest();
        sendProjectRequest(pr, ProjectRequestType.INVITATION);
        takeObject(pr, prDto);
        ProjectRequest p = projectRequestRepository.save(pr);
        return new ProjectRequestResponse(p);
    }

}

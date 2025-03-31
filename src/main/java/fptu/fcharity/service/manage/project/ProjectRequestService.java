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
            if(formattedDecision.equals(ProjectRequestStatus.APPROVED)){
                projectMemberService.addProjectMember(pmDto);
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
        return new ProjectRequestResponse(p);
    }


}

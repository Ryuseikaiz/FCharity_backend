package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.ProjectMemberDto;
import fptu.fcharity.entity.Category;
import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.ProjectMember;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.manage.project.ProjectMemberRepository;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.response.project.ProjectMemberResponse;
import fptu.fcharity.utils.constants.ObjectType;
import fptu.fcharity.utils.constants.ProjectMemberRole;
import fptu.fcharity.utils.constants.RequestStatus;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Service
public class ProjectMemberService {
    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    public void takeObject(ProjectMember projectMember,ProjectMemberDto projectMemberDto){
        if (projectMemberDto.getUserId() != null) {
            User user = userRepository.findWithDetailsById(projectMemberDto.getUserId() );
            user.getPassword();
            projectMember.setUser(user);
        }
        if (projectMemberDto.getProjectId() != null) {
            Project project = projectRepository.findById(projectMemberDto.getProjectId())
                    .orElseThrow(() -> new ApiRequestException("Không tìm thấy Project"));
            projectMember.setProject(project);
        }
    }
    public List<ProjectMemberResponse> getMembersOfProject(UUID projectId) {
        List<ProjectMember> projectMembers = projectMemberRepository.findByProjectId(projectId);
        return projectMembers.stream()
                .filter(pr-> pr.getMemberRole().equals(ProjectMemberRole.MEMBER))
                .map(ProjectMemberResponse::new)
                .toList();
    }
    //để mời/ thêm thành viên vào dự án
    public ProjectMemberResponse addMemberToProject(ProjectMemberDto projectMemberDto) {
      List<ProjectMember> membershipHistory = projectMemberRepository.findByProjectIdAndUserUserId(projectMemberDto.getProjectId(),projectMemberDto.getUserId());
        ProjectMember pm = membershipHistory.stream().filter(projectMember -> projectMember.getLeaveDate() == null).findFirst().orElse(null);
      if(pm != null){
          if(pm.getMemberRole().equals(ProjectMemberRole.MEMBER)){
              throw new ApiRequestException("Already a member of this project");
          }else{
              throw new ApiRequestException("Already sent invitation to this user");
          }
      }
      ProjectMember projectMember = new ProjectMember();
        projectMember.setMemberRole(projectMemberDto.getRole());
        projectMember.setJoinDate(Instant.now());
        takeObject(projectMember,projectMemberDto);
        projectMemberRepository.save(projectMember);
        return new ProjectMemberResponse(projectMember);
    }
    //đồng ý yêu cầu tham gia
    public void reviewInvitation(UUID memberId,String objectType,String decision) {
        if(objectType.equals(ObjectType.PROJECT)){
            ProjectMember member = projectMemberRepository.findById(memberId).orElseThrow(() -> new ApiRequestException("Member not found"));
            if(decision.equals(RequestStatus.APPROVED)) {
                member.setMemberRole(ProjectMemberRole.MEMBER);
                projectMemberRepository.save(member);
            }else{
                projectMemberRepository.delete(member);
            }
        }
    }
    //rời nhóm
    public ProjectMemberResponse moveOutFromProject(UUID id) {
        ProjectMember projectMember = projectMemberRepository.findWithEssentialById(id);
        projectMember.setLeaveDate(Instant.now());
        projectMemberRepository.save(projectMember);
        return new ProjectMemberResponse(projectMember);
    }
    //để xóa thành viên khỏi dự án
    public void removeMemberFromProject(UUID memberId) {
        ProjectMember projectMember = projectMemberRepository.findById(memberId).orElseThrow(() -> new ApiRequestException("Không tìm thấy thành viên"));
        projectMemberRepository.delete(projectMember);
    }


}

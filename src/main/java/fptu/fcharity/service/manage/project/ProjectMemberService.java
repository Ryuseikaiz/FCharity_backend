package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.ProjectMemberDto;
import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.ProjectMember;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.manage.project.ProjectMemberRepository;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.response.project.ProjectMemberResponse;
import fptu.fcharity.utils.constants.project.ProjectMemberRole;
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
            User user = userRepository.findWithEssentialById(projectMemberDto.getUserId() );
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
                .map(ProjectMemberResponse::new)
                .toList();
    }
    public List<ProjectMemberResponse> getActiveMembersOfProject(UUID id) {
        List<ProjectMember> projectMembers = projectMemberRepository.findByProjectId(id);
        return projectMembers.stream()
                .filter(pr-> !pr.getMemberRole().equals(ProjectMemberRole.LEADER) && pr.getLeaveDate()==null)
                .map(ProjectMemberResponse::new)
                .toList();
    }
    public ProjectMemberResponse updateRoleOfProjectMember(ProjectMemberDto projectMemberDto) {
        ProjectMember projectMember = projectMemberRepository.findWithEssentialById(projectMemberDto.getId());
        if (projectMember == null) {
            throw new ApiRequestException("Không tìm thấy thành viên trong dự án");
        }
        projectMember.setMemberRole(projectMemberDto.getRole());
        projectMemberRepository.save(projectMember);
        return new ProjectMemberResponse(projectMember);
    }
    //thêm thành viên
    public ProjectMemberResponse addProjectMember(ProjectMemberDto projectMemberDto) {
        ProjectMember projectMember = new ProjectMember();
        projectMember.setJoinDate(Instant.now());
        projectMember.setMemberRole(projectMemberDto.getRole());
        takeObject(projectMember, projectMemberDto);
        projectMemberRepository.save(projectMember);
        return new ProjectMemberResponse(projectMember);
    }

    //rời nhóm
    public void removeProjectMember(ProjectMemberDto projectMemberDto) {
        ProjectMember projectMember = projectMemberRepository.findByProjectIdAndUserId(projectMemberDto.getProjectId(), projectMemberDto.getUserId());
        projectMember.setLeaveDate(Instant.now());
        projectMemberRepository.save(projectMember);
    }
    public ProjectMemberResponse removeProjectMemberById(UUID id) {
        ProjectMember projectMember = projectMemberRepository.findWithEssentialById(id);
        projectMember.setLeaveDate(Instant.now());
        projectMemberRepository.save(projectMember);
        return new ProjectMemberResponse(projectMember);
    }

    public ProjectMemberResponse removeProjectMemberCompletely(UUID memberId) {
        ProjectMember projectMember = projectMemberRepository.findWithEssentialById(memberId);
        projectMemberRepository.delete(projectMember);
        return new ProjectMemberResponse(projectMember);
    }
}

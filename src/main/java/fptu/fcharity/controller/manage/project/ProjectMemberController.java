package fptu.fcharity.controller.manage.project;

import fptu.fcharity.dto.project.ProjectMemberDto;
import fptu.fcharity.response.project.ProjectMemberResponse;
import fptu.fcharity.service.manage.project.ProjectMemberService;
import fptu.fcharity.utils.constants.project.ProjectMemberRole;
import fptu.fcharity.utils.constants.project.ProjectRequestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects/members")
public class ProjectMemberController {
    @Autowired
    private ProjectMemberService projectMemberService;
    //--OKAY
    @GetMapping("/{projectId}")
    public ResponseEntity<?> getMembersOfProject(@PathVariable UUID projectId) {
        List<ProjectMemberResponse> projectMembers = projectMemberService.getMembersOfProject(projectId);
        return ResponseEntity.ok(projectMembers);
    }
    //--OKAY
    @GetMapping("/{projectId}/active")
    public ResponseEntity<?> getActiveMembersOfProject(@PathVariable UUID projectId) {
       List<ProjectMemberResponse> projectMembers = projectMemberService.getActiveMembersOfProject(projectId);
        return ResponseEntity.ok(projectMembers);
    }
    @PostMapping("/move-out/{memberId}")
    public ResponseEntity<?> moveOutProject(@PathVariable UUID memberId ) {
        ProjectMemberResponse pmr = projectMemberService.removeProjectMemberById(memberId);
        return ResponseEntity.ok(pmr);
    }
    @PostMapping("/remove/{memberId}")
    public ResponseEntity<?> removeProjectMember(@PathVariable UUID memberId ) {
        return ResponseEntity.ok(projectMemberService.removeProjectMemberCompletely(memberId));
    }
    @PostMapping("/add-member/{projectId}/{userId}/{role}")
    public ResponseEntity<?> addMemberProject(@PathVariable UUID projectId ,@PathVariable UUID userId,@PathVariable String role ) {
        ProjectMemberDto pmDto = new ProjectMemberDto();
        pmDto.setProjectId(projectId);
        pmDto.setUserId(userId);
        pmDto.setRole(role.toUpperCase());
        ProjectMemberResponse pmr = projectMemberService.addProjectMember(pmDto);
        return ResponseEntity.ok(pmr);
    }
}

package fptu.fcharity.controller.manage.project;

import fptu.fcharity.dto.project.ProjectMemberDto;
import fptu.fcharity.entity.ProjectMember;
import fptu.fcharity.response.project.ProjectMemberResponse;
import fptu.fcharity.service.manage.project.ProjectMemberService;
import fptu.fcharity.utils.constants.ObjectType;
import fptu.fcharity.utils.constants.ProjectMemberRole;
import fptu.fcharity.utils.constants.RequestStatus;
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
    @GetMapping("/{projectId}")
    public ResponseEntity<?> getMembersOfProject(@PathVariable UUID projectId) {
       List<ProjectMemberResponse> projectMembers = projectMemberService.getMembersOfProject(projectId);
        return ResponseEntity.ok(projectMembers);
    }
    @PostMapping("/add")
    public ResponseEntity<?> addMemberToProject(@RequestBody ProjectMemberDto projectMemberDto) {
      projectMemberDto.setRole(ProjectMemberRole.MEMBER);
       ProjectMemberResponse p =  projectMemberService.addMemberToProject(projectMemberDto);
       return ResponseEntity.ok(p);
    }
    @PostMapping("/invite")
    public ResponseEntity<?> inviteMemberToProject(@RequestBody ProjectMemberDto projectMemberDto) {
        projectMemberDto.setRole(ProjectMemberRole.SUGGESTED);
        ProjectMemberResponse p =  projectMemberService.addMemberToProject(projectMemberDto);
        return ResponseEntity.ok(p);
    }
    @PostMapping("/remove/{memberId}")
    public ResponseEntity<?> removeMemberFromProject(@PathVariable UUID memberId) {
        projectMemberService.removeMemberFromProject(memberId);
        return ResponseEntity.ok("Delete successful!");
    }
    @PostMapping("/invitation/{memberId}/approved")
    public ResponseEntity<?> approveInvitation(@PathVariable UUID memberId ) {
        projectMemberService.reviewInvitation(memberId, ObjectType.PROJECT, RequestStatus.APPROVED);
        return ResponseEntity.ok("done review!");
    }
    @PostMapping("/invitation/{memberId}/rejected")
    public ResponseEntity<?> rejectInvitation(@PathVariable UUID memberId ) {
        projectMemberService.reviewInvitation(memberId, ObjectType.PROJECT, RequestStatus.REJECTED);
        return ResponseEntity.ok("done review!");
    }
    @PostMapping("/move-out/{memberId}")
    public ResponseEntity<?> moveOutProject(@PathVariable UUID memberId ) {
        ProjectMemberResponse pmr = projectMemberService.moveOutFromProject(memberId);
        return ResponseEntity.ok(pmr);
    }
}

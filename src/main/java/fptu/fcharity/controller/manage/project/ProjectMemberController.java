package fptu.fcharity.controller.manage.project;

import fptu.fcharity.response.project.ProjectMemberResponse;
import fptu.fcharity.service.manage.project.ProjectMemberService;
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
    @PostMapping("/move-out/{memberId}")
    public ResponseEntity<?> moveOutProject(@PathVariable UUID memberId ) {
        ProjectMemberResponse pmr = projectMemberService.removeProjectMemberById(memberId);
        return ResponseEntity.ok(pmr);
    }
}

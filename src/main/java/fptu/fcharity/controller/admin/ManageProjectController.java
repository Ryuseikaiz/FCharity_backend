package fptu.fcharity.controller.admin;

import fptu.fcharity.dto.admindashboard.ProjectDTO;
import fptu.fcharity.service.admin.ManageProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
public class ManageProjectController {
    private final ManageProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectService.getProjectById(projectId));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable UUID projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok("Project deleted successfully.");
    }

    @PutMapping("/approve/{projectId}")
    public ResponseEntity<String> approveProject(@PathVariable UUID projectId) {
        projectService.approveProject(projectId);
        return ResponseEntity.ok("Project has been approved successfully.");
    }

    @PutMapping("/hide/{projectId}")
    public ResponseEntity<String> hideProject(@PathVariable UUID projectId) {
        projectService.hideProject(projectId);
        return ResponseEntity.ok("Project has been hidden successfully.");
    }

}

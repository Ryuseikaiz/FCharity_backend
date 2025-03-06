package fptu.fcharity.controller;

import fptu.fcharity.dto.admindashboard.ProjectDTO;
import fptu.fcharity.service.ManageProjectService;
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

    // Lấy danh sách dự án
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    // Lấy dự án theo ID
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectService.getProjectById(projectId));
    }

    // Xóa dự án
    @DeleteMapping("/{projectId}")
    public ResponseEntity<String> deleteProject(@PathVariable UUID projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok("Project deleted successfully.");
    }

    // Duyệt dự án (Chuyển từ PENDING → ACTIVE)
    @PutMapping("/{projectId}/approve")
    public ResponseEntity<String> approveProject(@PathVariable UUID projectId) {
        projectService.approveProject(projectId);
        return ResponseEntity.ok("Project has been approved successfully.");
    }
}

package fptu.fcharity.controller.manage.project;

import fptu.fcharity.dto.project.ProjectDto;
import fptu.fcharity.response.project.ProjectFinalResponse;
import fptu.fcharity.service.manage.project.ProjectService;
import fptu.fcharity.utils.mapper.ProjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService, ProjectMapper projectMapper) {
        this.projectService = projectService;
    }
    @GetMapping
    public ResponseEntity< List<ProjectFinalResponse>> getAllProjects() {
        List<ProjectFinalResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProjectFinalResponse> getProjectById(@PathVariable UUID id) {
        ProjectFinalResponse project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }
    @GetMapping("/my-owner-project/{userId}")
    public ResponseEntity<ProjectFinalResponse> getMyOwnerProject(@PathVariable UUID userId) {
        ProjectFinalResponse project = projectService.getMyOwnerProject(userId);
        return ResponseEntity.ok(project);
    }
    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestBody ProjectDto projectDto) {
        ProjectFinalResponse newProject =  projectService.createProject(projectDto);
        return ResponseEntity.ok(newProject);
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateProject(@RequestBody ProjectDto projectDto) {
        ProjectFinalResponse newProject =  projectService.updateProject(projectDto);
        return ResponseEntity.ok(newProject);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}

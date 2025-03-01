package fptu.fcharity.controller;

import fptu.fcharity.entity.Project;
import fptu.fcharity.dto.project.ProjectDto;
import fptu.fcharity.service.manageproject.ProjectService;
import fptu.fcharity.utils.mapper.ProjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService, ProjectMapper projectMapper) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<Project> getProjectById(@PathVariable UUID id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }
    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestBody ProjectDto projectDto) {
             Project newProject =  projectService.createProject(projectDto);
        return ResponseEntity.ok(newProject);
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateProject(@RequestBody ProjectDto projectDto) {
        Project newProject =  projectService.updateProject(projectDto);
        return ResponseEntity.ok(newProject);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}

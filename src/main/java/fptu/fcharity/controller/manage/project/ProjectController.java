package fptu.fcharity.controller.manage.project;

import fptu.fcharity.dto.project.ProjectDto;
import fptu.fcharity.dto.project.ProjectNeedDonateDto;
import fptu.fcharity.entity.Wallet;
import fptu.fcharity.helpers.schedule.ScheduleService;
import fptu.fcharity.response.project.ProjectFinalResponse;
import fptu.fcharity.service.WalletService;
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
    private final ScheduleService scheduleService;
    private final WalletService walletService;

    public ProjectController(ProjectService projectService, WalletService walletService, ScheduleService scheduleService) {
        this.projectService = projectService;
        this.scheduleService = scheduleService;
        this.walletService = walletService;
    }
    @GetMapping
    public ResponseEntity< List<ProjectFinalResponse>> getAllProjects() {
        List<ProjectFinalResponse> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }
    @GetMapping("/need-donate")
    public ResponseEntity<?> getAllProjectsNeedDonating() {
        List<ProjectFinalResponse> projects = projectService.getAllProjectsNeedDonating();
        return ResponseEntity.ok(projects);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProjectFinalResponse> getProjectById(@PathVariable UUID id) {
        ProjectFinalResponse project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }
    @GetMapping("/my-project/{userId}")
    public ResponseEntity<List<ProjectFinalResponse>> getMyProject(@PathVariable UUID userId) {
        List<ProjectFinalResponse> project = projectService.getMyProject(userId);
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
    @GetMapping("org/{orgId}")
    public ResponseEntity<List<ProjectFinalResponse>> getProjectByOrgId(@PathVariable UUID orgId) {
        List<ProjectFinalResponse> projects = projectService.getProjectByOrgId(orgId);
        return ResponseEntity.ok(projects);
    }
    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<Wallet> getProjectWalletByWalletId(@PathVariable UUID walletId) {
        Wallet wallet = walletService.getById(walletId);
        return ResponseEntity.ok(wallet);
    }

}

package fptu.fcharity.controller.manage.project;

import fptu.fcharity.response.project.ProjectConfirmationRequestResponse;
import fptu.fcharity.service.manage.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/projects/confirmation-requests")
@RequiredArgsConstructor
public class ConfirmRequestController {
    private final ProjectService projectService;

    @GetMapping("/confirm/{id}")
    public ResponseEntity<ProjectConfirmationRequestResponse> getRequest(@PathVariable UUID id) {
        var result = projectService.getConfirmationRequestById(id);
        return ResponseEntity.ok(result);
    }
    @PostMapping("/{projectId}")
    public ResponseEntity<ProjectConfirmationRequestResponse> createRequest(@PathVariable UUID projectId) {
        var result = projectService.createProjectConfirmationRequest(projectId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ProjectConfirmationRequestResponse> getByProject(@PathVariable UUID projectId) {
        var result = projectService.getConfirmationRequestOfProject(projectId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/request/{requestId}")
    public ResponseEntity<ProjectConfirmationRequestResponse> getByRequest(@PathVariable UUID requestId) {
        var result = projectService.getConfirmationRequestOfRequest(requestId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ProjectConfirmationRequestResponse> confirmRequest(@PathVariable UUID id) {
        var result = projectService.confirmProjectConfirmationRequest(id);
        return ResponseEntity.ok(result);
    }
    @PutMapping("/{id}/reject")
    public ResponseEntity<ProjectConfirmationRequestResponse> rejectRequest(@PathVariable UUID id,@RequestBody Map<String,String> message) {
        var result = projectService.rejectProjectConfirmationRequest(id, message.get("message"));
        return ResponseEntity.ok(result);
    }

}



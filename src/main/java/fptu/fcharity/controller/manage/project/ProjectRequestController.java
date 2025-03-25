package fptu.fcharity.controller.manage.project;

import fptu.fcharity.dto.project.ProjectRequestDto;
import fptu.fcharity.response.project.ProjectRequestResponse;
import fptu.fcharity.service.manage.project.ProjectRequestService;
import fptu.fcharity.utils.constants.project.ProjectRequestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/projects/requests")
public class ProjectRequestController {
    @Autowired
    ProjectRequestService projectRequestService;

    //*************COMMON ACTION*************
    //hủy: yêu cầu vào, yêu cầu ra
    //hủy: lời mời
    @PostMapping("/{projectId}/{userId}/cancel")
    public ResponseEntity<?> cancelRequest(@PathVariable UUID projectId,@PathVariable UUID userId) {
        ProjectRequestDto prDto = new ProjectRequestDto();
        prDto.setProjectId(projectId);
        prDto.setUserId(userId);
        ProjectRequestResponse prr = projectRequestService.cancelRequest(prDto);
        return ResponseEntity.ok(prr);
    }
    //review: yêu cầu mời vào
    //review: yêu cầu vào cua user
    @PostMapping("/{projectId}/{userId}/review-join-request/{decision}")
    public ResponseEntity<?> reviewJoinRequest(@PathVariable UUID projectId,@PathVariable UUID userId,@PathVariable String decision) {
        ProjectRequestDto prDto = new ProjectRequestDto();
        prDto.setProjectId(projectId);
        prDto.setUserId(userId);
        String formattedDecision = decision.toUpperCase(Locale.ROOT);
        if (formattedDecision.equals(ProjectRequestStatus.APPROVED) || formattedDecision.equals(ProjectRequestStatus.REJECTED)) {
            ProjectRequestResponse prr = projectRequestService.reviewJoinRequest(formattedDecision,prDto);
            return ResponseEntity.ok(prr);
        }
        return ResponseEntity.badRequest().body("Invalid decision value");
    }
    //review: yêu cầu ra cua user
    @PostMapping("/{projectId}/{userId}/review-leave-request/{decision}")
    public ResponseEntity<?> reviewInvitation(@PathVariable UUID projectId,@PathVariable UUID userId,@PathVariable String decision) {
        ProjectRequestDto prDto = new ProjectRequestDto();
        prDto.setProjectId(projectId);
        prDto.setUserId(userId);
        String formattedDecision = decision.toUpperCase(Locale.ROOT);
        if (formattedDecision.equals(ProjectRequestStatus.APPROVED) || formattedDecision.equals(ProjectRequestStatus.REJECTED)) {
            ProjectRequestResponse prr = projectRequestService.reviewLeaveRequest(formattedDecision,prDto);
            return ResponseEntity.ok(prr);
        }
        return ResponseEntity.badRequest().body("Invalid decision value");
    }

    //*************USER ACTION*************
   //gửi: yêu cầu vào, yêu cầu ra
    @PostMapping("/{projectId}/join")
    public ResponseEntity<?> sendJoinRequest(@PathVariable UUID projectId,@RequestBody ProjectRequestDto prDto) {
        prDto.setProjectId(projectId);
        ProjectRequestResponse prr = projectRequestService.sendJoinRequest(prDto);
        return ResponseEntity.ok(prr);
    }
    @PostMapping("/{projectId}/leave")
    public ResponseEntity<?> sendLeaveRequest(@PathVariable UUID projectId,@RequestBody ProjectRequestDto prDto) {
        prDto.setProjectId(projectId);
        ProjectRequestResponse prr = projectRequestService.sendLeaveRequest(prDto);
        return ResponseEntity.ok(prr);
    }

    //*************FOUNDER ACTION*************
    //gửi: lời mời vào
    @PostMapping("/{projectId}/invite")
    public ResponseEntity<?> sendInvitation(@PathVariable UUID projectId,@RequestBody ProjectRequestDto prDto) {
        prDto.setProjectId(projectId);
        ProjectRequestResponse prr = projectRequestService.sendJoinInvitation(prDto);
        return ResponseEntity.ok(prr);
    }

}

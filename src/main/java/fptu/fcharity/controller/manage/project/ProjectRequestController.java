package fptu.fcharity.controller.manage.project;

import fptu.fcharity.dto.project.ProjectRequestDto;
import fptu.fcharity.entity.ProjectRequest;
import fptu.fcharity.response.project.ProjectRequestResponse;
import fptu.fcharity.service.manage.project.ProjectRequestService;
import fptu.fcharity.utils.constants.project.ProjectRequestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/projects/requests")
public class ProjectRequestController {
    @Autowired
    ProjectRequestService projectRequestService;

    //*************COMMON ACTION*************
    //get all reques--OKAY
    @GetMapping("/{projectId}")
    public ResponseEntity<?> getAllRequest(@PathVariable UUID projectId) {
        List<ProjectRequestResponse> l = projectRequestService.getAllProjectRequests(projectId);
        return ResponseEntity.ok(l);
    }
    //hủy: yêu cầu vào, yêu cầu ra ---OKAY
    //hủy: lời mời---OKAY
    @PostMapping("/{requestId}/cancel")
    public ResponseEntity<?> cancelRequest(@PathVariable UUID requestId) {
        ProjectRequestResponse prr = projectRequestService.cancelRequest(requestId);
        return ResponseEntity.ok(prr);
    }
    //review: yêu cầu mời vào ---OKAY
    //review: yêu cầu vào cua user ---OKAY
    @PostMapping("/{requestId}/approve-join")
    public ResponseEntity<?> approveJoinRequest(@PathVariable UUID requestId) {
            ProjectRequestResponse prr = projectRequestService.reviewJoinRequest(ProjectRequestStatus.APPROVED,requestId);
            return ResponseEntity.ok(prr);
    }
    @PostMapping("/{requestId}/reject-join")
    public ResponseEntity<?> rejectJoinRequest(@PathVariable UUID requestId) {
            ProjectRequestResponse prr = projectRequestService.reviewJoinRequest(ProjectRequestStatus.REJECTED,requestId);
            return ResponseEntity.ok(prr);
    }
    //review: yeu cau ra cua user --OKAY 2/2
    @PostMapping("/{requestId}/approve-move-out")
    public ResponseEntity<?> approveMoveOut(@PathVariable UUID requestId) {
        ProjectRequestResponse prr = projectRequestService.reviewLeaveRequest(ProjectRequestStatus.APPROVED,requestId);
        return ResponseEntity.ok(prr);
    }
    @PostMapping("/{requestId}/reject-move-out")
    public ResponseEntity<?> rejectMoveOut(@PathVariable UUID requestId) {
        ProjectRequestResponse prr = projectRequestService.reviewLeaveRequest(ProjectRequestStatus.REJECTED,requestId);
        return ResponseEntity.ok(prr);
    }

    //*************USER ACTION*************
   //gửi: yêu cầu vào, yêu cầu ra ---OKAY 2/2
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
    //gửi: lời mời vào ---OKAY
    @PostMapping("/{projectId}/invite")
    public ResponseEntity<?> sendInvitation(@PathVariable UUID projectId,@RequestBody ProjectRequestDto prDto) {
        prDto.setProjectId(projectId);
        ProjectRequestResponse prr = projectRequestService.sendJoinInvitation(prDto);
        return ResponseEntity.ok(prr);
    }

}

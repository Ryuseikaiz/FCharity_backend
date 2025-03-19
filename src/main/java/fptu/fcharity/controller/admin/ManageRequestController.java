package fptu.fcharity.controller.admin;

import fptu.fcharity.dto.request.RequestDto;
import fptu.fcharity.service.admin.ManageRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/requests")
@RequiredArgsConstructor
public class ManageRequestController {
    private final ManageRequestService manageRequestService;

    @GetMapping
    public ResponseEntity<List<RequestDto>> getAllRequests() {
        return ResponseEntity.ok(manageRequestService.getAllRequests());
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDto> getRequestById(@PathVariable UUID requestId) {
        return ResponseEntity.ok(manageRequestService.getRequestById(requestId));
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<String> deleteRequest(@PathVariable UUID requestId) {
        manageRequestService.deleteRequest(requestId);
        return ResponseEntity.ok("Request deleted successfully.");
    }

    @PutMapping("/approve/{requestId}")
    public ResponseEntity<String> approveRequest(@PathVariable UUID requestId) {
        manageRequestService.approveRequest(requestId);
        return ResponseEntity.ok("Request has been approved successfully.");
    }

    @PutMapping("/hide/{requestId}")
    public ResponseEntity<String> hideRequest(@PathVariable UUID requestId) {
        manageRequestService.hideRequest(requestId);
        return ResponseEntity.ok("Request has been hidden successfully.");
    }
    //Set Status thành REJECTED
    @PutMapping("/reject/{requestId}")
    public ResponseEntity<String> rejectRequest(@PathVariable UUID requestId) {
        manageRequestService.rejectRequest(requestId);
        return ResponseEntity.ok("Request has been rejected successfully.");
    }
}

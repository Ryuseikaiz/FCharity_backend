package fptu.fcharity.controller.manage.organization;

import fptu.fcharity.dto.organization.OrganizationRequestDTO;

import fptu.fcharity.entity.OrganizationRequest;
import fptu.fcharity.service.manage.organization.request.OrganizationRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class OrganizationRequestRestController {
    private final OrganizationRequestService organizationRequestService;

    @Autowired
    public OrganizationRequestRestController(OrganizationRequestService organizationRequestService) {
        this.organizationRequestService = organizationRequestService;
    }

    // Lấy tất cả danh sách yêu cầu tham gia lời mời (thống kê)
    @GetMapping("/join-invitation-requests")
    public List<OrganizationRequestDTO> getAllJoinInvitationRequests() {
        return organizationRequestService.getAllJoinInvitationRequests();
    }

    // Lấy danh sách yêu cần tham gia tới một tổ chức có Id
    @GetMapping("/join-requests/organizations/{organizationId}")
    public List<OrganizationRequestDTO> getAllJoinRequestsByOrganizationId(@PathVariable UUID organizationId) {
        return organizationRequestService.getAllJoinRequestsByOrganizationId(organizationId);
    }

    // Lấy tất cả yêu cầu tham gia mà người Id đã gửi tới các tổ chức
    @GetMapping("/join-requests/users/{userId}")
    public List<OrganizationRequestDTO> getAllJoinRequestsByUserId(@PathVariable UUID userId) {
        return organizationRequestService.getAllJoinRequestsByUserId(userId);
    }

    // Lấy thông tin yêu cầu tham gia của một request có Id
    @GetMapping("/join-requests/{joinRequestId}")
    public OrganizationRequestDTO getJoinRequestById(@PathVariable UUID joinRequestId) {
        return organizationRequestService.getJoinRequestById(joinRequestId);
    }

    // Tạo yêu cầu tham gia tới một tổ chức
    @PostMapping("/join-requests/{userId}/{organizationId}")
    public OrganizationRequestDTO createJoinRequest( @PathVariable UUID userId, @PathVariable UUID organizationId) {
        System.out.println("createJoinRequest ⚓⚓⚓");
        return organizationRequestService.createJoinRequest(userId, organizationId);
    }

    // Chấp nhận yêu cầu tham gia
    @PutMapping("/join-requests/{joinRequestId}/accept")
    public OrganizationRequestDTO acceptJoinRequest(@PathVariable UUID joinRequestId) {
        return organizationRequestService.acceptJoinRequest(joinRequestId);
    }

    // Từ chối yêu cầu tham gia
    @PutMapping("/join-requests/{joinRequestId}/reject")
    public OrganizationRequestDTO rejectJoinRequest(@PathVariable UUID joinRequestId) {
        return organizationRequestService.rejectJoinRequest(joinRequestId);
    }

    // Xóa yêu cầu tham gia
    @DeleteMapping("/join-requests/{joinRequestId}/cancel")
    public UUID cancelJoinRequest(@PathVariable UUID joinRequestId) {
        organizationRequestService.cancelJoinRequest(joinRequestId);
        return joinRequestId;
    }

    // Lấy tất cả lời mời mà tổ chức đã gửi tới các cá nhân
    @GetMapping("/invitation-requests/organizations/{organizationId}")
    public List<OrganizationRequestDTO> getAllInvitationRequestsByOrganizationId(@PathVariable UUID organizationId) {
        return organizationRequestService.getAllInvitationRequestsByOrganizationId(organizationId);
    }

    // Lấy tất cả lời mời tới một người dùng Id
    @GetMapping("/invitation-requests/users/{userId}")
    public ResponseEntity<List<OrganizationRequestDTO>> getAllInvitationRequestsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(organizationRequestService.getAllInvitationRequestsByUserId(userId));
    }

    // Lấy thông tin yêu cầu tham gia có Id
    @GetMapping("/invitation-requests/{invitationRequestId}")
    public OrganizationRequestDTO getInvitationRequestById(@PathVariable("invitationRequestId") UUID requestId) {
        return organizationRequestService.getInvitationRequestById(requestId);
    }

    // Tạo lời mời tham gia từ tổ chức tới cá nhân
    @PostMapping("/invitation-requests/{organizationId}/{userId}")  // In progress
    public OrganizationRequestDTO createInvitationRequest(@PathVariable UUID organizationId, @PathVariable UUID userId) {
        return organizationRequestService.createInvitationRequest(organizationId, userId);
    }

    // Chấp nhận lời mời tham gia
    @PutMapping("/invitation-requests/{invitationRequestId}/accept")
    public OrganizationRequestDTO acceptInvitationRequest(@PathVariable UUID invitationRequestId) {
        return organizationRequestService.acceptInvitationRequest(invitationRequestId);
    }

    // Từ chối lời mời tham gia
    @PutMapping("/invitation-requests/{invitationRequestId}/reject")
    public OrganizationRequestDTO rejectInvitationRequest(@PathVariable UUID invitationRequestId) {
        return organizationRequestService.rejectInvitationRequest(invitationRequestId);
    }

    // Hủy lời mời tham gia
    @DeleteMapping("/invitation-requests/{invitationRequestId}/cancel")
    public UUID cancelInvitationRequest(@PathVariable UUID invitationRequestId) {
        organizationRequestService.cancelInvitationRequest(invitationRequestId);
        return invitationRequestId;
    }
}

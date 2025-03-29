package fptu.fcharity.controller.manage.organization;

import fptu.fcharity.dto.request.OrganizationRequestDto;

import fptu.fcharity.entity.OrganizationRequest;
import fptu.fcharity.service.manage.organization.OrganizationMemberService;
import fptu.fcharity.service.manage.organization.OrganizationService;
import fptu.fcharity.service.manage.organization.request.OrganizationRequestService;
import fptu.fcharity.service.manage.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class OrganizationRequestRestController {
    private final OrganizationRequestService OrganizationRequestService;
    private final OrganizationMemberService organizationMemberService;
    private final OrganizationService organizationService;
    private final UserService userService;

    @Autowired
    public OrganizationRequestRestController(OrganizationRequestService OrganizationRequestService, OrganizationMemberService organizationMemberService, UserService userService, OrganizationService organizationService) {
        this.OrganizationRequestService = OrganizationRequestService;
        this.organizationMemberService = organizationMemberService;
        this.userService = userService;
        this.organizationService = organizationService;
    }


    @PostMapping("/join-requests")
    public OrganizationRequest createJoinRequest(@RequestBody OrganizationRequestDto OrganizationRequestDto) {
        return OrganizationRequestService.createJoinRequest(OrganizationRequestDto);
    }

    @PutMapping("/join-requests")
    public OrganizationRequest updateJoinRequest(@RequestBody OrganizationRequestDto OrganizationRequestDto) {
        return OrganizationRequestService.updateJoinRequest(OrganizationRequestDto);
    }

    @DeleteMapping("/join-requests/{joinRequestId}")
    public void deleteJoinRequest(@PathVariable UUID joinRequestId) {
        OrganizationRequestService.deleteJoinRequest(joinRequestId);
    }

    @GetMapping("/join-requests")
    public List<OrganizationRequest> getAllJoinRequests() {
        return OrganizationRequestService.getAllJoinRequests();
    }

    @GetMapping("/join-requests/organizations/{organizationId}")
    public List<OrganizationRequest> getJoinRequestsByOrganizationId(@PathVariable("organizationId") UUID organizationId) {
        System.out.println("getJoinRequestsByOrganizationId " + organizationId);
        return OrganizationRequestService.getAllJoinRequestsByOrganizationId(organizationId);
    }

    @GetMapping("/join-requests/{requestId}")
    public Optional<OrganizationRequest> getJoinRequest(@PathVariable("requestId") UUID requestId) {
        return OrganizationRequestService.getJoinRequestById(requestId);
    }

    @GetMapping("/join-requests/users/{userId}")
    public List<OrganizationRequest> getJoinRequestsByUserId(@PathVariable("userId") UUID userId) {
        return OrganizationRequestService.getAllJoinRequestsByUserId(userId);
    }

    @GetMapping("/invite-requests/organizations/{organizationId}")
    public List<OrganizationRequest> getInviteRequestsByOrganizationId(@PathVariable("organizationId") UUID organizationId) {
        return OrganizationRequestService.getAllInviteRequestsByOrganizationId(organizationId);
    }

    @PostMapping("/invite-requests")
    public ResponseEntity<OrganizationRequest> createInviteRequest(@RequestBody OrganizationRequestDto OrganizationRequestDto) {
        OrganizationRequest OrganizationRequest = OrganizationRequestService.createInviteRequest(OrganizationRequestDto);
        return ResponseEntity.ok(OrganizationRequest);
    }

    @PutMapping("/invite-requests")
    public ResponseEntity<OrganizationRequest> updateInviteRequest(@RequestBody OrganizationRequestDto OrganizationRequestDto) {
        return ResponseEntity.ok(OrganizationRequestService.updateInviteRequest(OrganizationRequestDto));
    }

    @DeleteMapping("/invite-requests/{inviteRequestId}")
    public void deleteInviteRequest(@PathVariable("inviteRequestId") UUID inviteRequestId) {
        OrganizationRequestService.deleteInviteRequest(inviteRequestId);
    }

}

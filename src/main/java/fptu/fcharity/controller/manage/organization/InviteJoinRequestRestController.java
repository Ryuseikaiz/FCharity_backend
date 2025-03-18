package fptu.fcharity.controller.manage.organization;

import fptu.fcharity.dto.request.InviteJoinRequestDto;
import fptu.fcharity.entity.InviteJoinRequest;
import fptu.fcharity.entity.OrganizationMember;

import fptu.fcharity.entity.User;
import fptu.fcharity.service.organization.OrganizationMemberService;
import fptu.fcharity.service.organization.OrganizationService;
import fptu.fcharity.service.request.InviteJoinRequestService;
import fptu.fcharity.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class InviteJoinRequestRestController {
    private final InviteJoinRequestService inviteJoinRequestService;
    private final OrganizationMemberService organizationMemberService;
    private final OrganizationService organizationService;
    private final UserService userService;

    @Autowired
    public InviteJoinRequestRestController(InviteJoinRequestService inviteJoinRequestService, OrganizationMemberService organizationMemberService, UserService userService, OrganizationService organizationService) {
        this.inviteJoinRequestService = inviteJoinRequestService;
        this.organizationMemberService = organizationMemberService;
        this.userService = userService;
        this.organizationService = organizationService;
    }

    @PostMapping("/join-requests")
    public InviteJoinRequest createJoinRequest(@RequestBody InviteJoinRequestDto inviteJoinRequestDto) {
        return inviteJoinRequestService.createJoinRequest(inviteJoinRequestDto);
    }

    @PutMapping("/join-requests")
    public InviteJoinRequest updateJoinRequest(@RequestBody InviteJoinRequestDto inviteJoinRequestDto) {
        return inviteJoinRequestService.updateJoinRequest(inviteJoinRequestDto);
    }

    @DeleteMapping("/join-requests/{joinRequestId}")
    public void deleteJoinRequest(@PathVariable UUID joinRequestId) {
        inviteJoinRequestService.deleteJoinRequest(joinRequestId);
    }

    @GetMapping("/join-requests")
    public List<InviteJoinRequest> getAllJoinRequests() {
        return inviteJoinRequestService.getAllJoinRequests();
    }

    @GetMapping("/join-requests/organizations/{organization_id}")
    public List<InviteJoinRequest> getJoinRequestsByOrganizationId(@PathVariable("organization_id") UUID organizationId) {
        System.out.println("getJoinRequestsByOrganizationId " + organizationId);
        return inviteJoinRequestService.getAllJoinRequestsByOrganizationId(organizationId);
    }

    @GetMapping("/join-requests/{request_id}")
    public Optional<InviteJoinRequest> getJoinRequest(@PathVariable("request_id") UUID request_id) {
        return inviteJoinRequestService.getJoinRequestById(request_id);
    }

    @GetMapping("/join-requests/users/{user_id}")
    public List<InviteJoinRequest> getJoinRequestsByUserId(@PathVariable("user_id") UUID user_id) {
        return inviteJoinRequestService.getAllJoinRequestsByUserId(user_id);
    }

    @GetMapping("/invite-requests/organizations/{organization_id}")
    public List<InviteJoinRequest> getInviteRequestsByOrganizationId(@PathVariable("organization_id") UUID organizationId) {
        return inviteJoinRequestService.getAllInviteRequestsByOrganizationId(organizationId);
    }

    @PostMapping("/invite-requests")
    public ResponseEntity<InviteJoinRequest> createInviteRequest(@RequestBody InviteJoinRequestDto inviteJoinRequestDto) {
        InviteJoinRequest inviteJoinRequest = inviteJoinRequestService.createInviteRequest(inviteJoinRequestDto);
        return ResponseEntity.ok(inviteJoinRequest);
    }

    @PutMapping("/invite-requests")
    public ResponseEntity<InviteJoinRequest> updateInviteRequest(@RequestBody InviteJoinRequestDto inviteJoinRequestDto) {
        return ResponseEntity.ok(inviteJoinRequestService.updateInviteRequest(inviteJoinRequestDto));
    }

    @DeleteMapping("/invite-requests/{inviteRequest_id}")
    public void deleteInviteRequest(@PathVariable("inviteRequest_id") UUID inviteRequestId) {
        inviteJoinRequestService.deleteInviteRequest(inviteRequestId);
    }

}

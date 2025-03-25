package fptu.fcharity.controller.manage.request;

import fptu.fcharity.dto.request.OrganizationRequestDto;

import fptu.fcharity.entity.HelpRequest;
import fptu.fcharity.entity.OrganizationRequest;
import fptu.fcharity.service.organization.OrganizationMemberService;
import fptu.fcharity.service.organization.OrganizationService;
import fptu.fcharity.service.request.OrganizationRequestService;
import fptu.fcharity.service.request.RequestService;
import fptu.fcharity.service.user.UserService;
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
    private final RequestService requestService;

    @Autowired
    public OrganizationRequestRestController(OrganizationRequestService OrganizationRequestService, OrganizationMemberService organizationMemberService, UserService userService, OrganizationService organizationService, RequestService requestService) {
        this.OrganizationRequestService = OrganizationRequestService;
        this.organizationMemberService = organizationMemberService;
        this.userService = userService;
        this.organizationService = organizationService;
        this.requestService = requestService;
    }

    @GetMapping("/requests")
    public List<HelpRequest> getRequests() {
        return requestService.getAll();
    }

    @GetMapping("/requests/{request_id}")
    public Optional<HelpRequest> getRequest(@PathVariable UUID request_id) {
        return requestService.getById(request_id);
    }

    @PostMapping("/requests")
    public HelpRequest createRequest(@RequestBody HelpRequest request) {
        return requestService.save(request);
    }

    @PutMapping("/requests")
    public HelpRequest updateRequest(@RequestBody HelpRequest request) {
        return requestService.update(request);
    }

    @DeleteMapping("/requests/{request_id}")
    public void deleteRequest(@PathVariable UUID request_id) {
        requestService.delete(request_id);
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

    @GetMapping("/join-requests/organizations/{organization_id}")
    public List<OrganizationRequest> getJoinRequestsByOrganizationId(@PathVariable("organization_id") UUID organizationId) {
        System.out.println("getJoinRequestsByOrganizationId " + organizationId);
        return OrganizationRequestService.getAllJoinRequestsByOrganizationId(organizationId);
    }

    @GetMapping("/join-requests/{request_id}")
    public Optional<OrganizationRequest> getJoinRequest(@PathVariable("request_id") UUID request_id) {
        return OrganizationRequestService.getJoinRequestById(request_id);
    }

    @GetMapping("/join-requests/users/{user_id}")
    public List<OrganizationRequest> getJoinRequestsByUserId(@PathVariable("user_id") UUID user_id) {
        return OrganizationRequestService.getAllJoinRequestsByUserId(user_id);
    }

    @GetMapping("/invite-requests/organizations/{organization_id}")
    public List<OrganizationRequest> getInviteRequestsByOrganizationId(@PathVariable("organization_id") UUID organizationId) {
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

    @DeleteMapping("/invite-requests/{inviteRequest_id}")
    public void deleteInviteRequest(@PathVariable("inviteRequest_id") UUID inviteRequestId) {
        OrganizationRequestService.deleteInviteRequest(inviteRequestId);
    }

}

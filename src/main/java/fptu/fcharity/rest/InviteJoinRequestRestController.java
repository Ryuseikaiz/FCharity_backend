package fptu.fcharity.rest;

import fptu.fcharity.dto.request.InviteJoinRequestDto;
import fptu.fcharity.entity.InviteJoinRequest;
import fptu.fcharity.entity.OrganizationMember;

import fptu.fcharity.entity.User;
import fptu.fcharity.service.organization.OrganizationMemberService;
import fptu.fcharity.service.organization.OrganizationService;
import fptu.fcharity.service.request.InviteJoinRequestService;
import fptu.fcharity.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public InviteJoinRequest updateJoinRequest(@RequestBody InviteJoinRequest inviteJoinRequest) {
        System.out.println(inviteJoinRequest);
        if (inviteJoinRequestService.getInviteRequestById(inviteJoinRequest.getInviteJoinRequestId()).isPresent()) {
            System.out.println(inviteJoinRequest + " already exists");
            switch (inviteJoinRequest.getStatus()) {
                case "Approved":
                    System.out.println("created new organization member: ");

                    OrganizationMember newMember = new OrganizationMember();
                    newMember.setUser(inviteJoinRequest.getUser());
                    newMember.setOrganization(organizationService.getById(inviteJoinRequest.getOrganizationId()));
                    System.out.println("New member: " + newMember);
                    System.out.println("After creating: ");
                    System.out.println(organizationMemberService.save(newMember));
                    break;
                case "Rejected":
                    break;
                default:
                    break;
            }
        }
        return inviteJoinRequestService.updateJoinRequest(inviteJoinRequest);
    }

    @DeleteMapping("/join-requests")
    public void deleteJoinRequest(@RequestBody InviteJoinRequest joinRequest) {
        inviteJoinRequestService.deleteJoinRequest(joinRequest);
    }

    @GetMapping("/join-requests")
    public List<InviteJoinRequest> getAllJoinRequests() {
        return inviteJoinRequestService.getAllJoinRequests();
    }

    @GetMapping("/join-requests/organizations/{organization_id}")
    public List<InviteJoinRequest> getJoinRequestsByOrganizationId(@PathVariable("organization_id") UUID organization_id) {
        System.out.println("getJoinRequestsByOrganizationId " + organization_id);
        return inviteJoinRequestService.getAllJoinRequestsByOrganizationId(organization_id);
    }

    @GetMapping("/invite-requests/organizations/{organization_id}")
    public List<InviteJoinRequest> getInviteRequestsByOrganizationId(@PathVariable("organization_id") UUID organization_id) {
        System.out.println("getJoinRequestsByOrganizationId " + organization_id);
        return inviteJoinRequestService.getAllInviteRequestsByOrganizationId(organization_id);
    }

    @GetMapping("/join-requests/{request_id}")
    public Optional<InviteJoinRequest> getJoinRequest(@PathVariable("request_id") UUID request_id) {
        return inviteJoinRequestService.getJoinRequestById(request_id);
    }

    @GetMapping("/join-requests/users/{user_id}")
    public List<InviteJoinRequest> getJoinRequestsByUserId(@PathVariable("user_id") UUID user_id) {
        return inviteJoinRequestService.getAllJoinRequestsByUserId(user_id);
    }
}

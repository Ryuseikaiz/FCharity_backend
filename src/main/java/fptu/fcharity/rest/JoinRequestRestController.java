package fptu.fcharity.rest;

import fptu.fcharity.entity.JoinRequest;
import fptu.fcharity.service.request.JoinRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class JoinRequestRestController {
    private final JoinRequestService joinRequestService;

    @Autowired
    public JoinRequestRestController(JoinRequestService joinRequestService) {
        this.joinRequestService = joinRequestService;
    }

    @PostMapping("/join-requests")
    public JoinRequest joinRequest(@RequestBody JoinRequest joinRequest) {
        return joinRequestService.createJoinRequest(joinRequest);
    }

    @PutMapping("/join-requests")
    public JoinRequest updateJoinRequest(@RequestBody JoinRequest joinRequest) {
        return joinRequestService.updateJoinRequest(joinRequest);
    }

    @DeleteMapping("/join-requests")
    public void deleteJoinRequest(@RequestBody JoinRequest joinRequest) {
        joinRequestService.deleteJoinRequest(joinRequest);
    }

    @GetMapping("/join-requests")
    public List<JoinRequest> getAllJoinRequests() {
        return joinRequestService.getAllJoinRequests();
    }

    @GetMapping("/join-requests/organizations/{organization_id}")
    public List<JoinRequest> getJoinRequestsByOrganizationId(@PathVariable("organization_id") UUID organization_id) {
        System.out.println("getJoinRequestsByOrganizationId " + organization_id);
        return joinRequestService.getAllJoinRequestsByOrganizationId(organization_id);
    }

    @GetMapping("/join-requests/{request_id}")
    public Optional<JoinRequest> getJoinRequest(@PathVariable("request_id") UUID request_id) {
        return joinRequestService.getJoinRequestById(request_id);
    }

    @GetMapping("/join-requests/users/{user_id}")
    public List<JoinRequest> getJoinRequestsByUserId(@PathVariable("user_id") UUID user_id) {
        return joinRequestService.getAllJoinRequestsByUserId(user_id);
    }
}

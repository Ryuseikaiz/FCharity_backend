package fptu.fcharity.service.request;

import fptu.fcharity.entity.JoinRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JoinRequestService {
    JoinRequest createJoinRequest(JoinRequest joinRequest);
    JoinRequest updateJoinRequest(JoinRequest joinRequest);
    void deleteJoinRequest(JoinRequest joinRequest);

    List<JoinRequest> getAllJoinRequests();
    List<JoinRequest> getAllJoinRequestsByOrganizationId(UUID organizationId);
    Optional<JoinRequest> getJoinRequestById(UUID id);
    List<JoinRequest> getAllJoinRequestsByUserId(UUID userId);

}

package fptu.fcharity.dao;

import fptu.fcharity.entity.JoinRequest;

import java.util.List;
import java.util.UUID;

public interface JoinRequestDAO {
    JoinRequest createJoinRequest(JoinRequest joinRequest);
    JoinRequest updateJoinRequest(JoinRequest joinRequest);
    void deleteJoinRequest(JoinRequest joinRequest);

    List<JoinRequest> getAllJoinRequests();
    List<JoinRequest> getAllJoinRequestsByOrganizationId(UUID organizationId);
    JoinRequest getJoinRequestById(UUID id);
    List<JoinRequest> getAllJoinRequestsByUserId(UUID userId);

}

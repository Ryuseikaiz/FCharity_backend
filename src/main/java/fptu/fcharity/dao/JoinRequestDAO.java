package fptu.fcharity.dao;

import fptu.fcharity.entity.InviteJoinRequest;

import java.util.List;
import java.util.UUID;

public interface JoinRequestDAO {
    InviteJoinRequest createJoinRequest(InviteJoinRequest joinRequest);
    InviteJoinRequest updateJoinRequest(InviteJoinRequest joinRequest);
    void deleteJoinRequest(InviteJoinRequest joinRequest);

    List<InviteJoinRequest> getAllJoinRequests();
    List<InviteJoinRequest> getAllJoinRequestsByOrganizationId(UUID organizationId);
    InviteJoinRequest getJoinRequestById(UUID id);
    List<InviteJoinRequest> getAllJoinRequestsByUserId(UUID userId);

}

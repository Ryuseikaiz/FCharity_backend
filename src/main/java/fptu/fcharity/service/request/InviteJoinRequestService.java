package fptu.fcharity.service.request;

import fptu.fcharity.dto.request.InviteJoinRequestDto;
import fptu.fcharity.entity.InviteJoinRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InviteJoinRequestService {
    InviteJoinRequest createJoinRequest(InviteJoinRequestDto inviteJoinRequestDto);
    InviteJoinRequest updateJoinRequest(InviteJoinRequestDto inviteJoinRequestDto);
    void deleteJoinRequest(UUID joinRequestId);

    List<InviteJoinRequest> getAllJoinRequests();
    List<InviteJoinRequest> getAllJoinRequestsByOrganizationId(UUID organizationId);
    Optional<InviteJoinRequest> getJoinRequestById(UUID id);
    List<InviteJoinRequest> getAllJoinRequestsByUserId(UUID userId);

    Optional<InviteJoinRequest> getInviteRequestById(UUID id);
    List<InviteJoinRequest> getAllInviteRequestsByOrganizationId(UUID organizationId);
    InviteJoinRequest createInviteRequest(InviteJoinRequestDto inviteJoinRequestDto);
    InviteJoinRequest updateInviteRequest(InviteJoinRequestDto inviteJoinRequestDto);
    void deleteInviteRequest(UUID id);
}

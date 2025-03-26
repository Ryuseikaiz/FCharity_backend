package fptu.fcharity.service.request;

import fptu.fcharity.dto.request.OrganizationRequestDto;
import fptu.fcharity.entity.OrganizationRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRequestService {
    OrganizationRequest createJoinRequest(OrganizationRequestDto organizationRequestDto);
    OrganizationRequest updateJoinRequest(OrganizationRequestDto organizationRequestDto);
    void deleteJoinRequest(UUID joinRequestId);

    List<OrganizationRequest> getAllJoinRequests();
    List<OrganizationRequest> getAllJoinRequestsByOrganizationId(UUID organizationId);
    Optional<OrganizationRequest> getJoinRequestById(UUID id);
    List<OrganizationRequest> getAllJoinRequestsByUserId(UUID userId);

    Optional<OrganizationRequest> getInviteRequestById(UUID id);
    List<OrganizationRequest> getAllInviteRequestsByOrganizationId(UUID organizationId);
    OrganizationRequest createInviteRequest(OrganizationRequestDto organizationRequestDto);
    OrganizationRequest updateInviteRequest(OrganizationRequestDto organizationRequestDto);
    void deleteInviteRequest(UUID id);
}

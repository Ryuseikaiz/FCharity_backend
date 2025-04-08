package fptu.fcharity.service.manage.organization.request;

import fptu.fcharity.dto.organization.OrganizationRequestDTO;
import fptu.fcharity.dto.request.OrganizationRequestDto;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationMember;
import fptu.fcharity.entity.OrganizationRequest;
import fptu.fcharity.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRequestService {
    List<OrganizationRequest> getAllJoinInvitationRequests();
    List<OrganizationRequestDTO> getAllJoinRequestsByOrganizationId(UUID organizationId);
    List<OrganizationRequest> getAllJoinRequestsByUserId(UUID userId);
    Optional<OrganizationRequest> getJoinRequestById(UUID id);
    OrganizationRequest createJoinRequest(OrganizationRequestDto organizationRequestDto);
    OrganizationRequest acceptJoinRequest(UUID joinRequestId);
    OrganizationRequest rejectJoinRequest(UUID joinRequestId);
    void cancelJoinRequest(UUID joinRequestId);

    List<OrganizationRequestDTO> getAllInvitationRequestsByOrganizationId(UUID organizationId);
    List<OrganizationRequest> getAllInvitationRequestsByUserId(UUID userId);
    Optional<OrganizationRequest> getInvitationRequestById(UUID id);
    OrganizationRequestDTO createInvitationRequest(UUID organizationId, UUID userId);
    OrganizationRequest acceptInvitationRequest(UUID invitationRequestId);
    OrganizationRequest rejectInvitationRequest(UUID invitationRequestId);
    void cancelInvitationRequest(UUID invitationRequestId);
}

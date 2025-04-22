package fptu.fcharity.service.manage.organization.request;

import fptu.fcharity.dto.organization.OrganizationRequestDTO;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationMember;
import fptu.fcharity.entity.OrganizationRequest;
import fptu.fcharity.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRequestService {
    List<OrganizationRequestDTO> getAllJoinInvitationRequests();
    List<OrganizationRequestDTO> getAllJoinRequestsByOrganizationId(UUID organizationId);
    List<OrganizationRequestDTO> getAllJoinRequestsByUserId(UUID userId);
    OrganizationRequestDTO getJoinRequestById(UUID id);
    OrganizationRequestDTO createJoinRequest(UUID userId, UUID organizationId);
    OrganizationRequestDTO acceptJoinRequest(UUID joinRequestId);
    OrganizationRequestDTO rejectJoinRequest(UUID joinRequestId);
    void cancelJoinRequest(UUID joinRequestId);

    List<OrganizationRequestDTO> getAllInvitationRequestsByOrganizationId(UUID organizationId);
    List<OrganizationRequestDTO> getAllInvitationRequestsByUserId(UUID userId);
    OrganizationRequestDTO getInvitationRequestById(UUID id);
    OrganizationRequestDTO createInvitationRequest(UUID organizationId, UUID userId);
    OrganizationRequestDTO acceptInvitationRequest(UUID invitationRequestId);
    OrganizationRequestDTO rejectInvitationRequest(UUID invitationRequestId);
    void cancelInvitationRequest(UUID invitationRequestId);
}
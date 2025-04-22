package fptu.fcharity.repository.manage.organization;

import fptu.fcharity.entity.OrganizationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizationRequestRepository extends JpaRepository<OrganizationRequest, UUID> {
    List<OrganizationRequest> findByOrganizationOrganizationIdAndRequestType(UUID organizationId, OrganizationRequest.OrganizationRequestType requestType);

    List<OrganizationRequest> findByUserIdAndRequestType(UUID userId, OrganizationRequest.OrganizationRequestType requestType);

    OrganizationRequest findByOrganizationOrganizationIdAndUserId(UUID organizationId, UUID userId);

    List<OrganizationRequest> findByOrganizationOrganizationIdAndStatusAndRequestType(UUID organizationId, OrganizationRequest.OrganizationRequestStatus status, OrganizationRequest.OrganizationRequestType requestType);

    List<OrganizationRequest> findByUserIdAndStatusAndRequestType(UUID userId, OrganizationRequest.OrganizationRequestStatus status, OrganizationRequest.OrganizationRequestType requestType);

    OrganizationRequest findByOrganizationRequestId(UUID inviteJoinRequestId);

    OrganizationRequest findByUserIdAndOrganizationOrganizationId(UUID userId, UUID organizationId);

    OrganizationRequest findByUserIdAndOrganizationOrganizationIdAndRequestType(UUID userUserId, UUID organizationOrganizationId, OrganizationRequest.OrganizationRequestType requestType);
}
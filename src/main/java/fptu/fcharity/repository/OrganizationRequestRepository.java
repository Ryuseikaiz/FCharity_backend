package fptu.fcharity.repository;

import fptu.fcharity.entity.OrganizationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizationRequestRepository extends JpaRepository<OrganizationRequest, UUID> {
    List<OrganizationRequest> findByOrganizationOrganizationIdAndRequestType(UUID organizationId, OrganizationRequest.OrganizationRequestType requestType);

    List<OrganizationRequest> findByUserUserIdAndRequestType(UUID userId, OrganizationRequest.OrganizationRequestType requestType);

    List<OrganizationRequest> findByOrganizationOrganizationIdAndStatusAndRequestType(UUID organizationId, OrganizationRequest.OrganizationRequestStatus status, OrganizationRequest.OrganizationRequestType requestType);

    List<OrganizationRequest> findByUserUserIdAndStatusAndRequestType(UUID userId, OrganizationRequest.OrganizationRequestStatus status, OrganizationRequest.OrganizationRequestType requestType);

    OrganizationRequest findByOrganizationRequestId(UUID inviteJoinRequestId);

    OrganizationRequest findByUserUserIdAndOrganizationOrganizationId(UUID userId, UUID organizationId);
}

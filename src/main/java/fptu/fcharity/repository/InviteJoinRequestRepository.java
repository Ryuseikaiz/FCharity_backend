package fptu.fcharity.repository;

import fptu.fcharity.entity.InviteJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InviteJoinRequestRepository extends JpaRepository<InviteJoinRequest, UUID> {
    List<InviteJoinRequest> findByOrganizationIdAndRequestType(UUID organizationId, InviteJoinRequest.RequestType requestType);

    List<InviteJoinRequest> findByUserUserIdAndRequestType(UUID userId, InviteJoinRequest.RequestType requestType);

    List<InviteJoinRequest> findByOrganizationIdAndStatusAndRequestType(UUID organizationId, InviteJoinRequest.RequestStatus status, InviteJoinRequest.RequestType requestType);

    List<InviteJoinRequest> findByUserUserIdAndStatusAndRequestType(UUID userId, InviteJoinRequest.RequestStatus status, InviteJoinRequest.RequestType requestType);

    InviteJoinRequest findByInviteJoinRequestId(UUID inviteJoinRequestId);

    InviteJoinRequest findByUserUserIdAndOrganizationId(UUID userId, UUID organizationId);
}

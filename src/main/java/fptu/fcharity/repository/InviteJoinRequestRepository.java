package fptu.fcharity.repository;

import fptu.fcharity.entity.InviteJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InviteJoinRequestRepository extends JpaRepository<InviteJoinRequest, UUID> {
    List<InviteJoinRequest> findByOrganizationIdAndRequestType(UUID organizationId, String requestType);

    List<InviteJoinRequest> findByUserUserIdAndRequestType(UUID userId, String requestType);

    List<InviteJoinRequest> findByOrganizationIdAndStatusAndRequestType(UUID organizationId, String status, String requestType);

    List<InviteJoinRequest> findByUserUserIdAndStatusAndRequestType(UUID userId, String status, String requestType);

}

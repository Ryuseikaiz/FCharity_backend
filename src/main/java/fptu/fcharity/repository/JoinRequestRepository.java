package fptu.fcharity.repository;

import fptu.fcharity.entity.JoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JoinRequestRepository extends JpaRepository<JoinRequest, UUID> {
    List<JoinRequest> findByOrganizationId(UUID organizationId);
    List<JoinRequest> findByUserId(UUID userId);
}

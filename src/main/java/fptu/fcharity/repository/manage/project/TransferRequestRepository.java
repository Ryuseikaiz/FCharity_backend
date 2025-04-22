package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.TransferRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransferRequestRepository extends JpaRepository<TransferRequest, UUID> {
    @EntityGraph(attributePaths = {"project", "request"})
    TransferRequest findByRequestId(UUID id);
    // Define any custom query methods if needed
    // For example:
    // List<TransferRequest> findByProjectId(UUID projectId);
}

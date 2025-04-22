package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.ProjectConfirmationRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectConfirmationRequestRepository extends JpaRepository<ProjectConfirmationRequest, UUID> {
    // Define any custom query methods if needed
    // For example:
    // List<ProjectConfirmationRequest> findByProjectId(UUID projectId);
    @EntityGraph(attributePaths = {"project", "request"})
    ProjectConfirmationRequest findByRequestId(UUID requestId);
}

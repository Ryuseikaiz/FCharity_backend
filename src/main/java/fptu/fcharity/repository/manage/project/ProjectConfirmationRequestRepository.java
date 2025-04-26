package fptu.fcharity.repository.manage.project;

import fptu.fcharity.entity.ProjectConfirmationRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ProjectConfirmationRequestRepository extends JpaRepository<ProjectConfirmationRequest, UUID> {
    // Define any custom query methods if needed
    // For example:
    // List<ProjectConfirmationRequest> findByProjectId(UUID projectId);
    @EntityGraph(attributePaths = {"project", "request"})
    @Query("SELECT pcr FROM ProjectConfirmationRequest pcr JOIN FETCH pcr.request WHERE pcr.request.id = :requestId")
    ProjectConfirmationRequest findByRequestId(UUID requestId);
    @EntityGraph(attributePaths = {"project", "request"})
    @Query("SELECT pcr FROM ProjectConfirmationRequest pcr JOIN FETCH pcr.project WHERE pcr.project.id = :projectId")
    ProjectConfirmationRequest findByProjectId(UUID projectId);
}
